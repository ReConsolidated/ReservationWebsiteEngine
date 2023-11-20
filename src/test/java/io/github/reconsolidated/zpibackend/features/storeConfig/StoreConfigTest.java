package io.github.reconsolidated.zpibackend.features.storeConfig;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.authentication.appUser.AppUserService;
import io.github.reconsolidated.zpibackend.features.storeConfig.dtos.OwnerDto;
import io.github.reconsolidated.zpibackend.features.storeConfig.dtos.StoreConfigDto;
import io.github.reconsolidated.zpibackend.features.storeConfig.dtos.StoreConfigsListDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class StoreConfigTest {
    @Autowired
    private StoreConfigService storeConfigService;
    @Autowired
    private AppUserService appUserService;

    @Test
    @Transactional
    public void testCreateStoreConfig() {
        final String keycloakId = "unique_id";
        AppUser user = appUserService.getOrCreateUser(keycloakId, "any@any.com", "name", "lastname");
        OwnerDto ownerDto = new OwnerDto(Owner.builder().storeName("name").build());
        CoreConfig coreConfig = CoreConfig.builder()
                .flexibility(false)
                .uniqueness(false)
                .simultaneous(true)
                .build();
        MainPageConfig mainPageConfig = MainPageConfig.builder().build();
        DetailsPageConfig detailsPageConfig = DetailsPageConfig.builder().build();
        StoreConfigDto storeConfig = new StoreConfigDto(
                null,
                ownerDto,
                coreConfig,
                mainPageConfig,
                detailsPageConfig,
                new ArrayList<>(),
                null);

        StoreConfig createdConfig = storeConfigService.createStoreConfig(user, storeConfig);
        assertThat(createdConfig.getStoreConfigId()).isNotNull();
        assertThat(createdConfig.getOwner().getAppUserId().equals(user.getId())).isTrue();

    }

    @Test
    @Transactional
    public void testCreateStoreConfig_fail_when_id_given() {
        final String keycloakId = "unique_id";
        AppUser user = appUserService.getOrCreateUser(keycloakId, "any@any.com", "name", "lastname");
        OwnerDto ownerDto = new OwnerDto(Owner.builder().storeName("name").build());
        CoreConfig coreConfig = CoreConfig.builder()
                .flexibility(false)
                .uniqueness(false)
                .simultaneous(true)
                .build();
        MainPageConfig mainPageConfig = MainPageConfig.builder().build();
        DetailsPageConfig detailsPageConfig = DetailsPageConfig.builder().build();
        StoreConfigDto storeConfig = new StoreConfigDto(
                1L,
                ownerDto,
                coreConfig,
                mainPageConfig,
                detailsPageConfig,
                new ArrayList<>(),
                null);

        assertThrows(IllegalArgumentException.class, () -> storeConfigService.createStoreConfig(user, storeConfig));
    }

    @Test
    @Transactional
    public void testFetchStoreConfigs() {
        final String keycloakId = "unique_id";
        AppUser user = appUserService.getOrCreateUser(keycloakId, "any@any.com", "name", "lastname");
        OwnerDto ownerDto = new OwnerDto(Owner.builder().storeName("name").build());
        CoreConfig coreConfig = CoreConfig.builder()
                .flexibility(false)
                .uniqueness(false)
                .simultaneous(true)
                .build();
        MainPageConfig mainPageConfig = MainPageConfig.builder().build();
        DetailsPageConfig detailsPageConfig = DetailsPageConfig.builder().build();
        StoreConfigDto storeConfig = new StoreConfigDto(
                null,
                ownerDto,
                coreConfig,
                mainPageConfig,
                detailsPageConfig,
                new ArrayList<>(),
                null);

        Long storeId = storeConfigService.createStoreConfig(user, storeConfig).getStoreConfigId();
        assertThat(storeId).isNotNull();

        StoreConfigsListDto dto = storeConfigService.listStoreConfigs(user);
        assertThat(dto.getStoreConfigList().size()).isEqualTo(1);
    }


    @Test
    @Transactional
    public void testUpdateStoreConfig() {
        final String keycloakId = "unique_id";
        AppUser user = appUserService.getOrCreateUser(keycloakId, "any@any.com", "name", "lastname");
        OwnerDto ownerDto = new OwnerDto(Owner.builder().storeName("name").build());
        CoreConfig coreConfig = CoreConfig.builder()
                .flexibility(false)
                .uniqueness(false)
                .simultaneous(true)
                .build();
        MainPageConfig mainPageConfig = MainPageConfig.builder().build();
        DetailsPageConfig detailsPageConfig = DetailsPageConfig.builder().build();
        StoreConfigDto storeConfig = new StoreConfigDto(
                null,
                ownerDto,
                coreConfig,
                mainPageConfig,
                detailsPageConfig,
                new ArrayList<>(),
                null);

        Long storeId = storeConfigService.createStoreConfig(user, storeConfig).getStoreConfigId();
        assertThat(storeId).isNotNull();

        CoreConfig coreConfig2 = CoreConfig.builder()
                .coreConfigId(storeConfig.getCore().getCoreConfigId())
                .flexibility(false)
                .uniqueness(false)
                .simultaneous(true)
                .build();
        StoreConfig storeConfig2 = StoreConfig.builder()
                .storeConfigId(storeId)
                .core(coreConfig2)
                .mainPage(mainPageConfig)
                .detailsPage(detailsPageConfig)
                .build();

        storeConfigService.updateStoreConfig(user, storeConfig2);
    }

    @Test
    @Transactional
    public void testUpdateStoreConfig_fail_different_core_config() {
        final String keycloakId = "unique_id";
        AppUser user = appUserService.getOrCreateUser(keycloakId, "any@any.com", "name", "lastname");
        OwnerDto ownerDto = new OwnerDto(Owner.builder().storeName("name").build());
        CoreConfig coreConfig = CoreConfig.builder()
                .flexibility(false)
                .uniqueness(false)
                .simultaneous(true)
                .build();
        MainPageConfig mainPageConfig = MainPageConfig.builder().build();
        DetailsPageConfig detailsPageConfig = DetailsPageConfig.builder().build();
        StoreConfigDto storeConfig = new StoreConfigDto(
                null,
                ownerDto,
                coreConfig,
                mainPageConfig,
                detailsPageConfig,
                new ArrayList<>(),
                null);

        Long storeId = storeConfigService.createStoreConfig(user, storeConfig).getStoreConfigId();
        assertThat(storeId).isNotNull();

        CoreConfig coreConfig2 = CoreConfig.builder()
                .coreConfigId(storeConfig.getCore().getCoreConfigId())
                .flexibility(true)
                .periodicity(false)
                .specificReservation(false)
                .simultaneous(true)
                .build();
        StoreConfig storeConfig2 = StoreConfig.builder()
                .storeConfigId(storeId)
                .core(coreConfig2)
                .mainPage(mainPageConfig)
                .detailsPage(detailsPageConfig)
                .build();

        assertThrows(IllegalArgumentException.class, () -> storeConfigService.updateStoreConfig(user, storeConfig2));
    }

}
