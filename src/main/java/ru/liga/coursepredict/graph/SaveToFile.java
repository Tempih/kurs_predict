package ru.liga.coursepredict.graph;

import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


@Slf4j
public class SaveToFile {
    public static final String PATH_TO_FILE = "XYLineChart.png";
    public static final Integer WIDTH = 1000;
    public static final Integer HEIGHT = 600;

    public static String saveToFile(JFreeChart chart)
            throws IOException {
        log.debug("Начинаем сохранять файл с графиком");

        File imageFile = new File(PATH_TO_FILE);
        int width = WIDTH;
        int height = HEIGHT;

        ChartUtils.saveChartAsPNG(imageFile, chart, width, height);

        log.debug("Закончили сохранять файл с графиком");
        return PATH_TO_FILE;

    }
}
