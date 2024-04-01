package iso.example.store.impl.storeexperimentalframe.storecoupledmodel;

import devs.msg.Bag;
import devs.msg.PortValue;
import devs.msg.time.LongSimTime;
import iso.example.store.immutables.Order;
import iso.example.store.storeexperimentalframe.storecoupledmodel.ModifiableSupplierModelState;
import iso.example.store.storeexperimentalframe.storecoupledmodel.SupplierModel;
import iso.example.store.storeexperimentalframe.storecoupledmodel.SupplierModelProperties;

public class SupplierModelImpl extends SupplierModel {
    public SupplierModelImpl(ModifiableSupplierModelState initialState, SupplierModelProperties properties) {
        super(initialState, SupplierModel.modelIdentifier, properties);
    }

    /**
     * The only internal state transition function will be to clear output if the pendingSendShipmentOut
     * list has data
     * @param currentTime the current time of the transition
     */
    @Override
    protected void internalStateTransitionFunction(LongSimTime currentTime) {
        boolean hadOutput = hasPendingOutput();
        if (hadOutput) {
            clearPendingOutput();
        } else {
            throw new IllegalStateException(
                    "Supplier model attempted to execute internal transition when it had no output");
        }

    }

    // If the store gets an order, it will fulfill the entire order with a shipment scheduled for

    /**
     * If the store gets an order, it will fulfill the entire order with a shipment scheduled for
     * that supplier's exact mean shipment time.  (No random numbers makes testing easier for this
     * example simulation)
     * @param currentTime the current time of the transition
     * @param bag the bag of inputs that has arrived at the current time
     */
    @Override
    protected void externalSateTransitionFunction(LongSimTime currentTime, Bag bag) {
        for (PortValue<?> pv: bag.getPortValueList()) {
            if (pv.getPortIdentifier().equals(SupplierModel.receiveOrder.getPortIdentifier())) {
                Order order = SupplierModel.receiveOrder.getValue(pv);

            }
        }
    }

    @Override
    protected void confluentStateTransitionFunction(LongSimTime currentTime, Bag bag) {

    }

    @Override
    protected LongSimTime timeAdvanceFunction(LongSimTime currentTime) {
        return null;
    }
}
