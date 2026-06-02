package edu.harvard.dbmi.avillach.visualization.processing;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CategoricalAggregationServiceTest {

    private CategoricalAggregationService service;

    @BeforeEach
    void setUp() {
        service = new CategoricalAggregationService(7);
    }

    @Test
    void aggregateTopN_moreThanMaxCategories_createsOtherBucket() {
        Map<String, Integer> categories = new LinkedHashMap<>();
        categories.put("Cat1", 100);
        categories.put("Cat2", 90);
        categories.put("Cat3", 80);
        categories.put("Cat4", 70);
        categories.put("Cat5", 60);
        categories.put("Cat6", 50);
        categories.put("Cat7", 40);
        categories.put("Cat8", 30);
        categories.put("Cat9", 20);

        Map<String, Integer> result = service.aggregateTopN(categories);

        assertTrue(result.containsKey("Other"));
        assertEquals(50, result.get("Other"));
    }

    @Test
    void aggregateTopN_exactlyOneOverMax_createsOtherBucket() {
        Map<String, Integer> categories = new LinkedHashMap<>();
        categories.put("Cat1", 100);
        categories.put("Cat2", 90);
        categories.put("Cat3", 80);
        categories.put("Cat4", 70);
        categories.put("Cat5", 60);
        categories.put("Cat6", 50);
        categories.put("Cat7", 40);
        categories.put("Cat8", 30);

        Map<String, Integer> result = service.aggregateTopN(categories);

        assertTrue(result.containsKey("Other"));
        assertEquals(30, result.get("Other"));
        assertEquals(8, result.size());
    }

    @Test
    void aggregateTopN_exactlyAtMax_noOtherBucket() {
        Map<String, Integer> categories = new LinkedHashMap<>();
        categories.put("Cat1", 100);
        categories.put("Cat2", 90);
        categories.put("Cat3", 80);
        categories.put("Cat4", 70);
        categories.put("Cat5", 60);
        categories.put("Cat6", 50);
        categories.put("Cat7", 40);

        Map<String, Integer> result = service.aggregateTopN(categories);

        assertFalse(result.containsKey("Other"));
        assertEquals(7, result.size());
    }

    @Test
    void aggregateTopN_truncatesLongKeys() {
        String longKey = "a".repeat(60);
        Map<String, Integer> categories = new LinkedHashMap<>();
        categories.put(longKey, 100);

        Map<String, Integer> result = service.aggregateTopN(categories);

        assertFalse(result.containsKey(longKey));
        assertTrue(
            result.keySet().iterator().next().length() <= 48,
            "Long keys should be truncated"
        );
    }

    @Test
    void aggregateTopN_nullInput_returnsEmptyMap() {
        Map<String, Integer> result = service.aggregateTopN(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void aggregateTopN_emptyInput_returnsEmptyMap() {
        Map<String, Integer> result = service.aggregateTopN(new java.util.LinkedHashMap<>());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
