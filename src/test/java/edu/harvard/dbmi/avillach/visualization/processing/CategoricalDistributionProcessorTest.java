package edu.harvard.dbmi.avillach.visualization.processing;

import static org.junit.jupiter.api.Assertions.*;

import edu.harvard.dbmi.avillach.visualization.model.CategoricalDistributionData;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CategoricalDistributionProcessorTest {

    private CategoricalDistributionProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new CategoricalDistributionProcessor();
    }

    @Test
    void process_simpleCategoricalData_returnsDistributionData() {
        Map<String, Map<String, String>> data = new LinkedHashMap<>();
        data.put(
            "\\demographics\\race\\",
            new LinkedHashMap<>(Map.of("White", "45000", "Black", "12000", "Asian", "8000"))
        );

        List<CategoricalDistributionData> result = processor.process(data, false);

        assertEquals(1, result.size());
        CategoricalDistributionData distribution = result.get(0);
        assertEquals("\\demographics\\race\\", distribution.conceptPath());
        assertEquals("demographics: race", distribution.title());
        assertFalse(distribution.continuous());
        assertFalse(distribution.obfuscated());
        assertEquals("45000", distribution.categoricalMap().get("White"));
        assertEquals("race", distribution.xaxisName());
        assertEquals("Number of Participants", distribution.yaxisName());
    }

    @Test
    void process_obfuscatedStrings_passedThroughUnchanged() {
        Map<String, Map<String, String>> data = new LinkedHashMap<>();
        data.put(
            "\\demographics\\race\\",
            new LinkedHashMap<>(Map.of("White", "45000 ±3", "Black", "< 10"))
        );

        List<CategoricalDistributionData> result = processor.process(data, true);

        assertEquals(1, result.size());
        assertTrue(result.get(0).obfuscated());
        assertEquals("45000 ±3", result.get(0).categoricalMap().get("White"));
        assertEquals("< 10", result.get(0).categoricalMap().get("Black"));
    }

    @Test
    void process_skipsConsentKeysAndEmptySeries() {
        Map<String, Map<String, String>> data = new LinkedHashMap<>();
        data.put("\\_consents\\", Map.of("consent1", "100"));
        data.put("\\_harmonized_consent\\", Map.of("consent2", "200"));
        data.put("\\empty\\", Map.of());
        data.put("\\demographics\\race\\", new LinkedHashMap<>(Map.of("White", "45000")));

        List<CategoricalDistributionData> result = processor.process(data, false);

        assertEquals(1, result.size());
    }

    @Test
    void process_nullInnerMap_skippedWithoutCrash() {
        Map<String, Map<String, String>> data = new LinkedHashMap<>();
        data.put("\\demographics\\race\\", null);
        data.put("\\demographics\\sex\\", new LinkedHashMap<>(Map.of("Female", "100")));

        List<CategoricalDistributionData> result = processor.process(data, false);

        assertEquals(1, result.size());
        assertEquals("\\demographics\\sex\\", result.get(0).conceptPath());
    }

    @Test
    void metadata_extractsTitleAndXAxisLabel() {
        String title = DistributionMetadata.titleFor("\\demographics\\race\\");
        assertEquals("demographics: race", title);
        assertEquals("race", DistributionMetadata.xAxisLabelFor(title));
        assertEquals("race", DistributionMetadata.titleFor("race"));
    }
}
