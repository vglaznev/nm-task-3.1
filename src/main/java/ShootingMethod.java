import function.Function;
import function.TableFunction;
import interpolation.AitkenScheme;
import interpolation.InterpolationAlgorithm;
import interpolation.InterpolationPolynomial;
import ode.FiveArgumentFunction;
import ode.ThreeArgumentFunction;
import ode.RungeKuttaFourthMethod;
import ode.RungeWithVariableParameter;
import splitter.UniformSplitter;

public class ShootingMethod extends NewtonMethod {

    private ThreeArgumentFunction[] system;
    private ThreeArgumentFunction[] derivativeSystem;
    private double A, B;
    private double[] nodes;
    private double step;

    private Function currentU;
    private Function currentW;
    private static InterpolationAlgorithm interpolationAlgorithm = new AitkenScheme();

    private ShootingMethod(ThreeArgumentFunction[] system, FiveArgumentFunction[] derivativeSystem, double a, double b, double[] nodes, double step) {
        this.system = system;
        this.derivativeSystem = new ThreeArgumentFunction[]{
                (x, du_da, dw_da) -> derivativeSystem[0].apply(x, currentU, currentW, du_da, dw_da),
                (x, du_da, dw_da) -> derivativeSystem[1].apply(x, currentU, currentW, du_da, dw_da)
        };
        A = a;
        B = b;
        this.nodes = nodes;
        this.step = step;
    }

    private double calculatePhiDerivative() {
        RungeKuttaFourthMethod method = new RungeKuttaFourthMethod(derivativeSystem,
                new double[]{0, 1}, nodes[nodes.length - 1], step, nodes);
        var solution = method.solve();
        return solution[1].y()[0];
    }

    private double calculatePhi(double shootingParameter) {
        var nodes2 = new UniformSplitter().split(nodes[0], nodes[nodes.length - 1], 2 * nodes.length + 1);
        RungeWithVariableParameter method = new RungeWithVariableParameter(system,
                B, nodes2[nodes2.length - 1], nodes2[1]- nodes2[0], nodes2);
        var solution = method.solve(shootingParameter);
        currentU = new InterpolationPolynomial(solution[0].x(), solution[0].y(), interpolationAlgorithm);
        currentW = new InterpolationPolynomial(solution[1].x(), solution[1].y(), interpolationAlgorithm);
        return solution[1].y()[0] - A;
    }

    public double solve(double initialApproximation, double epsilon) {
        return solve(this::calculatePhi, a -> calculatePhiDerivative(), initialApproximation, epsilon);
    }

    public TableFunction[] getSolution(double shootingParameter) {
        var nodes2 = new UniformSplitter().split(nodes[0], nodes[nodes.length - 1], 2 * nodes.length + 1);
        RungeWithVariableParameter method = new RungeWithVariableParameter(system,
                B, nodes2[nodes2.length - 1], nodes2[1]- nodes2[0], nodes2);
        var solution = method.solve(shootingParameter);
        return solution;
    }

    public static class Builder {
        private ThreeArgumentFunction[] system;
        private FiveArgumentFunction[] derivativeSystem;
        private double A, B;
        private double[] nodes;
        private double step;

        public Builder system(ThreeArgumentFunction[] system) {
            this.system = system;
            return this;
        }

        public Builder derivativeSystem(FiveArgumentFunction[] derivativeSystem) {
            this.derivativeSystem = derivativeSystem;
            return this;
        }

        public Builder initialValues(double A, double B) {
            this.A = A;
            this.B = B;
            return this;
        }

        public Builder nodes(double[] nodes) {
            this.nodes = nodes;
            return this;
        }

        public Builder step(double step) {
            this.step = step;
            return this;
        }


        public ShootingMethod build() {
            return new ShootingMethod(system, derivativeSystem, A, B, nodes, step);
        }
    }
}
