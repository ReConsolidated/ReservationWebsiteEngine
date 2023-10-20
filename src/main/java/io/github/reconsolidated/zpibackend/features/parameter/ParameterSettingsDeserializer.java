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
                .isRequired(node.get("isRequired").asBoolean(false))
                .isFilterable(node.get("isFilterable").asBoolean(false))
                .showMainPage(node.get("showMainPage").asBoolean(false))
                .showDetailsPage(node.get("showDetailsPage").asBoolean(false))
                .build();
        switch (type) {
            case STRING -> {
                List<String> possibleValues = new ArrayList<>();
                if (node.get("limitValues").asBoolean(false)) {
                    Iterator<JsonNode> iterator = node.get("possibleValues").elements();
                    while (iterator.hasNext()) {
                        possibleValues.add(iterator.next().asText());
                    }
                    base.setDataType(ParameterType.LIST);
                }
                return new  ParameterStringSettings(
                        base, node.get("limitValues").asBoolean(false), possibleValues);
            }
            case BOOLEAN -> {
                return new ParameterBooleanSettings(base);
            }
            case NUMBER -> {
                return new ParameterNumberSettings(
                        base, node.get("unit").asText(), node.get("maxValue").asInt(), node.get("minValue").asInt());
            }
            default -> {
                return base;
            }
        }
    }
}
