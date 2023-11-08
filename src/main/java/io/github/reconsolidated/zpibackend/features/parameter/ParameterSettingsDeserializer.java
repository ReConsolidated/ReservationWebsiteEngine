package io.github.reconsolidated.zpibackend.features.parameter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParameterSettingsDeserializer extends JsonDeserializer<ParameterSettings> {
    @Override
    public ParameterSettings deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        ParameterType type = ParameterType.valueOf(node.get("dataType").asText().toUpperCase());

        ParameterSettings base = ParameterSettings.builder()
                .name(node.get("name").asText())
                .dataType(type)
                .isRequired(node.get("isRequired") != null && node.get("isRequired").asBoolean())
                .isFilterable(node.get("isFilterable") != null && node.get("isFilterable").asBoolean())
                .showMainPage(node.get("showMainPage") != null && node.get("showMainPage").asBoolean())
                .showDetailsPage(node.get("showDetailsPage") != null && node.get("showDetailsPage").asBoolean())
                .build();
        switch (type) {
            case STRING -> {
                List<String> possibleValues = new ArrayList<>();
                boolean limitValues = node.get("limitValues") != null && node.get("limitValues").asBoolean();
                if (limitValues) {
                    Iterator<JsonNode> iterator = node.get("possibleValues").elements();
                    while (iterator.hasNext()) {
                        possibleValues.add(iterator.next().asText());
                    }
                    base.setDataType(ParameterType.LIST);
                }
                return new  ParameterStringSettings(base, limitValues, possibleValues);
            }
            case BOOLEAN -> {
                return new ParameterBooleanSettings(base);
            }
            case NUMBER -> {
                String unit = node.get("unit") != null ? node.get("unit").asText() : "";
                Integer minValue = node.get("minValue") != null ? node.get("minValue").asInt() : null;
                Integer maxValue = node.get("maxValue") != null ? node.get("maxValue").asInt() : null;

                return new ParameterNumberSettings(base, unit, maxValue, minValue);
            }
            default -> {
                return base;
            }
        }
    }
}
