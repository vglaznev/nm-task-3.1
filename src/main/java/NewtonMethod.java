import java.util.function.UnaryOperator;

import static java.lang.Math.abs;

public class NewtonMethod {
    private static final int MAX_AMOUNT_OF_ITERATIONS = 100;

    public double solve(UnaryOperator<Double> function, UnaryOperator<Double> functionDerivative, double initialApproximation,
                        double epsilon) {
        double currentApproximation;
        double nextApproximation = initialApproximation;
        double functionValue;
        int iterationNumber = 0;

        do {
            currentApproximation = nextApproximation;
            iterationNumber++;
            functionValue = function.apply(currentApproximation);
            nextApproximation = currentApproximation - function.apply(currentApproximation) / functionDerivative.apply(currentApproximation);
        } while (abs(functionValue) >= epsilon && iterationNumber < MAX_AMOUNT_OF_ITERATIONS);
        return nextApproximation;
    }
}
