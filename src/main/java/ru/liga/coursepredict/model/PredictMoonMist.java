package ru.liga.coursepredict.model;

import java.math.BigDecimal;

public class PredictMoonMist {
    private final BigDecimal course;
    private final String date;

    public PredictMoonMist(BigDecimal course, String date) {
        this.course = course;
        this.date = date;
    }


    public BigDecimal getCourse() {
        return course;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "PredictMoonMist{" +
                "course=" + course +
                ", date='" + date + '\'' +
                '}';
    }
}
