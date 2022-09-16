import ode.ODE;

public class Main {
    public static void main(String[] args) {
        ODE[] ODEs = new ODE[]{
                (x, u, w) -> 0, //w'
                (x, u, w) -> 0 //u'
        };

        double beginOfInterval;
        double endOfInterval;
        double initialValueOnEnd;

        ODE[] newtonODEs = new ODE[]{
                (x, du_da, dw_da) -> 0, //
                (x, du_da, dw_da) -> 0 //
        };
    }
}
