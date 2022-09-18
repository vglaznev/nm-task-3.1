import function.Function;
import function.FunctionUtil;
import function.SimpleFunction;
import function.TableFunction;
import ode.FiveArgumentFunction;
import ode.ThreeArgumentFunction;
import splitter.UniformSplitter;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.abs;
import static java.lang.Math.exp;


public class Main {
    private static final int PLOTTER_RESOLUTION = 1000;

    public static void main(String[] args) {
        double beginOfInterval = 0, endOfInterval = 1;
        int numberOfDots = 5;
        double[] nodes = (new UniformSplitter()).split(beginOfInterval, endOfInterval, numberOfDots);

        Function analyticSolution = new SimpleFunction(x -> x + 1 / exp(x) - 1 / exp(1));
        Function analyticSolutionDerivative = new SimpleFunction(x -> 1 - 1 / exp(x));

        ShootingMethod method = new ShootingMethod.Builder()
                .system(
                        new ThreeArgumentFunction[]{
                                (x, u, w) -> w, //u'
                                (x, u, w) -> 1 - w //w'
                        })
                .derivativeSystem(
                        new FiveArgumentFunction[]{
                                (x, u, w, du_da, dw_da) -> dw_da, //du_da
                                (x, u, w, du_da, dw_da) -> /*0 * du_da + 1 **/ - dw_da, //dw_da
                        }
                )
                .initialValues(0, 1)
                .nodes(nodes)
                .step((endOfInterval - beginOfInterval) / (numberOfDots - 1))
                .build();
        var shootingParameter = method.solve(1, 0.01);
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
