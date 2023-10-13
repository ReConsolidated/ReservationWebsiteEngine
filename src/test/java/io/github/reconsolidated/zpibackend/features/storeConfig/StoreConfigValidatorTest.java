package io.github.reconsolidated.zpibackend.features.storeConfig;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class StoreConfigValidatorTest {

    @Autowired
    private StoreConfigValidator storeConfigValidator;

    @Test
    public void testValidateStoreConfig(){

        CoreConfig coreFP = CoreConfig.builder()
                .flexibility(true)
                .periodicity(true)
                .build();
        StoreConfig storeFP = StoreConfig.builder()
                .coreConfig(coreFP)
                .build();
        CoreConfig coreFSr = CoreConfig.builder()
                .flexibility(true)
                .periodicity(false)
                .specificReservation(true)
                .build();
        StoreConfig storeFSr = StoreConfig.builder()
                .coreConfig(coreFSr)
                .build();
        CoreConfig coreU = CoreConfig.builder()
                .flexibility(false)
                .uniqueness(true)
                .build();
        StoreConfig storeU = StoreConfig.builder()
                .coreConfig(coreU)
                .build();
        CoreConfig coreSSr = CoreConfig.builder()
                .flexibility(false)
                .uniqueness(false)
                .simultaneous(false)
                .specificReservation(true)
                .build();
        StoreConfig storeSSr = StoreConfig.builder()
                .coreConfig(coreSSr)
                .build();

        assertThrows(IllegalArgumentException.class,() -> storeConfigValidator.validateStoreConfig(storeFP));
        assertThrows(IllegalArgumentException.class,() -> storeConfigValidator.validateStoreConfig(storeFSr));
        assertThrows(IllegalArgumentException.class,() -> storeConfigValidator.validateStoreConfig(storeU));
        assertThrows(IllegalArgumentException.class,() -> storeConfigValidator.validateStoreConfig(storeSSr));
    }
}
