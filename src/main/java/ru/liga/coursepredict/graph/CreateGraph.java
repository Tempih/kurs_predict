package ru.liga.coursepredict.graph;

import lombok.extern.slf4j.Slf4j;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import ru.liga.coursepredict.exceptions.IncorrectDateFormatException;
import ru.liga.coursepredict.model.PredictResult;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Slf4j
public class CreateGraph {
    private static final String DATE_FORMAT = "dd.MM.yyyy";
    public static final Integer WIDTH = 1920;
    public static final Integer HEIGHT = 1080;
    private static final String TITLE = "Зависимость валют";
    private static final String Y_AXES_PATTERN = "Р #.##";
    private static final String X_LABEL = "Дата";
    private static final String Y_LABEL = "Курс";

    public XYChart createGraph(List<PredictResult> predictResultList) throws IncorrectDateFormatException {
        log.debug("Начинаем рисовать график");
        XYChart chart = new XYChartBuilder().width(WIDTH).height(HEIGHT)
                .title(TITLE)
                .xAxisTitle(X_LABEL)
                .yAxisTitle(Y_LABEL)
                .build();

        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        chart.getStyler().setYAxisDecimalPattern(Y_AXES_PATTERN);
        chart.getStyler().setDatePattern(DATE_FORMAT);

        log.debug("Формируем списки переменных для графика");
        DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        for (PredictResult result : predictResultList) {
            log.debug("Формируем список для {}", result.getCurrency());
            List<Date> dates = new ArrayList<>();
            List<Double> course = new ArrayList<>();
            for (int i = 0; i < result.getPredictedCurrency().size(); i++) {
                try {
                    dates.add(formatter.parse(result.getDates().get(i)));
                } catch (ParseException e) {
                   throw new IncorrectDateFormatException();
                }
                course.add(Double.parseDouble(result.getPredictedCurrency().get(i).toString()));
            }
            chart.addSeries(result.getCurrency().toUpperCase(), dates, course);
            log.debug("Закончили формировать список для {}", result.getCurrency());
        }
        log.debug("Закончили рисовать график");

        return chart;
    }
}
