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


import com.smoothstack.investment_orchestrator.dto.InvestmentPortfolioDTO;
import com.smoothstack.investment_orchestrator.model.InvestmentPortfolio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvestmentPortfolioMapper {

    @Mapping(target = "investmentPortfolioId", ignore = true)
    InvestmentPortfolio investmentPortfolioDTOToInvestmentPortfolio(InvestmentPortfolioDTO investmentPortfolioDTO);

    @Mapping(target = "investmentPortfolioId", source = "investmentPortfolioId")
    InvestmentPortfolioDTO investmentPortfolioToInvestmentPortfolioDTO(InvestmentPortfolio investmentPortfolio);
}
