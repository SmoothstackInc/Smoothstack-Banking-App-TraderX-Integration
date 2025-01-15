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

package com.smoothstack.live_data.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Stock {
    @Id
    private String symbol;
    private String security;
    private String secFilings;
    private String gicsSector;
    private String gicsSubIndustry;
    private String headquartersLocation;
    private String dateFirstAdded;
    private String cik;
    private String founded;
    private BigDecimal price;

    public Stock(String symbol, String security, String secFilings, String gicsSector,
                 String gicsSubIndustry, String headquartersLocation, String dateFirstAdded,
                 String cik, String founded, BigDecimal price) {
        this.symbol = symbol;
        this.security = security;
        this.secFilings = secFilings;
        this.gicsSector = gicsSector;
        this.gicsSubIndustry = gicsSubIndustry;
        this.headquartersLocation = headquartersLocation;
        this.dateFirstAdded = dateFirstAdded;
        this.cik = cik;
        this.founded = founded;
        this.price = price;
    }
}