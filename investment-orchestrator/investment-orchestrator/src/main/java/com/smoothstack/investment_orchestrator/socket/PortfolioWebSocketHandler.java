/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.smoothstack.investment_orchestrator.socket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smoothstack.investment_orchestrator.serviceImpl.PortfolioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class PortfolioWebSocketHandler extends TextWebSocketHandler implements WebSocketService.UpdateListener {
    private final Map<WebSocketSession, Integer> sessionPortfolioMap = new ConcurrentHashMap<>();
    private final PortfolioService portfolioService;
    private final ObjectMapper objectMapper;

    @Autowired
    public PortfolioWebSocketHandler(PortfolioService portfolioService, ObjectMapper objectMapper,
                                     WebSocketService webSocketService) {
        this.portfolioService = portfolioService;
        this.objectMapper = objectMapper;
        webSocketService.registerUpdateListener(this);
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket connection established from client");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(message.getPayload());
        String action = jsonNode.get("action").asText();
        Integer portfolioId = jsonNode.get("portfolioId").asInt();

        if ("subscribe".equals(action)) {
            sessionPortfolioMap.put(session, portfolioId);
            portfolioService.subscribeToPortfolioStocks(portfolioId);
        } else if ("unsubscribe".equals(action)) {
            sessionPortfolioMap.remove(session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionPortfolioMap.remove(session);
    }


    public void sendUpdate(Integer portfolioId, Map<String, Double> prices) {
        sessionPortfolioMap.forEach((session, sessionPortfolioId) -> {
            if (portfolioId.equals(sessionPortfolioId)) {
                try {
                    Map<String, Object> updateMessage = new HashMap<>();
                    updateMessage.put("updates", prices);
                    String update = objectMapper.writeValueAsString(updateMessage);
                    session.sendMessage(new TextMessage(update));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onUpdate(Integer portfolioId, Map<String, Double> prices) {
        sendUpdate(portfolioId, prices);
    }
}