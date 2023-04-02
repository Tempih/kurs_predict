package ru.liga.coursepredict.structure;

import java.math.BigDecimal;

public class PredictMoonMist {
    private BigDecimal course;
    private String date;

    public PredictMoonMist(BigDecimal course, String date) {
        this.course = course;
        this.date = date;
    }


    public BigDecimal getCourse() {
        return course;
    }

    public String  getDate() {
        return date;
    }

}
