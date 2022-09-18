package ode;

import function.Function;

public interface FiveArgumentFunction {
    double apply(double x, Function f1, Function f2, double y1, double y2);
}
