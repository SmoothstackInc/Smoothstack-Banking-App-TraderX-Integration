package com.smoothstack.investment_orchestrator.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class TimeSeriesData {
    private LocalDate date;
    private BigDecimal open;
    private BigDecimal close;
    private BigDecimal high;
    private BigDecimal low;
    private Long volume;

    public TimeSeriesData(LocalDate date, BigDecimal open, BigDecimal close, BigDecimal high, BigDecimal low,
                          Long volume) {
        this.date = date;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.volume = volume;
    }

    public TimeSeriesData() {

    }
}