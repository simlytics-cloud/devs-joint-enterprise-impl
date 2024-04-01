package iso.example.store.impl.storeexperimentalframe.storecoupledmodel.storemodel;

import devs.msg.Bag;
import devs.msg.time.LongSimTime;
import devs.utils.TimeUtils;
import iso.example.store.immutables.Customer;
import iso.example.store.immutables.Shipment;
import iso.example.store.storeexperimentalframe.storecoupledmodel.StoreModel;

public class StoreModelTestGeneratorImpl extends StoreModelTestGenerator {

    public StoreModelTestGeneratorImpl(ModifiableStoreModelTestGeneratorState initialState) {
        super(initialState);
    }

    @Override
    protected void internalStateTransitionFunction(LongSimTime currentTime) {
        while (modelState.getToCustomerArrivalSchedule().containsKey(currentTime)) {
            modelState.addPendingToCustomerArrival(modelState.getToCustomerArrivalSchedule().remove(currentTime));
        }
        while (modelState.getToReceiveShipmentSchedule().containsKey(currentTime)) {
            modelState.addPendingToReceiveShipment(modelState.getToReceiveShipmentSchedule().remove(currentTime));
        }
    }

    @Override
    protected void externalSateTransitionFunction(LongSimTime currentTime, Bag bag) {
        // No exterrnal events
        throw new IllegalArgumentException("StoreModelTestGenerator does not expect external events.  \n" +
                "Got event with port identifier of " + bag.getPortValueList().get(0).getPortIdentifier());
    }

    @Override
    protected void confluentStateTransitionFunction(LongSimTime currentTime, Bag bag) {
        internalStateTransitionFunction(currentTime);
    }

    @Override
    protected LongSimTime timeAdvanceFunction(LongSimTime currentTime) {
        LongSimTime minEventTime = TimeUtils.maxLongSimTime;
        if (!modelState.getToCustomerArrivalSchedule().isEmpty()) {
            minEventTime = modelState.getToCustomerArrivalSchedule().firstKey();
        }
        if (!modelState.getToReceiveShipmentSchedule().isEmpty()) {
            LongSimTime minShipmentTime = modelState.getToReceiveShipmentSchedule().firstKey();
            if (minShipmentTime.compareTo(minEventTime) < 0) {
                minEventTime = minShipmentTime;
            }
        }
        return minEventTime;
    }

    @Override
    protected Bag outputFunction() {
        Bag.Builder bagBuilder = Bag.builder();
        for (Customer customer: modelState.getPendingToCustomerArrival()) {
            bagBuilder.addPortValueList(StoreModelTestGenerator.toCustomerArrival.createPortValue(customer));
        }
        modelState.getPendingToCustomerArrival().clear();

        for (Shipment shipment: modelState.getPendingToReceiveShipment()) {
            bagBuilder.addPortValueList(StoreModelTestGenerator.toReceiveShipment.createPortValue(shipment));
        }
        modelState.getPendingToReceiveShipment().clear();
        return bagBuilder.build();
    }
}
