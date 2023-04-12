package ru.liga.coursepredict.system;

import lombok.extern.slf4j.Slf4j;
import ru.liga.coursepredict.constants.Constants;
import ru.liga.coursepredict.exceptions.IncorrectInputException;
import ru.liga.coursepredict.model.CourseTable;
import ru.liga.coursepredict.model.PredictResult;
import ru.liga.coursepredict.telegram.Sender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.liga.coursepredict.constants.Constants.*;
import static ru.liga.coursepredict.outputcreater.InfoOutput.*;
import static ru.liga.coursepredict.validation.ValidateInput.checkCorrectInput;

@Slf4j
public class StageControl {
    private static final String STANDARD_FIRST_WORD = "rate";
    private static final AlgorithmSelectStage selectPredictAlgorithm = new AlgorithmSelectStage();
    private static final Sender sender = new Sender();
    private static final CurrencySelectStage selectCurrency = new CurrencySelectStage();
    private static final OutputStage outputStage = new OutputStage();
    private static final Integer INDEX_FIRST_INPUT_WORD = 0;
    private static final Integer INDEX_SECOND_INPUT_WORD = 1;
    private static final Integer INDEX_THIRD_INPUT_WORD = 2;
    private static final Integer INDEX_FORTH_INPUT_WORD = 3;
    private static final Integer INDEX_FIFTH_INPUT_WORD = 5;
    private static final Integer INDEX_SIXTH_INPUT_WORD = 6;
    private static final Integer INDEX_SEVENTH_INPUT_WORD = 7;
    private static final String DEFAULT_OUTPUT_PARAM = "list";

    /**
     * startProgram - получает данные из терминала и запускает расчет курса валюты
     */
    public String startProgram(String inputMessage, Long chatId) {
        String output = EMPTY_STRING;
        try {
            try {
                if (!checkCorrectInput(inputMessage)) {
                    throw new IncorrectInputException(giveFormatError());
                }
                log.debug("Получено сообщение:{}", inputMessage);
                String paramOutput = DEFAULT_OUTPUT_PARAM;
                String[] inputMessageSeparated = inputMessage.split(Constants.SPACE);
                String firstWord = inputMessageSeparated[INDEX_FIRST_INPUT_WORD];
                if (!STANDARD_FIRST_WORD.equals(firstWord)) {
                    log.info("Первое слово не {},a {}", STANDARD_FIRST_WORD, firstWord);
                    throw new IncorrectInputException(giveFirstWordError());
                }
                String[] currencies = inputMessageSeparated[INDEX_SECOND_INPUT_WORD].split(COMMA);
                log.info("Получены следующие валюты {}", inputMessageSeparated[INDEX_SECOND_INPUT_WORD]);
                String inputPredictPeriod = inputMessageSeparated[INDEX_THIRD_INPUT_WORD].replace(DASH, EMPTY_STRING);
                String inputParamPeriod = inputMessageSeparated[INDEX_FORTH_INPUT_WORD];
                log.info("Выбран следующий период/дата предсказания: {}", inputParamPeriod);
                String predictAlgorithm = inputMessageSeparated[INDEX_FIFTH_INPUT_WORD];
                log.info("Выбран следующий алгоритм предсказания:{}", predictAlgorithm);
                if (inputMessageSeparated.length > INDEX_SIXTH_INPUT_WORD) {
                    paramOutput = inputMessageSeparated[INDEX_SEVENTH_INPUT_WORD];
                }
                log.info("Выбран следующий вариант вывода курса валют:{}", paramOutput);
                Map<String, List<CourseTable>> currencyTables = new HashMap<>();
                log.debug("Началось формирование словаря с валютами и курсам");
                for (String s : currencies) {
                    currencyTables.put(s, selectCurrency.getCurrencyData(s, chatId));
                }
                log.debug("Сформирован словарь с валютами и курсам валют. Его размер: {}", currencyTables.size());
                if (currencyTables.isEmpty()) {
                    log.debug("Словарь оказался пустой");
                    sender.sendText(chatId, giveDataError());
                }
                log.debug("Начался процесс предсказания курса валют");
                List<PredictResult> predictResult = selectPredictAlgorithm.startPredict(currencyTables, inputPredictPeriod, inputParamPeriod, predictAlgorithm, chatId);
                if (predictResult.isEmpty()) {
                    log.debug("Курса валют не получилось предсказать");
                    sender.sendText(chatId, giveDataError());
                }
                log.debug("Процесс предсказания курса валют закончился");
                log.debug("Начался процесс подготовки результата для вывода");
                output = outputStage.startOutputResult(predictResult, paramOutput);
                log.debug("Процесс подготовки результата для вывода закончился");
            } catch (IncorrectInputException ex) {
                log.debug("Ошибка при формировании данных");
                sender.sendText(chatId, giveFormatError());

            }
        } catch (RuntimeException ex) {
            log.debug("Ошибка в работе программы");
            sender.sendText(chatId, "Ошибка в работе программы");
            ex.printStackTrace();
        }
        return output;
    }
}
