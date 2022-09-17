import function.TableFunction;
import ode.ODE;
import ode.RungeKuttaFourthMethod;
import ode.RungeWithVariableParameter;

import java.util.function.UnaryOperator;

public class Main {
    public static void main(String[] args) {
        double beginOfInterval;
        double endOfInterval = 0; //remove
        double initialValueOnEnd;

        double[] nodes = null; //remove
        double step = 0; //remove

        ODE[] ODEs = new ODE[]{
                (x, u, w) -> 0, //u'
                (x, u, w) -> 0 //w'
        };
        double A = 0, B = 0;

        RungeWithVariableParameter method1 = new RungeWithVariableParameter(ODEs,
                B, endOfInterval, step, nodes);

        UnaryOperator<Double> phi =
                x -> {
                    TableFunction[] solutions = method1.solve();
                    return solutions[1].x()[0] - A;
                };

        ODE[] derivativeODEs = new ODE[]{
                (x, du_da, dw_da) -> dw_da, // du/da
                (x, du_da, dw_da) -> df_du * du_da + df_dw * dw_da // dw/da
        };

        double[] derivativeInitialValues = new double[]{0, 1}; // du/da = 0 при x = b, dw/da = 1 при x = b
        RungeKuttaFourthMethod method = new RungeKuttaFourthMethod(derivativeODEs,
                derivativeInitialValues, endOfInterval, step, nodes);
        TableFunction[] solutions = method.solve();
        double phiDerivative = solutions[1].x()[0];


        UnaryOperator<Integer> func = x -> 5;
        UnaryOperator<Double> func2 = x -> nodes[func.apply(1)];

    }
}
