package ode;

import function.TableFunction;

public class RungeKuttaFourthMethod {
    private ThreeArgumentFunction[] odes;
    protected double[] initialValues;
    private double x0;

    private double step;
    private double[] nodes;

    public RungeKuttaFourthMethod(ThreeArgumentFunction[] odes, double[] initialValues, double x0, double step, double[] nodes) {
        this.odes = odes;
        this.initialValues = initialValues;
        this.x0 = x0;
        this.step = step;
        this.nodes = nodes;
    }

    public TableFunction[] solve() {
        int n = nodes.length;

        double[] solution0 = new double[n];
        double[] solution1 = new double[n];

        solution0[n - 1] = initialValues[0];
        solution1[n - 1] = initialValues[1];

        double previousX, previousY0, previousY1;
        double k0_1, k0_2, k0_3, k0_4, k1_1, k1_2, k1_3, k1_4;

        for (int i = n - 2; i >= 0; i--) {
            previousX = nodes[i + 1];
            previousY0 = solution0[i + 1];
            previousY1 = solution1[i + 1];

            k0_1 = step * odes[0].apply(previousX, previousY0, previousY1);
            k1_1 = step * odes[1].apply(previousX, previousY0, previousY1);

            k0_2 = step * odes[0].apply(previousX + step / 2, previousY0 + k0_1 / 2, previousY1 + k1_1 / 2);
            k1_2 = step * odes[1].apply(previousX + step / 2, previousY0 + k0_1 / 2, previousY1 + k1_1 / 2);

            k0_3 = step * odes[0].apply(previousX + step / 2, previousY0 + k0_2 / 2, previousY1 + k1_2 / 2);
            k1_3 = step * odes[1].apply(previousX + step / 2, previousY0 + k0_2 / 2, previousY1 + k1_2 / 2);

            k0_4 = step * odes[0].apply(previousX + step, previousY0 + k0_3, previousY1 + k1_3);
            k1_4 = step * odes[1].apply(previousX + step, previousY0 + k0_3, previousY1 + k1_3);

            solution0[i] = solution0[i + 1] - (k0_1 + 2 * k0_2 + 2 * k0_3 + k0_4) / 6;
            solution1[i] = solution1[i + 1] - (k1_1 + 2 * k1_2 + 2 * k1_3 + k1_4) / 6;
        }

        return new TableFunction[]{
                new TableFunction(nodes, solution0),
                new TableFunction(nodes, solution1)
        };
    }
}
