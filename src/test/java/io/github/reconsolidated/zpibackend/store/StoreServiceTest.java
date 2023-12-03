package io.github.reconsolidated.zpibackend.store;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import io.github.reconsolidated.zpibackend.domain.appUser.AppUser;
import io.github.reconsolidated.zpibackend.domain.store.Store;
import io.github.reconsolidated.zpibackend.domain.store.StoreRepository;
import io.github.reconsolidated.zpibackend.domain.store.StoreService;
import io.github.reconsolidated.zpibackend.domain.store.dtos.CreateStoreDto;
import io.github.reconsolidated.zpibackend.domain.storeConfig.Owner;
import io.github.reconsolidated.zpibackend.domain.storeConfig.StoreConfig;
import io.github.reconsolidated.zpibackend.domain.storeConfig.StoreConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;
    @Mock
    private StoreConfigService storeConfigService;

    @InjectMocks
    private StoreService storeService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetStore_StoreFound() {
        String storeName = "Test Store";
        Store mockStore = new Store();
        when(storeRepository.findByStoreName(storeName)).thenReturn(Optional.of(mockStore));

        Store result = storeService.getStore(storeName);

        assertNotNull(result);
        assertEquals(mockStore, result);
    }

    @Test
    public void testGetStore_StoreNotFound() {
        String storeName = "Nonexistent Store";
        when(storeRepository.findByStoreName(storeName)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> storeService.getStore(storeName));
    }

    @Test
    public void testCreateStore() {
        AppUser currentUser = new AppUser();
        currentUser.setId(3L);
        CreateStoreDto dto = new CreateStoreDto();
        dto.setStoreConfigId(1L);
        StoreConfig storeConfig = new StoreConfig();
        Owner owner = new Owner();
        owner.setAppUserId(currentUser.getId());
        storeConfig.setOwner(owner);

        when(storeConfigService.getStoreConfig(dto.getStoreConfigId())).thenReturn(storeConfig);
        when(storeRepository.save(any(Store.class))).thenReturn(new Store(storeConfig));

        Store result = storeService.createStore(currentUser, dto);

        assertNotNull(result);
        verify(storeRepository).save(any(Store.class));
    }

    @Test
    public void testListStores_ByUser() {
        AppUser currentUser = new AppUser();
        when(storeRepository.findAllByOwnerAppUserId(currentUser.getId())).thenReturn(Arrays.asList(new Store(), new Store()));

        List<Store> result = storeService.listStores(currentUser);

        assertEquals(2, result.size());
    }

    @Test
    public void testListStores_All() {
        when(storeRepository.findAll()).thenReturn(Arrays.asList(new Store(), new Store(), new Store()));

        List<Store> result = storeService.listStores();

        assertEquals(3, result.size());
    }


}
