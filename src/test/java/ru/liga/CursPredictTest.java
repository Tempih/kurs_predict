package ru.liga;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.liga.coursepredict.formatter.Formatter;
import ru.liga.coursepredict.parser.Parser;
import ru.liga.coursepredict.structure.CourseTable;
import ru.liga.coursepredict.structure.PredictResult;
import ru.liga.coursepredict.system.StageControl;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CursPredictTest {
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    StageControl stageControl = new StageControl();
    String input;
    Formatter formatter = new Formatter();
    Formatter formatterMock = Mockito.spy(Formatter.class);
    Parser parser = new Parser();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    public void checkUsdWeekAlgAvg() {
        input = "rate USD -period week -alg avg";

        assertThat(stageControl.startProgram(input, 1L))
                .contains("Сб 18.03.2023 - 75,72")
                .contains("Вс 19.03.2023 - 75,76")
                .contains("Пн 20.03.2023 - 75,74")
                .contains("Вт 21.03.2023 - 75,72")
                .contains("Ср 22.03.2023 - 75,75")
                .contains("Чт 23.03.2023 - 75,84")
                .contains("Пт 24.03.2023 - 75,85");
    }

    @Test
    public void checkUsdMonthAlgAvg() {
        input = "rate USD -period month -alg avg";
        assertThat(stageControl.startProgram(input, 1L))
                .isEqualTo("""
                        USD
                        Сб 18.03.2023 - 75,72
                        Вс 19.03.2023 - 75,76
                        Пн 20.03.2023 - 75,74
                        Вт 21.03.2023 - 75,72
                        Ср 22.03.2023 - 75,75
                        Чт 23.03.2023 - 75,84
                        Пт 24.03.2023 - 75,85
                        Сб 25.03.2023 - 75,77
                        Вс 26.03.2023 - 75,77
                        Пн 27.03.2023 - 75,77
                        Вт 28.03.2023 - 75,77
                        Ср 29.03.2023 - 75,77
                        Чт 30.03.2023 - 75,77
                        Пт 31.03.2023 - 75,77
                        Сб 01.04.2023 - 75,77
                        Вс 02.04.2023 - 75,77
                        Пн 03.04.2023 - 75,77
                        Вт 04.04.2023 - 75,77
                        Ср 05.04.2023 - 75,77
                        Чт 06.04.2023 - 75,77
                        Пт 07.04.2023 - 75,77
                        Сб 08.04.2023 - 75,77
                        Вс 09.04.2023 - 75,77
                        Пн 10.04.2023 - 75,77
                        Вт 11.04.2023 - 75,77
                        Ср 12.04.2023 - 75,77
                        Чт 13.04.2023 - 75,77
                        Пт 14.04.2023 - 75,77
                        Сб 15.04.2023 - 75,77
                        Вс 16.04.2023 - 75,77
                        """);

    }

    @Test
    public void checkUsdTomorrowAlgAvg() {
        input = "rate USD -period tomorrow -alg avg";
        assertThat(stageControl.startProgram(input, 1L))
                .contains("Сб 18.03.2023 - 75,72");
    }

    @Test
    public void checkEurWeek() {
        input = "rate EUR -period week -alg moon";
        assertThat(stageControl.startProgram(input, 1L))
                .contains("""
                        Сб 18.03.2023 - 115,93
                        Вс 19.03.2023 - 114,39
                        Пн 20.03.2023 - 114,39
                        Вт 21.03.2023 - 114,39
                        Ср 22.03.2023 - 115,60
                        Чт 23.03.2023 - 114,78
                        Пт 24.03.2023 - 113,26
                        """);
    }

    //
    @Test
    public void checkEurTomorrow() {
        input = "rate EUR -period tomorrow -alg moon";
        assertThat(stageControl.startProgram(input, 1L)).contains("Сб 18.03.2023 - 115,93");
    }

    //
//
    @Test
    public void checkRandomCorrect() {
        Mockito.when(formatterMock.randomYear(2005, 2023)).thenReturn(2019);
        List<String> input = new ArrayList<>();
        input.add("12.03.2023");
        input.add("12.03.2024");
        List<String> result = new ArrayList<>();
        result.add("12.03.2019");
        result.add("12.03.2019");
        List<String> res = formatterMock.randomYearForDate(input, 2005, 2023);
        assertEquals(res, result);
    }

    @Test
    public void checkCurrencyUsdSelect() {
        assertThat(stageControl.selectCurrency("usd", 1L).stream().map(CourseTable::toString).toList().toString())
                .contains("1;17.03.2023;76.4095;Доллар США, 1;16.03.2023;75.7457;Доллар США, 1;15.03.2023;75.1927;Доллар США, 1;14.03.2023;75.4609;Доллар США");
    }

    @Test
    public void checkPeriodTomorrowSelect() {
        assertThat(stageControl.selectPeriod("tomorrow", 1L)).isEqualTo(1);
    }

    @Test
    public void checkPredictMoonWithOneCurrency() {
        Map<String, List<CourseTable>> currencyTables = new HashMap<>();
        currencyTables.put("usd", stageControl.selectCurrency("usd", 1L));
        assertThat(stageControl.startPredict(currencyTables, "period", "week", "moon", 1L).stream().map(PredictResult::toString))
                .contains("usd;[104.8012, 103.9524, 103.9524, 103.9524, 104.6819, 104.0741, 103.1618];[18.03.2023, 19.03.2023, 20.03.2023, 21.03.2023, 22.03.2023, 23.03.2023, 24.03.2023]");
    }

    @Test
    public void checkPredictMoonWithTwoCurrency() {
        Map<String, List<CourseTable>> currencyTables = new HashMap<>();
        currencyTables.put("usd", stageControl.selectCurrency("usd", 1L));
        currencyTables.put("try", stageControl.selectCurrency("try", 1L));
        assertThat(stageControl.startPredict(currencyTables, "period", "week", "moon", 1L).stream().map(PredictResult::toString))
                .contains("usd;[104.8012, 103.9524, 103.9524, 103.9524, 104.6819, 104.0741, 103.1618];[18.03.2023, 19.03.2023, 20.03.2023, 21.03.2023, 22.03.2023, 23.03.2023, 24.03.2023]")
                .contains("try;[71.0257, 70.1623, 70.1623, 70.1623, 70.6108, 70.1890, 69.4912];[18.03.2023, 19.03.2023, 20.03.2023, 21.03.2023, 22.03.2023, 23.03.2023, 24.03.2023]");
    }

    @Test
    public void checkCreateOutputList() {
        Map<String, List<CourseTable>> currencyTables = new HashMap<>();
        currencyTables.put("lev", stageControl.selectCurrency("lev", 1L));
        List<PredictResult> predictResultList = stageControl.startPredict(currencyTables, "period", "week", "reg", 1L);
        assertThat(stageControl.startOutputResult(predictResultList, "list"))
                .contains("""
                        LEV
                        Сб 18.03.2023 - 40,91
                        Вс 19.03.2023 - 40,91
                        Пн 20.03.2023 - 40,91
                        Вт 21.03.2023 - 40,91
                        Ср 22.03.2023 - 40,91
                        Чт 23.03.2023 - 40,91
                        Пт 24.03.2023 - 40,91
                                      """);
    }
    @Test
    public void checkCreateOutputGraph() {
        Map<String, List<CourseTable>> currencyTables = new HashMap<>();
        currencyTables.put("lev", stageControl.selectCurrency("lev", 1L));
        currencyTables.put("dram", stageControl.selectCurrency("dram", 1L));
        List<PredictResult> predictResultList = stageControl.startPredict(currencyTables, "period", "week", "reg", 1L);
        assertThat(stageControl.startOutputResult(predictResultList, "graph"))
                .isEqualTo("XYLineChart.png");
    }

    @Test
    public void checkParserFromFileEur(){
        assertThat(parser.getDataFromFile("eur.csv").stream().map(CourseTable::toString))
                .contains("1;17.03.2023;81.1418;Евро")
                .contains("1;01.09.2022;60.2141;Евро");
    }

    @Test
    public void checkGetMaxYear(){
        assertThat(parser.getMaxYear(stageControl.selectCurrency("lev", 1L)))
                .isEqualTo(2023);
    }

    @Test
    public void checkGetMinYear(){
        assertThat(parser.getMinYear(stageControl.selectCurrency("lev", 1L)))
                .isEqualTo(2005);
    }

    @Test
    public void checkAddDayOfWeek(){
        assertThat(formatter.addDayOfWeek("20.12.2019"))
                .isEqualTo("Пт 20.12.2019");
    }

    @Test
    public void checkCheckLengthOfCursWithTwoDecimals(){
        assertThat(formatter.addCursDecimal("12,45"))
                .isEqualTo("12,45");
    }

    @Test
    public void checkCheckLengthOfCursWithOneDecimals(){
        assertThat(formatter.addCursDecimal("12,4"))
                .isEqualTo("12,40");
    }

    @Test
    public void checkCheckLengthOfCursWithZeroDecimals(){
        assertThat(formatter.addCursDecimal("12"))
                .isEqualTo("12,00");
    }

    @Test
    public void checkConvertDate(){
        assertThat(formatter.convertDate("20.12.2019", new BigDecimal("45.89")))
                .isEqualTo("Пт 20.12.2019 - 45,89");
    }

    @Test
    public void checkFormatOutputDate(){
        List<String> dates = new ArrayList<>();
        dates.add("21.12.2019");
        dates.add("22.12.2019");
        assertThat(formatter.formatOutputDate("20.12.2019", 2))
                .isEqualTo(dates);
    }
}

