package io.github.reconsolidated.zpibackend.features.storeAccess;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.features.store.Store;
import io.github.reconsolidated.zpibackend.features.storeConfig.StoreAccessType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StoreAccessService {
    public boolean validateClientAccess(AppUser currentUser, Store store) {
        if (store.getStoreConfig().getRequiredStoreAccessType().equals(StoreAccessType.ALL)) {
            return true;
        }
        if (store.getStoreConfig().getRequiredStoreAccessType().equals(StoreAccessType.LOGGED_IN)) {
            return currentUser != null;
        }

        return currentUser != null &&
                (store.getAddedUsers().stream().anyMatch((user) -> currentUser.getId().equals(user.getId()))
                || store.getStoreConfig().getOwner().getAppUserId().equals(currentUser.getId()));
    }
}
