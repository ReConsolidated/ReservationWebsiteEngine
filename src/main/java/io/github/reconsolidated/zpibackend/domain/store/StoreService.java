package io.github.reconsolidated.zpibackend.domain.store;

import io.github.reconsolidated.zpibackend.domain.appUser.AppUser;
import io.github.reconsolidated.zpibackend.domain.store.dtos.CreateStoreDto;
import io.github.reconsolidated.zpibackend.domain.storeConfig.StoreConfig;
import io.github.reconsolidated.zpibackend.domain.storeConfig.StoreConfigService;
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
        return storeRepository.findAllByOwnerAppUserId(currentUser.getId());
    }

    public List<Store> listStores() {
        return storeRepository.findAll();
    }
}
