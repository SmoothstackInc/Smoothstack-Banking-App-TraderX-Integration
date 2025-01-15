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

package com.smoothstack.live_data.websocket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smoothstack.live_data.service.RealTimeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;

@Component
public class StockWebSocketHandler extends TextWebSocketHandler {
    @Autowired
    private RealTimeDataService realTimeDataService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("WebSocket connection established for session: " + session.getId());
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(message.getPayload());
        String action = jsonNode.get("action").asText();
        JsonNode symbolsNode = jsonNode.get("symbols");

        if (symbolsNode != null && symbolsNode.isArray()) {
            List<String> symbols = objectMapper.convertValue(symbolsNode, new TypeReference<>() {
            });

            switch (action) {
                case "subscribe":
                    realTimeDataService.addSubscriptions(session, symbols);
                    break;
                case "unsubscribe":
                    realTimeDataService.removeSubscriptions(session, symbols);
                    break;
                default:
                    session.sendMessage(new TextMessage("Unknown action: " + action));
            }
        } else {
            session.sendMessage(new TextMessage("Invalid message format. Expected 'symbols' array."));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        realTimeDataService.removeAllSubscriptions(session);
        System.out.println("WebSocket connection closed for session: " + session.getId());
        System.out.println("Status: " + status.getCode() + " - " + status.getReason());
    }
}