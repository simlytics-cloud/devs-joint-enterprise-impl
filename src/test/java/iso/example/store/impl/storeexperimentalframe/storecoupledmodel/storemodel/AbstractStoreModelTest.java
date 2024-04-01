package iso.example.store.impl.storeexperimentalframe.storecoupledmodel.storemodel;

import devs.PDevsCoordinator;
import devs.PDevsCouplings;
import devs.PDevsSimulator;
import devs.RootCoordinator;
import devs.msg.DevsMessage;
import devs.msg.InitSim;
import devs.msg.time.LongSimTime;
import iso.example.store.storeexperimentalframe.storecoupledmodel.StoreModel;
import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.javadsl.Behaviors;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.pekko.actor.testkit.typed.javadsl.ActorTestKit;
import org.apache.pekko.actor.testkit.typed.javadsl.TestProbe;

public abstract class AbstractStoreModelTest {
    protected ActorTestKit testKit;

    protected LongSimTime startTime = LongSimTime.builder().t(0L).build();
    protected abstract StoreModel buildStoreModel();

    protected abstract StoreModelTestAcceptor buildStoreModelTestAcceptor();

    protected abstract StoreModelTestGenerator buildStoreModelTestGenerator();


    public Behavior<DevsMessage> createStoreModelTestFrame() {
        Map<String, ActorRef<DevsMessage>> modelSimulators = new HashMap<>();
        PDevsCouplings couplings = new PDevsCouplings(
                Collections.emptyList(), Collections.singletonList(new StoreModelTestOutputCouplingHandler()));
        return Behaviors.setup(context -> {
            ActorRef<DevsMessage> storeModelRef = context.spawn(PDevsSimulator.create(
                    buildStoreModel(), startTime), StoreModel.modelIdentifier);
            modelSimulators.put(StoreModel.modelIdentifier, storeModelRef);

            ActorRef<DevsMessage> storeModelTestAcceptorRef = context.spawn(PDevsSimulator.create(
                    buildStoreModelTestAcceptor(), startTime), StoreModelTestAcceptor.modelIdentifier);
            modelSimulators.put(StoreModelTestAcceptor.modelIdentifier, storeModelTestAcceptorRef);

            ActorRef<DevsMessage> storeModelTestGeneratorRef = context.spawn(PDevsSimulator.create(
                    buildStoreModelTestGenerator(), startTime), StoreModelTestGenerator.modelIdentifier);
            modelSimulators.put(StoreModelTestGenerator.modelIdentifier, storeModelTestGeneratorRef);

            return new PDevsCoordinator<LongSimTime>("storeModelTestFrame", "root",
                    modelSimulators, couplings, context);
        });
    }

    protected void runStoreModelTest(LongSimTime endTime) throws InterruptedException {
        ActorTestKit testKit = ActorTestKit.create();
        ActorRef<DevsMessage> storeModelTestFrame = testKit.spawn(createStoreModelTestFrame(), "storeModelTestFrame");
        ActorRef<DevsMessage> rootCoordinator = testKit.spawn(RootCoordinator.create(
                endTime, storeModelTestFrame), "root");
        rootCoordinator.tell(InitSim.builder().time(startTime).build());
        TestProbe<DevsMessage> testProbe = testKit.createTestProbe();
        testProbe.expectTerminated(rootCoordinator, Duration.ofSeconds(10));
        Thread.sleep(10 * 1000);
        //testKit.shutdownTestKit();
    }

}
