package edu.harvard.dbmi.avillach.visualization.processing;

import static org.junit.jupiter.api.Assertions.*;

import edu.harvard.dbmi.avillach.visualization.model.ContinuousDistributionData;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContinuousDistributionProcessorTest {

    private ContinuousDistributionProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new ContinuousDistributionProcessor();
    }

    @Test
    void process_preBinnedStrings_returnsContinuousDistribution() {
        Map<String, Map<String, String>> data = new LinkedHashMap<>();
        data.put(
            "\\measurements\\bmi\\",
            new LinkedHashMap<>(Map.of("18.0 - 24.0", "600", "24.0 - 30.0", "700", "30.0 +", "150"))
        );

        List<ContinuousDistributionData> result = processor.process(data, false);

        assertEquals(1, result.size());
        ContinuousDistributionData distribution = result.get(0);
        assertEquals("\\measurements\\bmi\\", distribution.conceptPath());
        assertEquals("measurements: bmi", distribution.title());
        assertTrue(distribution.continuous());
        assertFalse(distribution.obfuscated());
        assertEquals("600", distribution.continuousMap().get("18.0 - 24.0"));
    }

    @Test
    void process_obfuscatedStrings_passedThroughUnchanged() {
        Map<String, Map<String, String>> data = new LinkedHashMap<>();
        data.put(
            "\\measurements\\bmi\\",
            new LinkedHashMap<>(Map.of("18.0 - 22.0", "500 ±3", "22.0 - 26.0", "< 10"))
        );

        List<ContinuousDistributionData> result = processor.process(data, true);

        assertEquals(1, result.size());
        assertTrue(result.get(0).obfuscated());
        assertEquals("500 ±3", result.get(0).continuousMap().get("18.0 - 22.0"));
        assertEquals("< 10", result.get(0).continuousMap().get("22.0 - 26.0"));
    }

    @Test
    void process_skipsConsentKeysAndEmptySeries() {
        Map<String, Map<String, String>> data = new LinkedHashMap<>();
        data.put("\\_consents\\", Map.of("1.0", "100"));
        data.put("\\empty\\", Map.of());
        data.put("\\measurements\\bmi\\", new LinkedHashMap<>(Map.of("25.0", "100")));

        List<ContinuousDistributionData> result = processor.process(data, false);

        assertEquals(1, result.size());
    }

    @Test
    void process_nullInnerMap_skippedWithoutCrash() {
        Map<String, Map<String, String>> data = new LinkedHashMap<>();
        data.put("\\measurements\\bmi\\", null);
        data.put("\\measurements\\age\\", new LinkedHashMap<>(Map.of("25.0", "100")));

        List<ContinuousDistributionData> result = processor.process(data, false);

        assertEquals(1, result.size());
        assertEquals("\\measurements\\age\\", result.get(0).conceptPath());
    }
}
