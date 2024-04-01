package iso.example.store.impl.storeexperimentalframe.storecoupledmodel;


import devs.msg.Bag;
import devs.msg.PortValue;
import devs.msg.time.LongSimTime;
import iso.example.store.immutables.*;
import iso.example.store.storeexperimentalframe.storecoupledmodel.ModifiableStoreModelState;
import iso.example.store.storeexperimentalframe.storecoupledmodel.StoreModel;
import iso.example.store.storeexperimentalframe.storecoupledmodel.StoreModelProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class StoreModelTest extends AbstractStoreModelTest {

    private final StoreModelImpl storeModel;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    static Product truck = Product.builder().name("Truck").cost(10.0).weight(5.0).build();
    static Vechile vechile = Vechile.builder().payloadCapacity(30.0).vehicleId("TestVehicle").build();
    static ProductShipped productShipped = ProductShipped.builder().quantityShipped(2).product(truck).build();

    public StoreModelTest() {
        this.storeModel = (StoreModelImpl) buildStoreModel();
    }

    @Override
    protected ModifiableStoreModelState buildInitialState() {
        return ModifiableStoreModelState.create()
                .setLastOrderTime(0)
                .setProductstock(Collections.singletonList(ProductStock.builder().product(truck).stockLevel(3).build()));
    }

    @Override
    protected StoreModelProperties buildProperties() {
        return StoreModelProperties.builder()
                .address("Next door")
                .orderInterval(24)
                .storeName("TestTruckStore")
                .orderQuantity(2)
                .build();
    }

    @Override
    protected StoreModel buildStoreModel() {
        return new StoreModelImpl(buildInitialState(), StoreModel.modelIdentifier, buildProperties());
    }

    /**
     * This test has the following expected sequence of events
     * Store properties initialized with an order interval of 24 and quantity of 2
     * Store stock initialized with 3 trucks
     * At t = 1,2,3 - a customer named enters the store and purchases a truck
     * At t = 4 - a customer named enters the store but does not purchase a truck
     * At t = 24 - the store sends an order for 2 trucks to the shipper
     * At t = 30 - a shipment of 2 trucks arrives at the store
     * At t = 31,32 - a customer enters the store and purchases a truck
     * At t = 33 - a customer nenters the store but does not purchase a truck
     * The test validates the model by checking that each Satisfied customer did indeed purchase a product and
     *  each Disappointed customer did not
     *  It also validates an order for 2 trucks is received at t = 24
     */
    @Test
    @DisplayName("Test store model")
    void testStoreModel() {
        // A customer enters at t = 1,2,3,4
        for (long i = 1; i <= 4; i++) {
            processCustomer(i);
        }
        // At time 0, next internal transition is an order at t = 24
        assert storeModel.timeAdvanceFunction(LongSimTime.builder().t(5L).build())
                .equals(LongSimTime.builder().t(24L).build());
        // The store generates an order at the order interval, 24 hours
        processOrder(24);
        // A shipment arrives at t = 25
        processShipment(30);
        // A customer enters at t = 31,32,33
        for (long i = 31; i <= 33; i++) {
            processCustomer(i);
        }

    }


    /**
     * This method call's the store's internal state transition function for it to process an order
     * The order product and quantity ordered should match the data in the store properties.  In this case
     * the store should order 2 trucks.
     * @param i the time in hours at which the order is processed
     */
    void processOrder(long i) {
        LongSimTime t = LongSimTime.builder().t(i).build();
        storeModel.internalStateTransitionFunction(t);
        LongSimTime nextTime = storeModel.timeAdvanceFunction(t);
        assert nextTime.equals(t);
        Order orderOut = StoreModel.sendOrder.getValue(storeModel.outputFunction().getPortValueList().get(0));
        ProductRequest productRequest = orderOut.getProductRequest().get(0);
        logger.info("Store ordered " + productRequest.getQuantity() + " " + productRequest.getProduct().getName());
        assert productRequest.getProduct().getName().equals(truck.getName());
        assert productRequest.getQuantity().equals(2);
        storeModel.internalStateTransitionFunction(t);  // Clears shipment from pending output
    }

    /**
     * This method call's the store's external state transition function as a shipment arrives
     * In this test, the shipment will have 2 trucks to match the ordered quantity.
     * @param i the time in hours at which the shipment arrives
     */
    void processShipment(long i) {
        Shipment shipment = Shipment.builder()
                .plantName("TestPlant")
                .vechile(vechile)
                .storeName("TestTruckStore")
                .addProductshipped(productShipped)
                .build();
        PortValue<Shipment> portValue = StoreModel.receiveShipment.createPortValue(shipment);
        LongSimTime time = LongSimTime.builder().t(i).build();
        storeModel.externalSateTransitionFunction(time, Bag.builder().addPortValueList(portValue).build());
    }

    /**
     * This method call's the store's external state transition function as a customer enters the store.
     * The store should immediately process the customer and add to the pendingCustomerDepartureOut list
     * This method then calls the time advance function to ensure the model requests an immediate
     * state transition to allow output.  The result of the time advance function should equal the current time.
     * This method then calls the output function to get the customer departing the store.  Based in this test,
     * customer's exiting at t=1,2,3,31,32 should purchase a product, and those exiting at t=4,33 should not
     * This method then calls the internal state transition function to clear the pending output lists
     * @param i the time in hours the customer enters the store
     */
    void processCustomer(long i) {
        Customer customerIn = Customer.builder().customerId("Customer" + i).product(truck).purchasedProduct(false).build();
        PortValue<Customer> portValue = StoreModel.customerArrival.createPortValue(customerIn);
        LongSimTime time = LongSimTime.builder().t(i).build();
        storeModel.externalSateTransitionFunction(time, Bag.builder().addPortValueList(portValue).build());
        LongSimTime nextTime = storeModel.timeAdvanceFunction(time);
        assert nextTime.equals(time);
        Customer customerOut = StoreModel.customerDeparture.getValue(storeModel.outputFunction().getPortValueList().get(0));
        logger.info(customerOut.getCustomerId() + " exited store with purchased product equals " + customerOut.getPurchasedProduct());
        if (i == 4 || i == 33) {
            assert customerOut.getPurchasedProduct().equals(false);
        } else {
            assert customerOut.getPurchasedProduct().equals(true);
        }
        storeModel.internalStateTransitionFunction(time);  // Clears customer from pending output
    }
}
