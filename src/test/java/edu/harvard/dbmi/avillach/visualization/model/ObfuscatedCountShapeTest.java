package edu.harvard.dbmi.avillach.visualization.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

/**
 * Pins the JSON wire shape that the visualization resource exchanges with
 * aggregate-data-sharing. If either side renames a field, this test fails
 * BEFORE the cross-repo contract silently breaks in production.
 */
class ObfuscatedCountShapeTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializesAsCountAndDisplayFields() throws JsonProcessingException {
        ObfuscatedCount value = new ObfuscatedCount(222, "222 ±3");

        String json = mapper.writeValueAsString(value);

        assertEquals("{\"count\":222,\"display\":\"222 ±3\"}", json);
    }

    @Test
    void deserializesFromAggSideShape() throws JsonProcessingException {
        String aggSideJson = "{\"count\":9,\"display\":\"< 10\"}";

        ObfuscatedCount value = mapper.readValue(aggSideJson, ObfuscatedCount.class);

        assertEquals(9, value.count());
        assertEquals("< 10", value.display());
    }

    @Test
    void ofInt_factory_producesStringifiedDisplay() {
        ObfuscatedCount value = ObfuscatedCount.ofInt(45000);

        assertEquals(45000, value.count());
        assertEquals("45000", value.display());
    }
}
