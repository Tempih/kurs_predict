package ru.liga.curspredict.structure;

import java.math.BigDecimal;

public class CursTable {
    private final Integer nominal;
    private final String date;
    private final BigDecimal curs;
    private final String cdx;

    public CursTable(Integer nominal, String date, BigDecimal curs, String cdx) {
        this.nominal = nominal;
        this.date = date;
        this.curs = curs;
        this.cdx = cdx;
    }

    public Integer getNominal() {
        return nominal;
    }

    public String getDate() {
        return date;
    }

    public BigDecimal getCurs() {
        return curs;
    }

    public String getCdx() {
        return cdx;
    }

}
