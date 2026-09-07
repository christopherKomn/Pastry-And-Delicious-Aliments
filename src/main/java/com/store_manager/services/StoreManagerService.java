package com.store_manager.services;

import com.ErrorCodes;
import com.repository.IStoreManagerRepository;

/** Store operations scoped to the signed-in owner. */
public class StoreManagerService {
    private final IStoreManagerRepository repository;
    private final int ownerId;

    public StoreManagerService(IStoreManagerRepository repository, int ownerId) {
        this.repository = repository;
        this.ownerId = ownerId;
    }

    public ErrorCodes deleteStore() {
        try {
            // The owner-scoped DELETE also handles a store removed since login.
            return repository.deleteByOwnerId(ownerId);
        } catch (RuntimeException exception) {
            return ErrorCodes.IO_ERROR;
        }
    }
}
