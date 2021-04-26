import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Double> results = new ArrayList<>();
        TargetFunction function = new NotSimpleFunction();

        List<Double> upperBound = new ArrayList<>(2);
        upperBound.add(5D);
        upperBound.add(5D);
        List<Double> lowerBound = new ArrayList<>(2);
        lowerBound.add(-5D);
        lowerBound.add(-5D);

        var beeColony = new ArtificialBeeColony(100, 2, 50, 25, function, lowerBound, upperBound, false);
        beeColony.initialize();

        for (int i = 0; i < 10; i++) {
            beeColony.generateSolution();
            results.add(beeColony.getResultValue());
            System.out.println("Best value is = " + beeColony.getResultValue() + " for variables " + beeColony.getResultVariables().get(0) + " and " + beeColony.getResultVariables().get(1));
        }

        final XYChart demo = new XYChart("essa m8", results);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }
}
