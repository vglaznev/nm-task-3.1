import function.Function;
import function.FunctionUtil;
import function.SimpleFunction;
import function.TableFunction;
import interpolation.AitkenScheme;
import interpolation.InterpolationPolynomial;
import interpolation.builder.InterpolationPolynomialBuilder;
import ode.FiveArgumentFunction;
import ode.ThreeArgumentFunction;
import splitter.UniformSplitter;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.*;


public class Main {
    private static final int PLOTTER_RESOLUTION = 1000;

    public static void main(String[] args) {
        double beginOfInterval = 0, endOfInterval = 1;
        int numberOfDots = 25;
        double[] nodes = (new UniformSplitter()).split(beginOfInterval, endOfInterval, numberOfDots);

        Function analyticSolution = new SimpleFunction(x -> 1 + exp(2 * x));
        Function analyticSolutionDerivative = new SimpleFunction(x -> 2 * exp(2 * x));

        ShootingMethod method = new ShootingMethod.Builder()
                .system(
                        new ThreeArgumentFunction[]{
                                (x, u, w) -> w, //u'
                                (x, u, w) -> (pow(w, 2) * (w - u + 1)) / pow(u - 1, 2) //w'
                        })
                .derivativeSystem(
                        new FiveArgumentFunction[]{
                                (x, u, w, du_da, dw_da) -> dw_da, //du_da
                                (x, u, w, du_da, dw_da) -> -(pow(w.getY(x), 2) / pow(u.getY(x) - 1, 2) + (2 * pow(w.getY(x), 2) * (w.getY(x) - u.getY(x) + 1)) / pow(u.getY(x) - 1, 3)) * du_da
                                        + (pow(w.getY(x), 2) / pow(u.getY(x) - 1, 2) + (2 * w.getY(x) * (w.getY(x) - u.getY(x) + 1)) / pow(u.getY(x) - 1, 2)) * dw_da, //dw_da
                        }
                )
                .initialValues(2, 1 + exp(2))
                .nodes(nodes)
                .step((endOfInterval - beginOfInterval) / (numberOfDots - 1))
                .build();
        var shootingParameter = method.solve(12, 0.0000000001);
        System.out.println(shootingParameter);

        var solution = method.getSolution(shootingParameter);


        Plotter plotter = new Plotter();
        plotter.addGraphic(FunctionUtil.getTableFunction(analyticSolution, beginOfInterval, endOfInterval, PLOTTER_RESOLUTION), "u аналитическое", Color.black);
        plotter.addGraphic(FunctionUtil.getTableFunction(analyticSolutionDerivative, beginOfInterval, endOfInterval, PLOTTER_RESOLUTION), "u'(w) аналитическое", Color.green);
        plotter.addGraphic(solution[0], "u численное", Color.red);
        plotter.addGraphic(solution[1], "u'(w) численное", Color.orange);
        plotter.display();

        double[] x = nodes;
        double[] u = Arrays.stream(nodes).map(analyticSolution::getY).toArray();
        double[] w = Arrays.stream(nodes).map(analyticSolutionDerivative::getY).toArray();
        double[] deltaU = IntStream.range(0, nodes.length).mapToDouble(i -> abs(u[i] - solution[0].y()[i])).toArray();
        double[] deltaW = IntStream.range(0, nodes.length).mapToDouble(i -> abs(w[i] - solution[1].y()[i])).toArray();
        var table = IntStream.range(0, nodes.length).mapToObj(i -> new double[]{nodes[i], u[i], deltaU[i], w[i], deltaW[i]}).toArray(double[][]::new);
        int numberOfRows = 3;
        System.out.println("x u delta_U u' delta_U'");
        Arrays.stream(table).limit(numberOfRows).forEach(row -> System.out.println(Arrays.toString(row)));
        Arrays.stream(table).skip(nodes.length - numberOfRows).forEach(row -> System.out.println(Arrays.toString(row)));

    }
}
