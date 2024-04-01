package iso.example.store.impl.storeexperimentalframe.storecoupledmodel.storemodel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import devs.msg.time.LongSimTime;
import devs.utils.DevsObjectMapper;
import iso.example.store.immutables.*;
import iso.example.store.impl.storeexperimentalframe.storecoupledmodel.StoreModelImpl;
import iso.example.store.storeexperimentalframe.storecoupledmodel.ModifiableStoreModelState;
import iso.example.store.storeexperimentalframe.storecoupledmodel.StoreModel;
import iso.example.store.storeexperimentalframe.storecoupledmodel.StoreModelProperties;
import iso.example.store.storeexperimentalframe.storecoupledmodel.StoreModelState;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.TreeMap;

/**
 * This test has the following expected sequence of events
 * Store properties initialized with an order interval of 24 and quantity of 2
 * Store stock initialized with 3 trucks
 * At t = 1,2,3 - a customer named Satisfied1,2,3 enters the store and purchases a truck
 * At t = 4 - a customer named Disappointed4 enters the store but does not purchase a truck
 * At t = 24 - the store sends an order for 2 trucks to the shipper
 * At t = 25 - a shipment of 2 trucks arrives at the store
 * At t = 31,32 - a customer named Satisfied31,32 enters the store and purchases a truck
 * At t = 33 - a customer named Disappointed33 enters the store but does not purchase a truck
 * The acceptor validates the model by checking that each Satisfied customer did indeed purchase a product and
 *  each Disappointed customer did not
 *  It also validates an order for 2 trucks is received at t = 24
 */
public class StoreModelTest extends AbstractStoreModelTest {

    static ObjectMapper objectMapper = DevsObjectMapper.buildObjectMapper();

    static Product truck = Product.builder().name("Truck").cost(10.0).weight(5.0).build();

    public static StoreModelProperties storeModelProperties = StoreModelProperties.builder()
            .address("Next door")
            .orderInterval(24)
            .storeName("TestTruckStore")
            .orderQuantity(2)
            .build();

    static Vechile vechile = Vechile.builder().payloadCapacity(30.0).vehicleId("TestVehicle").build();
    static ProductShipped productShipped = ProductShipped.builder().quantityShipped(2).product(truck).build();
    static Shipment shipment = Shipment.builder()
            .plantName("TestPlant")
            .vechile(vechile)
            .storeName(storeModelProperties.getStoreName())
            .addProductshipped(productShipped)
            .build();



    @Override
    protected StoreModel buildStoreModel() {


        ModifiableStoreModelState storeModelState = ModifiableStoreModelState.create()
                .setLastOrderTime(0)
                .setProductstock(Collections.singletonList(ProductStock.builder().product(truck).stockLevel(3).build()));
        try {
            String propertiesJson = objectMapper.writeValueAsString(storeModelProperties);
            String stateJson = objectMapper.writeValueAsString(storeModelState.toImmutable());
            StoreModelProperties properties = objectMapper.readValue(propertiesJson, StoreModelProperties.class);
            StoreModelState initialState = objectMapper.readValue(stateJson, StoreModelState.class);
            ModifiableStoreModelState modifiableInitialState =  ModifiableStoreModelState.create().from(initialState);
            return new StoreModelImpl(modifiableInitialState,
                    StoreModel.modelIdentifier,
                    properties);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected StoreModelTestAcceptor buildStoreModelTestAcceptor() {
        return new StoreModelTestAcceptorImpl();
    }

    @Override
    protected StoreModelTestGenerator buildStoreModelTestGenerator() {
        TreeMap<LongSimTime, Customer> customerSchedule = new TreeMap<>();
        TreeMap<LongSimTime, Shipment> shipmentSchedule = new TreeMap<>();
        customerSchedule.put(LongSimTime.builder().t(1L).build(),
                Customer.builder().customerId("Satisfied1").product(truck).purchasedProduct(false).build());
        customerSchedule.put(LongSimTime.builder().t(2L).build(),
                Customer.builder().customerId("Satisfied2").product(truck).purchasedProduct(false).build());
        customerSchedule.put(LongSimTime.builder().t(3L).build(),
                Customer.builder().customerId("Satisfied3").product(truck).purchasedProduct(false).build());
        customerSchedule.put(LongSimTime.builder().t(4L).build(),
                Customer.builder().customerId("Disappointed4").product(truck).purchasedProduct(false).build());
        shipmentSchedule.put(LongSimTime.builder().t(25L).build(),
                shipment);
        customerSchedule.put(LongSimTime.builder().t(31L).build(),
                Customer.builder().customerId("Satisfied31").product(truck).purchasedProduct(false).build());
        customerSchedule.put(LongSimTime.builder().t(32L).build(),
                Customer.builder().customerId("Satisfied32").product(truck).purchasedProduct(false).build());
        customerSchedule.put(LongSimTime.builder().t(33L).build(),
                Customer.builder().customerId("Disappointed33").product(truck).purchasedProduct(false).build());
        ModifiableStoreModelTestGeneratorState initialState = ModifiableStoreModelTestGeneratorState.create()
                .putAllToCustomerArrivalSchedule(customerSchedule)
                .putAllToReceiveShipmentSchedule(shipmentSchedule);
        return new StoreModelTestGeneratorImpl(initialState);
    }

    @Test
    void testStoreModel() throws InterruptedException {
        runStoreModelTest(LongSimTime.builder().t(48L).build());
    }
}
