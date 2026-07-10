package edu.harvard.dbmi.avillach.visualization.service;

import edu.harvard.dbmi.avillach.visualization.model.AccessType;
import org.springframework.stereotype.Component;

/**
 * Decides AUTHORIZED vs OPEN treatment for a distributions request. The access type comes from the gateway's identity propagation, NOT from
 * the request body: the {@code X-User-Id} header is gateway-owned (the gateway strips any client-supplied value and only injects it after
 * successful PSAMA introspection), so its presence proves an authenticated caller and its absence means the request passed open-access
 * validation instead. Clients cannot spoof it.
 */
@Component
public class HpdsAccessResolver {

    public AccessType resolve(String gatewayUserId) {
        return gatewayUserId != null && !gatewayUserId.isBlank() ? AccessType.AUTHORIZED : AccessType.OPEN;
    }
}
