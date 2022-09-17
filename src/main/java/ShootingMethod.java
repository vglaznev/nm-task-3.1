import function.TableFunction;
import ode.ODE;
import ode.RungeWithVariableParameter;

public class ShootingMethod {

    private ODE[] ODEs;
    private double A = 0, B = 0;
    private double endOfInterval = 0;
    private double[] nodes = null; //remove
    private double step = 0; //remove

    private TableFunction currentU;
    private TableFunction currentW;

    public ShootingMethod() {
        ODEs = new ODE[]{
                (x, u, w) -> 0, //u'
                (x, u, w) -> 0 //w'
        };

    }

    private double calculatePhi(double shootingParameter) {
        RungeWithVariableParameter method1 = new RungeWithVariableParameter(ODEs,
                B, endOfInterval, step, nodes);
        method1.solve(shootingParameter);


    }
}
