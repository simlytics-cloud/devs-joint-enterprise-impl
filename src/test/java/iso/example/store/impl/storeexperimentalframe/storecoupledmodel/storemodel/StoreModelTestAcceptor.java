package iso.example.store.impl.storeexperimentalframe.storecoupledmodel.storemodel;

import devs.PDEVSModel;
import devs.Port;
import devs.msg.time.LongSimTime;
import iso.example.store.immutables.Customer;
import iso.example.store.immutables.Order;

public abstract class StoreModelTestAcceptor extends PDEVSModel<LongSimTime, StoreModelTestAcceptor.StoreModelTestAcceptorState> {

    public static String modelIdentifier = "storeModelTestAcceptor";
    public static abstract class StoreModelTestAcceptorState {

    }

    public static Port<Customer> fromCustomerDeparture = new Port<>("FROM_CUSTOMER_DEPARTURE");
    public static Port<Order> fromSendOrder = new Port<>("FROM_CUSTOMER_DEPARTURE");


    public StoreModelTestAcceptor(StoreModelTestAcceptorState modelState) {
        super(modelState, modelIdentifier);
    }
}
