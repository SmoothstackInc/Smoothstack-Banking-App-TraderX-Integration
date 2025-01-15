package com.smoothstack.investment_orchestrator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketDocumentationController {

    @Operation(summary = "Get WebSocket connection information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "WebSocket connection URL")
    })
    @GetMapping("/websocket-info")
    public String getWebSocketInfo() {
        return "ws://localhost:8061/portfolio-updates";
    }
}