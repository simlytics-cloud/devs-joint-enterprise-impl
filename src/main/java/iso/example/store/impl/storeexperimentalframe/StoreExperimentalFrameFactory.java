package iso.example.store.impl.storeexperimentalframe;

import iso.example.store.storeexperimentalframe.AbstractStoreExperimentalFrameFactory;
import iso.example.store.storeexperimentalframe.CustomerArrivals;
import iso.example.store.storeexperimentalframe.StoreEvaluator;
import iso.example.store.storeexperimentalframe.storecoupledmodel.AbstractStoreCoupledModelFactory;

import java.util.List;

public class StoreExperimentalFrameFactory extends AbstractStoreExperimentalFrameFactory {

    public StoreExperimentalFrameFactory() {
    }

    @Override
    protected List<CustomerArrivals> buildCustomerArrivalss() {
        return null;
    }

    @Override
    protected List<StoreEvaluator> buildStoreEvaluators() {
        return null;
    }

    @Override
    protected AbstractStoreCoupledModelFactory buildStoreCoupledModelFactory() {
        return null;
    }
}
