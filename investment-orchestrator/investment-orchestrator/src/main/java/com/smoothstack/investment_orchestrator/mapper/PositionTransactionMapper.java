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

package com.smoothstack.investment_orchestrator.mapper;

import com.smoothstack.investment_orchestrator.dto.PositionTransactionDTO;
import com.smoothstack.investment_orchestrator.model.PositionTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PositionTransactionMapper {

    @Mapping(target = "positionTransactionId", ignore = true)
    PositionTransaction positionTransactionDTOToPositionTransaction(PositionTransactionDTO positionTransactionDTO);

    @Mapping(target = "positionTransactionId", source = "positionTransactionId")
    PositionTransactionDTO positionTransactionToPositionTransactionDTO(PositionTransaction positionTransaction);
}
