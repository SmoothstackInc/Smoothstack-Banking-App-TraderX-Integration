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

package com.smoothstack.live_data.service;

import com.smoothstack.live_data.dao.StockRepository;
import com.smoothstack.live_data.dto.MetaDataDTO;
import com.smoothstack.live_data.model.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetaDataService {
    @Autowired
    private final StockRepository sr;

    public MetaDataService(StockRepository sr) {
        this.sr = sr;
    }

    public MetaDataDTO getTimeSeriesForSymbol(String symbol) {
        Stock stock = sr.findBySymbol(symbol);
        return new MetaDataDTO(stock.getSymbol(), stock.getSecurity(), stock.getSecFilings(),
                stock.getGicsSector(), stock.getGicsSubIndustry(), stock.getHeadquartersLocation(),
                stock.getDateFirstAdded(), stock.getCik(), stock.getFounded());
    }
}