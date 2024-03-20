package iso.example.store.impl.storeexperimentalframe.storecoupledmodel;

import iso.example.store.storeexperimentalframe.storecoupledmodel.AbstractStoreCoupledModelFactory;
import iso.example.store.storeexperimentalframe.storecoupledmodel.StoreModel;
import iso.example.store.storeexperimentalframe.storecoupledmodel.SupplierModel;

import java.util.List;

public class StoreCoupledModelFactory extends AbstractStoreCoupledModelFactory {

    public StoreCoupledModelFactory() {
    }

    @Override
    protected List<StoreModel> buildStoreModels() {
        return null;
    }

    @Override
    protected List<SupplierModel> buildSupplierModels() {
        return null;
    }
}
