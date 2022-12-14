package ode;

import function.TableFunction;

public class RungeWithVariableParameter extends RungeKuttaFourthMethod {

    public RungeWithVariableParameter(ThreeArgumentFunction[] odes, double initialValue0, double x0, double step, double[] nodes) {
        super(odes, new double[]{initialValue0, 0}, x0, step, nodes);
    }

    public TableFunction[] solve(double initialValue1) {
        initialValues[1] = initialValue1;
        return solve();
    }
}
