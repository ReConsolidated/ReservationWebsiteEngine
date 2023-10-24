package io.github.reconsolidated.zpibackend.features.storeConfig;

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

        //core: { "flexibility": true, "periodicity": true }
        CoreConfig coreFP = CoreConfig.builder()
                .flexibility(true)
                .periodicity(true)
                .build();
        StoreConfig storeFP = StoreConfig.builder()
                .core(coreFP)
                .build();
        //core: { "flexibility": true, "specificReservation": true }
        CoreConfig coreFSr = CoreConfig.builder()
                .flexibility(true)
                .periodicity(false)
                .specificReservation(true)
                .build();
        StoreConfig storeFSr = StoreConfig.builder()
                .core(coreFSr)
                .build();
        //core: { "flexibility": false, "uniqueness": true }
        CoreConfig coreFU = CoreConfig.builder()
                .flexibility(false)
                .uniqueness(true)
                .build();
        StoreConfig storeFU = StoreConfig.builder()
                .core(coreFU)
                .build();
        //core: { "simultaneous": false, "specificReservation": true }
        CoreConfig coreSSr = CoreConfig.builder()
                .flexibility(false)
                .uniqueness(false)
                .simultaneous(false)
                .specificReservation(true)
                .build();
        StoreConfig storeSSr = StoreConfig.builder()
                .core(coreSSr)
                .build();

        assertThrows(IllegalArgumentException.class,() -> storeConfigValidator.validateStoreConfig(storeFP));
        assertThrows(IllegalArgumentException.class,() -> storeConfigValidator.validateStoreConfig(storeFSr));
        assertThrows(IllegalArgumentException.class,() -> storeConfigValidator.validateStoreConfig(storeFU));
        assertThrows(IllegalArgumentException.class,() -> storeConfigValidator.validateStoreConfig(storeSSr));
    }
}
