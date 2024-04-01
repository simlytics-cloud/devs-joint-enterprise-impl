package iso.example.store.impl.storeexperimentalframe.storecoupledmodel;

import com.google.common.collect.ImmutableList;
import devs.msg.Bag;
import devs.msg.PortValue;
import devs.msg.time.LongSimTime;
import iso.example.store.immutables.*;
import iso.example.store.storeexperimentalframe.storecoupledmodel.ModifiableStoreModelState;
import iso.example.store.storeexperimentalframe.storecoupledmodel.StoreModel;
import iso.example.store.storeexperimentalframe.storecoupledmodel.StoreModelProperties;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class StoreModelImpl extends StoreModel {
    public StoreModelImpl(ModifiableStoreModelState initialState, String identifier, StoreModelProperties properties) {
        super(initialState, identifier, properties);
    }

    @Override
    protected void processCustomer(Customer customer) {
        Customer finalCustomer = customer;
        ProductStock stockage = modelState.getProductstock().stream()
                .filter(ps -> ps.getProduct().getName().equals(finalCustomer.getProduct().getName()))
                .findFirst().orElseThrow();
        if (stockage.getStockLevel() > 0) {
            ProductStock updatedStock = stockage.withStockLevel(stockage.getStockLevel() - 1);
            modelState.getProductstock().remove(stockage);
            modelState.getProductstock().add(updatedStock);
            modelState.addPendingCustomerDepartureOut(customer.withPurchasedProduct(true));

        } else {
            modelState.addPendingCustomerDepartureOut(customer.withPurchasedProduct(false));
        }

    }

    @Override
    protected Order ordering() {
        List<ProductRequest> requests = new ArrayList<>();
        for (ProductStock stock: modelState.getProductstock()) {
            if (stock.getStockLevel() <= 0) {
                requests.add(ProductRequest.builder()
                        .product(stock.getProduct())
                        .quantity(10)
                        .builtQuantity(0)
                        .build());
            }
        }
        return Order.builder()
                .date(Instant.now().toString())
                .storeName(modelIdentifier)
                .addAllProductRequest(requests)
                .build();
    }

    @Override
    protected void processDelivery(Shipment shipment) {
        for (ProductShipped productShipped: shipment.getProductshipped()) {
            ProductStock stockage = modelState.getProductstock().stream()
                    .filter(ps -> ps.getProduct().getName().equals(productShipped.getProduct().getName()))
                    .findFirst().orElseThrow();
            modelState.getProductstock().remove(stockage);
            modelState.getProductstock().add(stockage.withStockLevel(stockage.getStockLevel() +
                    productShipped.getQuantityShipped()));
        }
    }

    @Override
    protected void internalStateTransitionFunction(LongSimTime longSimTime) {
        // The only internally generated action is an order every 24 hours
        if (longSimTime.getT().intValue() - modelState.getLastOrderTime() == properties.getOrderInterval()) {
            Order order = ordering();
            if (!order.getProductRequest().isEmpty()) {
                modelState.getPendingSendOrderOut().add(order);
            }
            modelState.setLastOrderTime(longSimTime.getT().intValue());
        } else {
            throw new IllegalStateException("Store got internal state transition when not scheduled to order");
        }
    }

    @Override
    protected void externalSateTransitionFunction(LongSimTime longSimTime, Bag bag) {
        for (PortValue portValue: bag.getPortValueList()) {
            if (portValue.getPortIdentifier() == StoreModel.customerArrival.getPortIdentifier()) {
                Customer customer = StoreModel.customerArrival.getValue(portValue);
                processCustomer(customer);
            } else if (portValue.getPortIdentifier() == StoreModel.receiveShipment.getPortIdentifier()) {
                Shipment shipment = StoreModel.receiveShipment.getValue(portValue);
                processDelivery(shipment);
            } else {
                throw new IllegalArgumentException("Store received unknown portValue with identifier " +
                        portValue.getPortIdentifier());
            }
        }
    }

    @Override
    protected void confluentStateTransitionFunction(LongSimTime longSimTime, Bag bag) {
        externalSateTransitionFunction(longSimTime,bag);
        internalStateTransitionFunction(longSimTime);
    }

    @Override
    protected LongSimTime timeAdvanceFunction(LongSimTime longSimTime) {
        if (this.hasPendingOutput()) {
            return longSimTime;
        }
        return LongSimTime.builder()
                .t((long)(modelState.getLastOrderTime() + properties.getOrderInterval()))
                .build();
    }

}
