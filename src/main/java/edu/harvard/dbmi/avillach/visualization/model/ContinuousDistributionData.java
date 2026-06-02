package edu.harvard.dbmi.avillach.visualization.model;

import java.util.Map;

public record ContinuousDistributionData(
    String conceptPath, String title, boolean continuous, Map<String, String> continuousMap, boolean obfuscated, String xaxisName,
    String yaxisName, Integer chartWidth, Integer chartHeight
) {
}
