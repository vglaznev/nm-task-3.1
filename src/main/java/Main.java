import function.Function;
import function.FunctionUtil;
import function.SimpleFunction;
import function.TableFunction;
import ode.FiveArgumentFunction;
import ode.ThreeArgumentFunction;
import splitter.UniformSplitter;

import java.awt.*;

import static java.lang.Math.exp;


public class Main {
    public static void main(String[] args) {
        double beginOfInterval = 0, endOfInterval = 1;
        int numberOfDots = 20;

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
                .nodes((new UniformSplitter()).split(beginOfInterval, endOfInterval, numberOfDots))
                .step((endOfInterval - beginOfInterval) / (numberOfDots - 1))
                .build();
        var sol = method.solve(1, 0.00001);
        System.out.println(sol);


        Plotter plotter = new Plotter();
        plotter.addGraphic(FunctionUtil.getTableFunction(new SimpleFunction(x -> x + 1 / exp(x) - 1 / exp(1)), beginOfInterval, endOfInterval, 1000), "u analytic", Color.black);
        plotter.addGraphic(FunctionUtil.getTableFunction(new SimpleFunction(x -> 1 - 1 / exp(x)), beginOfInterval, endOfInterval, 1000), "u der analytic", Color.green);
        plotter.addGraphic(method.getSolution(sol)[0], "u numeric", Color.red);
        plotter.addGraphic(method.getSolution(sol)[1], "u der numeric", Color.orange);
        plotter.display();
    }
}
