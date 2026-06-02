package edu.harvard.dbmi.avillach.visualization.processing;

import edu.harvard.dbmi.avillach.visualization.model.CategoricalDistributionData;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class CategoricalDistributionProcessor {

    public List<CategoricalDistributionData> process(Map<String, Map<String, String>> crossCounts, boolean obfuscated) {
        List<CategoricalDistributionData> distributions = new ArrayList<>();
        for (Map.Entry<String, Map<String, String>> entry : crossCounts.entrySet()) {
            if (DistributionMetadata.SKIP_KEYS.contains(entry.getKey()) || entry.getValue() == null || entry.getValue().isEmpty()) {
                continue;
            }
            String title = DistributionMetadata.titleFor(entry.getKey());
            distributions.add(
                new CategoricalDistributionData(
                    entry.getKey(), title, false, new LinkedHashMap<>(entry.getValue()), obfuscated,
                    DistributionMetadata.xAxisLabelFor(title), "Number of Participants", null, null
                )
            );
        }
        return distributions;
    }
}
