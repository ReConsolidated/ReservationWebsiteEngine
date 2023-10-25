package io.github.reconsolidated.zpibackend.features.storeAccess;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.features.storeConfig.StoreAccessType;
import io.github.reconsolidated.zpibackend.features.storeConfig.StoreConfig;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StoreAccessService {
    public boolean validateViewAccess(AppUser currentUser, StoreConfig storeConfig) {
        if (storeConfig.getRequiredStoreAccessType().equals(StoreAccessType.ALL)) {
            return true;
        }
        if (storeConfig.getRequiredStoreAccessType().equals(StoreAccessType.LOGGED_IN)) {
            return currentUser != null;
        }
        // TODO check if user is invited to store
        return false;
    }
}
