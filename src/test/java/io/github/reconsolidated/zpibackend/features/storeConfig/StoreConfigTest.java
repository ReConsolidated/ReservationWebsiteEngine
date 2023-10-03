package io.github.reconsolidated.zpibackend.features.storeConfig;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.authentication.appUser.AppUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
        LayoutConfig layoutConfig = LayoutConfig.builder().build();
        ItemConfig itemConfig = ItemConfig.builder().build();
        StoreConfig storeConfig = StoreConfig.builder()
                .coreConfig(coreConfig)
                .layoutConfig(layoutConfig)
                .itemConfig(itemConfig)
                .build();

        Long storeId = storeConfigService.createStoreConfig(storeConfig).getStoreConfigId();
        assertThat(storeId).isNotNull();
    }

    @Test
    @Transactional
    public void testCreateStoreConfig_fail_when_id_given() {
        CoreConfig coreConfig = CoreConfig.builder().build();
        LayoutConfig layoutConfig = LayoutConfig.builder().build();
        ItemConfig itemConfig = ItemConfig.builder().build();
        StoreConfig storeConfig = StoreConfig.builder()
                .storeConfigId(1L)
                .coreConfig(coreConfig)
                .layoutConfig(layoutConfig)
                .itemConfig(itemConfig)
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            storeConfigService.createStoreConfig(storeConfig);
        });
    }

    @Test
    @Transactional
    public void testFetchStoreConfigs() {
        CoreConfig coreConfig = CoreConfig.builder().build();
        LayoutConfig layoutConfig = LayoutConfig.builder().build();
        ItemConfig itemConfig = ItemConfig.builder().build();
        StoreConfig storeConfig = StoreConfig.builder()
                .coreConfig(coreConfig)
                .layoutConfig(layoutConfig)
                .itemConfig(itemConfig)
                .build();

        Long storeId = storeConfigService.createStoreConfig(storeConfig).getStoreConfigId();
        assertThat(storeId).isNotNull();

        final String keycloakId = "unique_id";
        AppUser user = appUserService.getOrCreateUser(keycloakId, "any@any.com", "name", "lastname");

        StoreConfigsListDto dto = storeConfigService.listStoreConfigs(user);
        assertThat(dto.getStoreConfigList().size()).isEqualTo(1);
    }
}
