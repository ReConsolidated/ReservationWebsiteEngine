package io.github.reconsolidated.zpibackend.domain.store;

import io.github.reconsolidated.zpibackend.domain.appUser.AppUser;
import io.github.reconsolidated.zpibackend.domain.item.Item;
import io.github.reconsolidated.zpibackend.domain.parameter.Parameter;
import io.github.reconsolidated.zpibackend.domain.parameter.ParameterSettings;
import io.github.reconsolidated.zpibackend.domain.store.dtos.CreateStoreDto;
import io.github.reconsolidated.zpibackend.domain.storeConfig.StoreConfig;
import io.github.reconsolidated.zpibackend.domain.storeConfig.StoreConfigService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final StoreConfigService storeConfigService;

    public Store getStore(String storeName) {
        return storeRepository.findByStoreName(storeName).orElseThrow();
    }

    public Store createStore(AppUser currentUser, CreateStoreDto dto) {
        StoreConfig storeConfig = storeConfigService.getStoreConfig(dto.getStoreConfigId());
        Store store = new Store(storeConfig);
        return storeRepository.save(store);
    }

    /**
     * @param currentUser
     * @return list of stores that are owned by curren user
     */
    public List<Store> listStores(AppUser currentUser) {
        return storeRepository.findAllByOwnerAppUserId(currentUser.getId());
    }

    /**
     * @return list of all stores in database
     */
    public List<Store> listStores() {
        return storeRepository.findAll();
    }

    public void saveStore(Store store) {
        storeRepository.save(store);
    }
}
