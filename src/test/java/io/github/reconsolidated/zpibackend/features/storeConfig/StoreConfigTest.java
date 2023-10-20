package io.github.reconsolidated.zpibackend.features.storeConfig;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.authentication.appUser.AppUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
        CoreConfig coreConfig = CoreConfig.builder().build();
        MainPageConfig mainPageConfig = MainPageConfig.builder().build();
        DetailsPageConfig detailsPageConfig = DetailsPageConfig.builder().build();
        StoreConfig storeConfig = StoreConfig.builder()
                .core(coreConfig)
                .mainPage(mainPageConfig)
                .detailsPage(detailsPageConfig)
                .build();

        Long storeId = storeConfigService.createStoreConfig(storeConfig).getStoreConfigId();
        assertThat(storeId).isNotNull();
    }

    @Test
    @Transactional
    public void testCreateStoreConfig_fail_when_id_given() {
        CoreConfig coreConfig = CoreConfig.builder().build();
        MainPageConfig mainPageConfig = MainPageConfig.builder().build();
        DetailsPageConfig detailsPageConfig = DetailsPageConfig.builder().build();
        StoreConfig storeConfig = StoreConfig.builder()
                .storeConfigId(1L)
                .core(coreConfig)
                .mainPage(mainPageConfig)
                .detailsPage(detailsPageConfig)
                .build();

        assertThrows(IllegalArgumentException.class, () -> storeConfigService.createStoreConfig(storeConfig));
    }

    @Test
    @Transactional
    public void testFetchStoreConfigs() {
        CoreConfig coreConfig = CoreConfig.builder().build();
        MainPageConfig mainPageConfig = MainPageConfig.builder().build();
        DetailsPageConfig detailsPageConfig = DetailsPageConfig.builder().build();
        StoreConfig storeConfig = StoreConfig.builder()
                .core(coreConfig)
                .mainPage(mainPageConfig)
                .detailsPage(detailsPageConfig)
                .build();

        Long storeId = storeConfigService.createStoreConfig(storeConfig).getStoreConfigId();
        assertThat(storeId).isNotNull();

        final String keycloakId = "unique_id";
        AppUser user = appUserService.getOrCreateUser(keycloakId, "any@any.com", "name", "lastname");

        StoreConfigsListDto dto = storeConfigService.listStoreConfigs(user);
        assertThat(dto.getStoreConfigList().size()).isEqualTo(1);
    }


    @Test
    @Transactional
    public void testUpdateStoreConfig() {
        CoreConfig coreConfig = CoreConfig.builder().build();
        MainPageConfig mainPageConfig = MainPageConfig.builder().build();
        DetailsPageConfig detailsPageConfig = DetailsPageConfig.builder().build();
        StoreConfig storeConfig = StoreConfig.builder()
                .core(coreConfig)
                .mainPage(mainPageConfig)
                .detailsPage(detailsPageConfig)
                .build();

        Long storeId = storeConfigService.createStoreConfig(storeConfig).getStoreConfigId();
        assertThat(storeId).isNotNull();

        CoreConfig coreConfig2 = CoreConfig.builder()
                .coreConfigId(storeConfig.getCore().getCoreConfigId()).build();
        StoreConfig storeConfig2 = StoreConfig.builder()
                .storeConfigId(storeId)
                .core(coreConfig2)
                .mainPage(mainPageConfig)
                .detailsPage(detailsPageConfig)
                .build();

        final String keycloakId = "unique_id";
        AppUser user = appUserService.getOrCreateUser(keycloakId, "any@any.com", "name", "lastname");

        storeConfigService.updateStoreConfig(user, storeConfig2);
    }

    @Test
    @Transactional
    public void testUpdateStoreConfig_fail_different_core_config() {
        CoreConfig coreConfig = CoreConfig.builder()
                .flexibility(false)
                .build();
        MainPageConfig mainPageConfig = MainPageConfig.builder().build();
        DetailsPageConfig detailsPageConfig = DetailsPageConfig.builder().build();
        StoreConfig storeConfig = StoreConfig.builder()
                .core(coreConfig)
                .mainPage(mainPageConfig)
                .detailsPage(detailsPageConfig)
                .build();

        Long storeId = storeConfigService.createStoreConfig(storeConfig).getStoreConfigId();
        assertThat(storeId).isNotNull();

        CoreConfig coreConfig2 = CoreConfig.builder()
                .coreConfigId(storeConfig.getCore().getCoreConfigId())
                .flexibility(true)
                .build();
        StoreConfig storeConfig2 = StoreConfig.builder()
                .storeConfigId(storeId)
                .core(coreConfig2)
                .mainPage(mainPageConfig)
                .detailsPage(detailsPageConfig)
                .build();

        final String keycloakId = "unique_id";
        AppUser user = appUserService.getOrCreateUser(keycloakId, "any@any.com", "name", "lastname");

        assertThrows(IllegalArgumentException.class, () -> storeConfigService.updateStoreConfig(user, storeConfig2));
    }

}
