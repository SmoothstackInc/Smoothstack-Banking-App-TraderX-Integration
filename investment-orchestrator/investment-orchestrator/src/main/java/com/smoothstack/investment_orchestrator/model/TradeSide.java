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

public enum TradeSide {
    Buy,
    Sell;

    public static TradeSide fromString(String side) {
        if (side == null) {
            throw new IllegalArgumentException("Trade side cannot be null");
        }
        return switch (side.trim().toLowerCase()) {
            case "buy" -> Buy;
            case "sell" -> Sell;
            default -> throw new IllegalArgumentException("Invalid trade side: " + side);
        };
    }
}
