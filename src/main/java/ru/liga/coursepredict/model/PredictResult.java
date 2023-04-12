package ru.liga.coursepredict.model;

import java.math.BigDecimal;
import java.util.List;

public class PredictResult {
    private String currency;
    private List<BigDecimal> predictedCurrency;
    private List<String> dates;

    public PredictResult(String currency, List<BigDecimal> predictedCurrency, List<String> dates) {
        this.currency = currency;
        this.predictedCurrency = predictedCurrency;
        this.dates = dates;
    }

    public String getCurrency() {
        return currency;
    }

    public List<BigDecimal> getPredictedCurrency() {
        return predictedCurrency;
    }

    public List<String> getDates() {
        return dates;
    }

    @Override
    public String toString() {
        return currency + ";" + predictedCurrency + ";" + dates;
    }

}

