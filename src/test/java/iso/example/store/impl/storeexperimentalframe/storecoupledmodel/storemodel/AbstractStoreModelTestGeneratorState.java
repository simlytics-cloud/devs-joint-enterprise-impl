package iso.example.store.impl.storeexperimentalframe.storecoupledmodel.storemodel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import devs.msg.time.LongSimTime;
import iso.example.store.immutables.Customer;
import iso.example.store.immutables.Shipment;
import org.immutables.value.Value;
import iso.example.store.impl.storeexperimentalframe.storecoupledmodel.storemodel.StoreModelTestGeneratorState;

import java.util.List;
import java.util.SortedMap;

@Value.Immutable
@Value.Modifiable
@JsonSerialize(as = StoreModelTestGeneratorState.class)
@JsonDeserialize(as = StoreModelTestGeneratorState.class)
public abstract class AbstractStoreModelTestGeneratorState {

    public abstract List<Customer> getPendingToCustomerArrival();
    public abstract List<Shipment> getPendingToReceiveShipment();
    @Value.NaturalOrder
    public abstract SortedMap<LongSimTime, Customer> getToCustomerArrivalSchedule();
    @Value.NaturalOrder
    public abstract SortedMap<LongSimTime, Shipment> getToReceiveShipmentSchedule();

}
