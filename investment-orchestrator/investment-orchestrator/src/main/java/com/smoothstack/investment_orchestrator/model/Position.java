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

package com.smoothstack.investment_orchestrator.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Data
@Entity
@Table(name = "position", indexes = {@Index(name = "idx_position_id", columnList = "position_id", unique = true)})
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private Integer positionId;


    @Column(name = "investment_portfolio_id")
    private Integer investmentPortfolioId;

    @Column(name = "ticker")
    private String ticker;

    @Column(name = "security_name")
    private String securityName;

    @Column(name = "total_value")
    private double totalValue;

    @Column(name = "total_quantity")
    private Integer totalQuantity;

    @Column(name = "date_created")
    private Timestamp dateCreated;

    @Column(name = "date_modified")
    private Timestamp dateModified;

    public Position(Integer investmentPortfolioId, String ticker, String securityName, double totalValue,
                    Integer totalQuantity) {
    }

    @PrePersist
    protected void onCreate() {
        dateCreated = new Timestamp(System.currentTimeMillis());
        dateModified = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        dateModified = new Timestamp(System.currentTimeMillis());
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "investment_portfolio_id",
            referencedColumnName = "investment_portfolio_id",
            insertable = false, updatable = false
    )
    @ToString.Exclude
    private InvestmentPortfolio investmentPortfolio;


    @OneToMany(
            mappedBy = "position",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<PositionTransaction> positionTransactions;

    @Override
    public String toString() {
        return "Position{" +
                "positionId=" + positionId +
                ", investmentPortfolioId=" + investmentPortfolioId +
                ", ticker='" + ticker + '\'' +
                ", securityName='" + securityName + '\'' +
                ", totalValue=" + totalValue +
                ", totalQuantity=" + totalQuantity +
                ", dateCreated=" + dateCreated +
                ", dateModified=" + dateModified +
                ", investmentPortfolio=" + investmentPortfolio +
                ", positionTransactions=" + positionTransactions +
                '}';
    }
}