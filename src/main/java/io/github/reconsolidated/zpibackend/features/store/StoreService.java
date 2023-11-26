package io.github.reconsolidated.zpibackend.features.store;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.features.store.dtos.CreateStoreDto;
import io.github.reconsolidated.zpibackend.features.storeConfig.StoreConfig;
import io.github.reconsolidated.zpibackend.features.storeConfig.StoreConfigService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final StoreConfigService storeConfigService;


    public Store getStore(AppUser currentUser, Long storeId) {
        return storeRepository.findById(storeId).orElseThrow();
    }

    public Store getStore(String storeName) {
        return storeRepository.findByStoreConfigName(storeName).orElseThrow();

    }

    public Store createStore(AppUser currentUser, CreateStoreDto dto) {
        StoreConfig storeConfig = storeConfigService.getStoreConfig(currentUser, dto.getStoreConfigId());
        Store store = new Store(storeConfig);
        return storeRepository.save(store);
    }

    public List<Store> listStores(AppUser currentUser) {
        if (currentUser == null) {
            return storeRepository.findAll();
        }
        return storeRepository.findAllByOwnerAppUserId(currentUser.getId());
    }
}
