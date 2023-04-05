package ru.liga.coursepredict.graph;

import lombok.extern.slf4j.Slf4j;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;

import java.io.IOException;


@Slf4j
public class SaveToFile {
    public static final String PATH_TO_FILE = "XYLineChart.png";

    public static String saveToFile(XYChart chart)
            throws IOException {
        log.debug("Сохраняем в файл");
        BitmapEncoder.saveBitmap(chart, PATH_TO_FILE, BitmapEncoder.BitmapFormat.PNG);
        log.debug("Сохранили в файл: {}", PATH_TO_FILE);
        return PATH_TO_FILE;

    }
}
