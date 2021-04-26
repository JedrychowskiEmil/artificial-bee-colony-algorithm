import java.util.List;

    public class SimpleFunction implements TargetFunction {
        @Override
        public double f(List<Double> args) {
            double x1 = args.get(0);
            double x2 = args.get(1);
            return Math.pow(x1, 2) - x2;
        }
    }


