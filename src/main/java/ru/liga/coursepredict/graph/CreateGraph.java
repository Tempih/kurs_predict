package ru.liga.coursepredict.graph;

import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import ru.liga.coursepredict.structure.PredictResult;

import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static java.lang.Math.min;


@Slf4j
public class CreateGraph {
    private static final String DATE_FORMAT = "dd.MM.yyyy";
    private static final String DOT = "\\.";
    private static final Integer DAYS_INDEX = 0;
    private static final Integer MONTH_INDEX = 1;
    private static final Integer YEAR_INDEX = 2;
    private static final String TITLE = "Зависимость валют";
    private static final String X_LABEL = "Дата";
    private static final String Y_LABEL = "Курс";
    private static final double RECTANGLE_INSETS = 1.0;
    /*
     * void add(RegularTimePeriod period, double value)
     */
    private XYDataset createDataset(List<PredictResult> predictResult) {
        log.debug("Начинаем готовить данные для построения графика");
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        for (PredictResult result : predictResult) {
            TimeSeries s1 = new TimeSeries(result.getCurrency());
            for (int i = 0; i < result.getPredictedCurrency().size(); i++) {
                String[] splitDate = result.getDates().get(i).split(DOT);
                s1.add(new Day(Integer.parseInt(splitDate[DAYS_INDEX]), Integer.parseInt(splitDate[MONTH_INDEX]), Integer.parseInt(splitDate[YEAR_INDEX])), Double.parseDouble(result.getPredictedCurrency().get(i).toString()));
            }
            dataset.addSeries(s1);
        }
        log.debug("Закончили готовить данные для построения графика");
        return dataset;
    }

    public JFreeChart createChart(List<PredictResult> predictResult) {
        XYDataset dataset = createDataset(predictResult);

        log.debug("Начинаем строить график");
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                TITLE,
                X_LABEL,
                Y_LABEL,
                dataset,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        plot.setAxisOffset(new RectangleInsets (RECTANGLE_INSETS, RECTANGLE_INSETS, RECTANGLE_INSETS, RECTANGLE_INSETS));

        ValueAxis axis = plot.getDomainAxis();
        axis.setAxisLineVisible(false);
        axis = plot.getRangeAxis();
        axis.setAxisLineVisible (false);
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint (Color.lightGray);

        DateAxis dateAxis = (DateAxis) plot.getDomainAxis();
        dateAxis.setDateFormatOverride(new SimpleDateFormat(DATE_FORMAT));
        dateAxis.setVerticalTickLabels(true);

        log.debug("Закончили строить график");
        return chart;
    }
}
