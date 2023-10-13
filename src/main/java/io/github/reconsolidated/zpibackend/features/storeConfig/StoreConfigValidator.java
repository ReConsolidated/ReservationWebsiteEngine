package io.github.reconsolidated.zpibackend.features.storeConfig;

import io.github.reconsolidated.zpibackend.features.parameter.ParameterSettings;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class StoreConfigValidator {

    public void validateStoreConfig(StoreConfig storeConfig) {
        validateCoreConfig(storeConfig.getCoreConfig());
    }
    private void validateCoreConfig(CoreConfig coreConfig) {

        if (coreConfig.getFlexibility()) {
            if (coreConfig.getPeriodicity()) {
                //{ "flexibility": true, "periodicity": true }
                throw new IllegalArgumentException("Impossible combination of core configuration options! flexibility: true + periodicity: true");
            }
            if (coreConfig.getSpecificReservation()) {
                //{ "flexibility": true, "specificReservation": true }
                throw new IllegalArgumentException("Impossible combination of core configuration options! flexibility: true + specificReservation: true");
            }
        } else {
            if (coreConfig.getUniqueness()) {
                //{ "flexibility": false, "uniqueness": true }
                throw new IllegalArgumentException("Impossible combination of core configuration options! flexibility: false + uniqueness: true");
            }
        }
        if (!coreConfig.getSimultaneous()) {
            if (coreConfig.getSpecificReservation()) {
                //{ "simultaneous": false, "specificReservation": true }
                throw new IllegalArgumentException("Impossible combination of core configuration options! simultaneous: false + specificReservation: true");
            }
        }
    }
}
