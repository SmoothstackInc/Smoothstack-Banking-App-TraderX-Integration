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

package com.smoothstack.live_data.controller;

import com.smoothstack.live_data.dto.MetaDataDTO;
import com.smoothstack.live_data.dto.TimeSeriesDTO;
import com.smoothstack.live_data.service.HistoricalDataService;
import com.smoothstack.live_data.service.MetaDataService;
import com.smoothstack.live_data.websocket.StockWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@RestController
@RequestMapping("/api/v1/stocks")
@Configuration
@EnableWebSocket
public class StockController implements WebSocketConfigurer {
    @Autowired
    private StockWebSocketHandler stockWebSocketHandler;

    @Autowired
    private HistoricalDataService hds;

    @Autowired
    private MetaDataService mds;

    @GetMapping("/history/{symbol}")
    public TimeSeriesDTO getHistoricalData(@PathVariable String symbol) {
        return hds.getTimeSeriesForSymbol(symbol);
    }

    @GetMapping("/meta-data/{symbol}")
    public MetaDataDTO getMetaData(@PathVariable String symbol) {
        return mds.getTimeSeriesForSymbol(symbol);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(stockWebSocketHandler, "/ws/stocks").setAllowedOrigins("*");
    }
}