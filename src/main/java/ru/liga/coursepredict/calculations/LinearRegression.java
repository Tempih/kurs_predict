package ru.liga.coursepredict.calculations;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import static ru.liga.coursepredict.constants.Constants.ZERO;

@Slf4j
public class LinearRegression {
    private final BigDecimal intercept, slope;
    private final BigDecimal r2;
    private final BigDecimal svar0, svar1;
    private final MathContext mc = new MathContext(10);

    /**
     * Performs a linear regression on the data points {@code (y[i], x[i])}.
     *
     * @param  x the values of the predictor variable
     * @param  y the corresponding values of the response variable
     * @throws IllegalArgumentException if the lengths of the two arrays are not equal
     */
    public LinearRegression(List<BigDecimal> x, List<BigDecimal> y) {
        log.debug("Начинаем расчет коэффициентов для уравнения");

        if (x.size() != y.size()) {
            log.debug("Размер списка с курсом валют не равен размеру списка с датами");
            throw new IllegalArgumentException("array lengths are not equal");
        }
        int n = x.size();

        // first pass
        BigDecimal sumx,sumy;
        sumx = x.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        sumy  = y.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal xbar = sumx.divide(new BigDecimal(x.size()), MathContext.DECIMAL128);
        BigDecimal ybar = sumy.divide(new BigDecimal(x.size()), MathContext.DECIMAL128);

        // second pass: compute summary statistics
        BigDecimal xxbar = new BigDecimal(ZERO), yybar = new BigDecimal(ZERO), xybar = new BigDecimal(ZERO);
        for (int i = 0; i < n; i++) {
            xxbar = xxbar.add(x.get(i).subtract(xbar)).multiply(x.get(i).subtract(xbar));
            yybar = yybar.add((y.get(i).subtract(ybar)).multiply(y.get(i).subtract(ybar)));
            xybar = xybar.add((x.get(i).subtract(xbar)).multiply(y.get(i).subtract(ybar)));
        }
        slope  = xybar.divide(xxbar, MathContext.DECIMAL128);
        intercept = ybar.subtract(slope.multiply(xbar));

        // more statistical analysis
        BigDecimal rss = new BigDecimal(ZERO);      // residual sum of squares
        BigDecimal ssr = new BigDecimal(ZERO);      // regression sum of squares
        for (int i = 0; i < n; i++) {
            BigDecimal fit = slope.multiply(x.get(i)).add(intercept);
            rss = rss.add((fit.subtract(y.get(i))).multiply(fit.subtract(y.get(i))));
            ssr = ssr.add((fit.subtract(ybar)).multiply(fit.subtract(ybar)));
        }

        int degreesOfFreedom = n-2;
        r2    = ssr.divide(yybar, MathContext.DECIMAL128);
        BigDecimal svar  = rss.divide(new BigDecimal(degreesOfFreedom), MathContext.DECIMAL128);
        svar1 = svar.divide(xxbar, MathContext.DECIMAL128);
        svar0 = svar.divide(new BigDecimal(n), MathContext.DECIMAL128).add(xbar.multiply(xbar).multiply(svar1));
        log.debug("Закончили расчет коэффициентов для уравнения");

    }

    /**
     * Returns the <em>y</em>-intercept &alpha; of the best of the best-fit line <em>y</em> = &alpha; + &beta; <em>x</em>.
     *
     * @return the <em>y</em>-intercept &alpha; of the best-fit line <em>y = &alpha; + &beta; x</em>
     */
    public BigDecimal intercept() {
        return intercept;
    }

    /**
     * Returns the slope &beta; of the best of the best-fit line <em>y</em> = &alpha; + &beta; <em>x</em>.
     *
     * @return the slope &beta; of the best-fit line <em>y</em> = &alpha; + &beta; <em>x</em>
     */
    public BigDecimal slope() {
        return slope;
    }

    /**
     * Returns the coefficient of determination <em>R</em><sup>2</sup>.
     *
     * @return the coefficient of determination <em>R</em><sup>2</sup>,
     *         which is a real number between 0 and 1
     */
    public BigDecimal R2() {
        return r2;
    }

    /**
     * Returns the standard error of the estimate for the intercept.
     *
     * @return the standard error of the estimate for the intercept
     */
    public BigDecimal interceptStdErr() {
        return svar0.sqrt(mc);
    }

    /**
     * Returns the standard error of the estimate for the slope.
     *
     * @return the standard error of the estimate for the slope
     */
    public BigDecimal slopeStdErr() {
        return svar1.sqrt(mc);
    }

    /**
     * Returns the expected response {@code y} given the value of the predictor
     * variable {@code x}.
     *
     * @param  x the value of the predictor variable
     * @return the expected response {@code y} given the value of the predictor
     *         variable {@code x}
     */
    public BigDecimal predict(BigDecimal x) {
        return slope.multiply(x).add(intercept);
    }

    /**
     * Returns a string representation of the simple linear regression model.
     *
     * @return a string representation of the simple linear regression model,
     *         including the best-fit line and the coefficient of determination
     *         <em>R</em><sup>2</sup>
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(String.format("%.2f n + %.2f", slope(), intercept()));
        s.append("  (R^2 = " + String.format("%.3f", R2()) + ")");
        return s.toString();
    }

}
