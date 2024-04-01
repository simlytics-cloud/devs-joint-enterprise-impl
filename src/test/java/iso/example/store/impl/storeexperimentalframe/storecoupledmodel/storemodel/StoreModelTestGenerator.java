package iso.example.store.impl.storeexperimentalframe.storecoupledmodel.storemodel;

import devs.PDEVSModel;
import devs.Port;
import devs.msg.time.LongSimTime;
import iso.example.store.immutables.Customer;
import iso.example.store.immutables.Shipment;
import iso.example.store.impl.storeexperimentalframe.storecoupledmodel.storemodel.StoreModelTestGeneratorState;

public abstract class StoreModelTestGenerator extends PDEVSModel<LongSimTime, ModifiableStoreModelTestGeneratorState> {

    public static String modelIdentifier = "storeModelTestGenerator";

    public static Port<Customer> toCustomerArrival = new Port<>("toCustomerArrival");
    public static Port<Shipment> toReceiveShipment = new Port<>("toReceiveShipment");


    public StoreModelTestGenerator(ModifiableStoreModelTestGeneratorState initialState) {
        super(initialState, modelIdentifier);
    }


}
