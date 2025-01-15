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

package com.smoothstack.investment_orchestrator.service;


import com.smoothstack.investment_orchestrator.dto.PositionDTO;
import com.smoothstack.investment_orchestrator.dto.TradeRequestDTO;

import java.util.List;

public interface PositionService {

    List<PositionDTO> getPositionsByParams(Integer positionId);
    List<PositionDTO> getAllPositions();
    List<PositionDTO> getPositionById(Integer positionId);
    PositionDTO createPosition(PositionDTO positionDTO);
    PositionDTO updatePositionById(Integer positionId, PositionDTO updatedPositionDTO);
    void deletePositionById(Integer positionId);
    void processTrade(TradeRequestDTO tradeRequestDTO);
}
