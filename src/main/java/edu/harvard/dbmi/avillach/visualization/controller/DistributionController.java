package edu.harvard.dbmi.avillach.visualization.controller;

import edu.harvard.dbmi.avillach.visualization.logging.AuditLoggingContext;
import edu.harvard.dbmi.avillach.visualization.model.AccessType;
import edu.harvard.dbmi.avillach.visualization.model.DistributionRequest;
import edu.harvard.dbmi.avillach.visualization.model.VisualizationResponse;
import edu.harvard.dbmi.avillach.visualization.service.HpdsAccessResolver;
import edu.harvard.dbmi.avillach.visualization.service.VisualizationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DistributionController {

    private final VisualizationService visualizationService;
    private final HpdsAccessResolver hpdsAccessResolver;

    public DistributionController(VisualizationService visualizationService, HpdsAccessResolver hpdsAccessResolver) {
        this.visualizationService = visualizationService;
        this.hpdsAccessResolver = hpdsAccessResolver;
    }

    @PostMapping("/distributions")
    public ResponseEntity<VisualizationResponse> distributions(
        @Valid @RequestBody DistributionRequest request, @RequestHeader(value = "Authorization", required = false) String authorization,
        @RequestHeader(value = "X-User-Id", required = false) String gatewayUserId, HttpServletRequest servletRequest
    ) {
        AccessType accessType = hpdsAccessResolver.resolve(gatewayUserId);
        AuditLoggingContext.addDistributionRequestMetadata(
            servletRequest, accessType.getValue(), request.query(), visualizationService.subQueryCount(request.query())
        );
        VisualizationResponse response = visualizationService
            .generateDistributions(request.query(), accessType, authorization, AuditLoggingContext.requestId(servletRequest));
        AuditLoggingContext.addDistributionResponseMetadata(servletRequest, response);
        return ResponseEntity.ok(response);
    }
}
