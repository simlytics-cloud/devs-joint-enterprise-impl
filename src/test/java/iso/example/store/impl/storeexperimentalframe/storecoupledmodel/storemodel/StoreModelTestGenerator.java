package iso.example.store.impl.storeexperimentalframe.storecoupledmodel.storemodel;

import devs.PDEVSModel;
import devs.Port;
import devs.msg.time.LongSimTime;
import iso.example.store.immutables.Customer;
import iso.example.store.immutables.Shipment;
import iso.example.store.impl.storeexperimentalframe.storecoupledmodel.storemodel.StoreModelTestGeneratorState;

public abstract class StoreModelTestGenerator extends PDEVSModel<LongSimTime, StoreModelTestGeneratorState> {

    public static String modelIdentifier = "storeModelTestGenerator";

    public static Port<Customer> toCustomerArrival = new Port<>("TO_CUSTOMER_ARRIVAL");
    public static Port<Shipment> toReceiveShipment = new Port<>("TO_RECEIVE_SHIPMENT");


    public StoreModelTestGenerator(StoreModelTestGeneratorState initialState) {
        super(initialState, modelIdentifier);
    }


}
