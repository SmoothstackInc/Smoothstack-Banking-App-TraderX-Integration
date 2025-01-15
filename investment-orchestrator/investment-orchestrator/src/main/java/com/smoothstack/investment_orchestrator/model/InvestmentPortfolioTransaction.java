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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Data
@Entity
@Table(name = "investment_portfolio_transaction", indexes = {@Index(name = "idx_investment_portfolio_transaction_id",
        columnList = "investment_portfolio_transaction_id", unique = true)})
public class InvestmentPortfolioTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "investment_portfolio_transaction_id")
    private Integer investmentPortfolioTransactionId;

    @Column(name = "investment_portfolio_id")
    private Integer investmentPortfolioId;

    @Column(name = "investor_id")
    private Integer investorId;

    @Column(name = "funds_moved") // amt_invested
    private double fundsMoved;

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
            name = "investment_portfolio_id",
            referencedColumnName = "investment_portfolio_id",
            insertable = false, updatable = false
    )
    private InvestmentPortfolio investmentPortfolio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "investor_id",
            referencedColumnName = "investor_id",
            insertable = false, updatable = false
    )
    private Investor investor;
}