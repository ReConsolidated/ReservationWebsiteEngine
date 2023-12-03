package io.github.reconsolidated.zpibackend.domain.parameter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ParameterService {

    private final ParameterRepository parameterRepository;

    public void deleteParameter(Long parameterId) {
        parameterRepository.deleteById(parameterId);
    }

}
