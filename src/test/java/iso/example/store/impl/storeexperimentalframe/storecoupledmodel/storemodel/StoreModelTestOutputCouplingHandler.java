package iso.example.store.impl.storeexperimentalframe.storecoupledmodel.storemodel;

import devs.OutputCouplingHandler;
import devs.msg.PortValue;
import iso.example.store.immutables.Customer;
import iso.example.store.immutables.Order;
import iso.example.store.immutables.Shipment;
import iso.example.store.storeexperimentalframe.storecoupledmodel.StoreCoupledModel;
import iso.example.store.storeexperimentalframe.storecoupledmodel.StoreModel;
import iso.example.store.storeexperimentalframe.storecoupledmodel.SupplierModel;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StoreModelTestOutputCouplingHandler extends OutputCouplingHandler {

    public StoreModelTestOutputCouplingHandler() {
        super(Optional.empty(), Optional.empty(), Optional.empty());
    }

    @Override
    public void handlePortValue(String sender, PortValue<?> portValue, Map<String, List<PortValue<?>>> receiverMap,
                                List<PortValue<?>> outputMessages) {

        if (portValue.getPortIdentifier().equals(StoreModelTestGenerator.toCustomerArrival.getPortIdentifier())) {
            PortValue<Customer> flowPortValue = StoreModel.customerArrival.createPortValue(
                    StoreModelTestGenerator.toCustomerArrival.getValue(portValue));
            addInputPortValue(flowPortValue, determineTargetModel(portValue), receiverMap);
        } else if (portValue.getPortIdentifier().equals(StoreModelTestGenerator.toReceiveShipment.getPortIdentifier())) {
            PortValue<Shipment> flowPortValue = StoreModel.receiveShipment.createPortValue(
                    StoreModelTestGenerator.toReceiveShipment.getValue(portValue));
            addInputPortValue(flowPortValue, determineTargetModel(portValue), receiverMap);
        } else if (portValue.getPortIdentifier().equals(StoreModel.customerDeparture.getPortIdentifier())) {
            PortValue<Customer> flowPortValue = StoreModelTestAcceptor.fromCustomerDeparture.createPortValue(
                    StoreModel.customerDeparture.getValue(portValue));
            addInputPortValue(flowPortValue, determineTargetModel(portValue), receiverMap);
        } else if (portValue.getPortIdentifier().equals(StoreModel.sendOrder.getPortIdentifier())) {
            PortValue<Order> flowPortValue = StoreModelTestAcceptor.fromSendOrder.createPortValue(
                    StoreModel.sendOrder.getValue(portValue));
            addInputPortValue(flowPortValue, determineTargetModel(portValue), receiverMap);
        } else {
            throw new IllegalArgumentException("Output couplings did not recognize port identifier " +
                    portValue.getPortIdentifier());
        }

    }

    protected String determineTargetModel(PortValue<?> fromPortValue) {
        return switch (fromPortValue.getPortIdentifier()) {
            case "toCustomerArrival" -> StoreModel.modelIdentifier;
            case "toReceiveShipment" -> StoreModel.modelIdentifier;
            case "customerDeparture" -> StoreModelTestAcceptor.modelIdentifier;
            case "sendOrder" -> StoreModelTestAcceptor.modelIdentifier;
            default -> throw new IllegalArgumentException(
                    "Could not identify target model from PortValue with identifier " +
                            fromPortValue.getPortIdentifier());
        };
    }
}
