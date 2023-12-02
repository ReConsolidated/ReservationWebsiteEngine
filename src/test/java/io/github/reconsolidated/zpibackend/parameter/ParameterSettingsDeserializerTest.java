package io.github.reconsolidated.zpibackend.parameter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.reconsolidated.zpibackend.domain.parameter.ParameterSettings;
import io.github.reconsolidated.zpibackend.domain.parameter.ParameterSettingsDeserializer;
import io.github.reconsolidated.zpibackend.domain.parameter.ParameterStringSettings;
import io.github.reconsolidated.zpibackend.domain.parameter.ParameterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParameterSettingsDeserializerTest {

    @Mock
    private JsonParser jsonParser;
    @Mock
    private DeserializationContext deserializationContext;
    @Mock
    private JsonNode rootNode;
    @Mock
    private ObjectCodec objectCodec;

    private ParameterSettingsDeserializer deserializer;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        deserializer = new ParameterSettingsDeserializer();
        when(jsonParser.getCodec()).thenReturn(objectCodec);
    }

    @Test
    public void testDeserializeStringParameter() throws IOException {
        when(objectCodec.readTree(jsonParser)).thenReturn(rootNode);
        when(jsonParser.getCodec().readTree(jsonParser)).thenReturn(rootNode);
        when(rootNode.get(anyString())).thenReturn(rootNode);
        when(rootNode.get("dataType").asText()).thenReturn("string");
        when(rootNode.get("name").asText()).thenReturn(ParameterType.STRING.name());
        when(rootNode.get("isRequired").asBoolean()).thenReturn(true);

        JsonNode possibleValuesNode = mock(JsonNode.class);
        when(rootNode.get("possibleValues")).thenReturn(possibleValuesNode);
        when(rootNode.get("limitValues").asBoolean()).thenReturn(true);

        List<JsonNode> possibleValuesList = List.of(
                mockTextNode("Value1"),
                mockTextNode("Value2")
        );
        when(possibleValuesNode.elements()).thenReturn(possibleValuesList.iterator());

        ParameterSettings result = deserializer.deserialize(jsonParser, deserializationContext);

        assertTrue(result instanceof ParameterStringSettings);
        ParameterStringSettings stringSettings = (ParameterStringSettings) result;
        assertTrue(stringSettings.getLimitValues());
        assertEquals(List.of("Value1", "Value2"), stringSettings.getPossibleValues());
    }

    private JsonNode mockTextNode(String text) {
        JsonNode node = mock(JsonNode.class);
        when(node.asText()).thenReturn(text);
        return node;
    }

}

