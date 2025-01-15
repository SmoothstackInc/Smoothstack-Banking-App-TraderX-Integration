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
@Table(name = "investment_portfolio", indexes = {@Index(name = "idx_investment_portfolio_id", columnList =
        "investment_portfolio_id", unique = true)})
public class InvestmentPortfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "investment_portfolio_id")
    private Integer investmentPortfolioId;

    @Column(name = "investor_id")
    private Integer investorId;

    @Column(name = "investment_portfolio_name")
    private String investmentPortfolioName;

    @Column(name = "total_invested")
    private double totalInvested;

    @Column(name = "amt_available")
    private double amtAvailable;

    @Column(name = "date_created")
    private Timestamp dateCreated;

    @Column(name = "date_modified")
    private Timestamp dateModified;

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
            name = "investor_id",
            referencedColumnName = "investor_id",
            insertable = false, updatable = false
    )
    private Investor investor;

    @OneToMany(
            mappedBy = "investmentPortfolio",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @ToString.Exclude
    private List<Position> positions;

    @OneToMany(
            mappedBy = "investmentPortfolio",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<InvestmentPortfolioTransaction> investmentPortfolioTransactions;
}