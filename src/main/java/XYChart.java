import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import java.util.List;

public class XYChart extends ApplicationFrame {

    /**
     * A demonstration application showing an XY series containing a null value.
     *
     * @param title the frame title.
     */
    public XYChart(final String title, List<Double> results) {

        super(title);
        final XYSeries series = new XYSeries("Value of function");
        for (int i = 1; i <= results.size(); i++) {
            series.add(i, results.get(i - 1));
        }
        final XYSeriesCollection data = new XYSeriesCollection(series);
        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Artificial bee colony algorithm progress",
                "Loop number",
                "Value of function",
                data,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }
}
