package ru.liga;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.liga.curspredict.Main;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class CursPredictTest {
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    public void checkUsdWeek() {
        String input = "rate USD week";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        Main.main(null);
        assertThat(outputStreamCaptor.toString())
                .contains("Сб 18.03.2023 - 75,72")
                .contains("Вс 19.03.2023 - 75,76")
                .contains("Пн 20.03.2023 - 75,74")
                .contains("Вт 21.03.2023 - 75,72")
                .contains("Ср 22.03.2023 - 75,75")
                .contains("Чт 23.03.2023 - 75,84")
                .contains("Пт 24.03.2023 - 75,85");
    }

    @Test
    public void checkUsdTomorrow() {
        String input = "rate USD tomorrow";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        Main.main(null);
        assertThat(outputStreamCaptor.toString()).contains("Сб 18.03.2023 - 75,72");
    }

    @Test
    public void checkEurWeek() {
        String input = "rate EUR week";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        Main.main(null);
        assertThat(outputStreamCaptor.toString())
                .contains("Сб 18.03.2023 - 80,60")
                .contains("Вс 19.03.2023 - 80,61")
                .contains("Пн 20.03.2023 - 80,68")
                .contains("Вт 21.03.2023 - 80,72")
                .contains("Ср 22.03.2023 - 80,73")
                .contains("Чт 23.03.2023 - 80,76")
                .contains("Пт 24.03.2023 - 80,75");
    }

    @Test
    public void checkEurTomorrow() {
        String input = "rate EUR tomorrow";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        Main.main(null);
        assertThat(outputStreamCaptor.toString()).contains("Сб 18.03.2023 - 80,60");
    }


    @Test
    public void checkTryWeek() {
        String input = "rate TRY week";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        Main.main(null);
        assertThat(outputStreamCaptor.toString())
                .contains("Сб 18.03.2023 - 3,99")
                .contains("Вс 19.03.2023 - 3,99")
                .contains("Пн 20.03.2023 - 3,99")
                .contains("Вт 21.03.2023 - 3,99")
                .contains("Ср 22.03.2023 - 3,99")
                .contains("Чт 23.03.2023 - 3,99")
                .contains("Пт 24.03.2023 - 4,00");
    }

    @Test
    public void checkTryTomorrow() {
        String input = "rate TRY tomorrow";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        Main.main(null);
        assertThat(outputStreamCaptor.toString()).contains("Сб 18.03.2023 - 3,99");
    }

}
