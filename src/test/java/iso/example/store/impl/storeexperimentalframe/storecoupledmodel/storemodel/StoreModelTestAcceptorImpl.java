package iso.example.store.impl.storeexperimentalframe.storecoupledmodel.storemodel;

import devs.Port;
import devs.msg.Bag;
import devs.msg.PortValue;
import devs.msg.time.LongSimTime;
import devs.utils.TimeUtils;
import iso.example.store.immutables.Customer;
import iso.example.store.immutables.Order;
import iso.example.store.immutables.ProductRequest;

public class StoreModelTestAcceptorImpl extends StoreModelTestAcceptor {
    public StoreModelTestAcceptorImpl() {
        super(new StoreModelTestAcceptorState());
    }

    @Override
    protected void internalStateTransitionFunction(LongSimTime currentTime) {
        // No internal events
    }

    @Override
    protected void externalSateTransitionFunction(LongSimTime currentTime, Bag bag) {
        for (PortValue<?> pv: bag.getPortValueList()) {
            if (pv.getPortIdentifier().equals(StoreModelTestAcceptor.fromCustomerDeparture.getPortIdentifier())) {
                Customer customer = StoreModelTestAcceptor.fromCustomerDeparture.getValue(pv);
                System.out.println("Received customer " + customer.getCustomerId() + " with purchased" +
                        customer.getProduct().getName() + " = " + customer.getPurchasedProduct());
                assert customer.getCustomerId().startsWith("Satisfied") == customer.getPurchasedProduct();
            } else if (pv.getPortIdentifier().equals(StoreModelTestAcceptor.fromSendOrder.getPortIdentifier())) {
                Order order = StoreModelTestAcceptor.fromSendOrder.getValue(pv);
                assert order.getStoreName().equals(StoreModelTest.storeModelProperties.getStoreName());
                assert order.getProductRequest().size() == 1;
                ProductRequest productRequest = order.getProductRequest().get(0);
                System.out.println("Got order for " + productRequest.getQuantity() + " " + productRequest.getProduct().getName());
                assert productRequest.getProduct().getName().equals(StoreModelTest.truck.getName());
                assert productRequest.getQuantity().equals(StoreModelTest.storeModelProperties.getOrderQuantity());
                assert currentTime.getT().intValue() == StoreModelTest.storeModelProperties.getOrderInterval();
            } else {
                throw new IllegalArgumentException("Did not expect PortValue with identifier " + pv.getPortIdentifier());
            }
        }

    }

    @Override
    protected void confluentStateTransitionFunction(LongSimTime currentTime, Bag bag) {
        externalSateTransitionFunction(currentTime, bag);
        internalStateTransitionFunction(currentTime);
    }

    @Override
    protected LongSimTime timeAdvanceFunction(LongSimTime currentTime) {
        return TimeUtils.maxLongSimTime;
    }

    @Override
    protected Bag outputFunction() {
        return Bag.builder().build();
    }
}
