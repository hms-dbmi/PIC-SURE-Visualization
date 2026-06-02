package edu.harvard.dbmi.avillach.visualization.processing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CategoricalAggregationService {

    private static final int MAX_LABEL_LENGTH = 45;

    private final int maxCategories;

    public CategoricalAggregationService(
        @Value("${distribution.categorical.max-categories:${chart.categorical.max-categories:7}}") int maxCategories
    ) {
        this.maxCategories = maxCategories;
    }

    public Map<String, Integer> aggregateTopN(Map<String, Integer> axisMap) {
        if (axisMap == null || axisMap.isEmpty()) {
            return new LinkedHashMap<>();
        }
        Map<String, Integer> finalAxisMap = axisMap;
        if (axisMap.size() > maxCategories) {
            Supplier<Stream<Map.Entry<String, Integer>>> stream =
                () -> finalAxisMap.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
            int otherSum = stream.get().skip(maxCategories).mapToInt(Map.Entry::getValue).sum();
            axisMap = stream.get().limit(maxCategories)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
            axisMap = limitKeySize(axisMap).entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
            axisMap.put("Other", otherSum);
        } else {
            axisMap = limitKeySize(finalAxisMap).entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
        }
        return axisMap;
    }

    private static Map<String, Integer> limitKeySize(Map<String, Integer> axisMap) {
        Map<String, Integer> newAxisMap = new LinkedHashMap<>();
        Set<String> keys = new HashSet<>();
        axisMap.forEach((key, value) -> {
            String adjustedKey = key.length() < MAX_LABEL_LENGTH ? key : createAdjustedKey(axisMap, keys, key);
            newAxisMap.put(adjustedKey, value);
            keys.add(adjustedKey);
        });
        return newAxisMap;
    }

    private static String createAdjustedKey(Map<String, Integer> axisMap, Set<String> keys, String key) {
        String keyPrefix = key.substring(0, MAX_LABEL_LENGTH);
        boolean prefixExists = axisMap.keySet().stream().anyMatch(k -> k.startsWith(keyPrefix));
        if (prefixExists) {
            int countFromEnd = 6;
            String proposedKey;
            do {
                proposedKey = String
                    .format("%s...%s", key.substring(0, MAX_LABEL_LENGTH - 3 - countFromEnd), key.substring(key.length() - countFromEnd));
                countFromEnd++;
            } while (keys.contains(proposedKey));
            return proposedKey;
        }
        return keyPrefix + "...";
    }
}
