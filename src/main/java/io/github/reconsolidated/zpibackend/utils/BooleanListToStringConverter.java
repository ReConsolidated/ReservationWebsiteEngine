package io.github.reconsolidated.zpibackend.utils;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Converter
public class BooleanListToStringConverter implements AttributeConverter<List<Boolean>, String> {

    private static final String SEPARATOR = ",";
    @Override
    public String convertToDatabaseColumn(List<Boolean> attribute) {
        return attribute == null ? null : StringUtils.join(attribute, SEPARATOR);
    }

        @Override
        public List<Boolean> convertToEntityAttribute(String dbData) {
            if (StringUtils.isBlank(dbData)) {
                return Collections.emptyList();
            }

            try (Stream<String> stream = Arrays.stream(dbData.split(SEPARATOR))) {
                return stream.map(Boolean::parseBoolean).collect(Collectors.toList());
            }
        }
    }
