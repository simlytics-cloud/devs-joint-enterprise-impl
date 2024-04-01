package iso.example.store.impl.storeexperimentalframe.storecoupledmodel;

import iso.example.store.storeexperimentalframe.storecoupledmodel.ModifiableStoreModelState;
import iso.example.store.storeexperimentalframe.storecoupledmodel.StoreModel;
import iso.example.store.storeexperimentalframe.storecoupledmodel.StoreModelProperties;

public abstract class AbstractStoreModelTest {

    protected abstract ModifiableStoreModelState buildInitialState();
    protected abstract StoreModelProperties buildProperties();
    protected abstract StoreModel buildStoreModel();
}
