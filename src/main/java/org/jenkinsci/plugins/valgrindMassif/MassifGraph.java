package org.jenkinsci.plugins.valgrindMassif;

import hudson.model.AbstractBuild;
import hudson.util.Graph;
import hudson.util.ShiftedCategoryAxis;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

import java.awt.*;

/**
 * Created by frakafra on 2014. 2. 23..
 */
public class MassifGraph extends Graph {
    private final CategoryDataset categoryDataset;

    public static final int DEFAULT_CHART_WIDTH = 600;
    public static final int DEFAULT_CHART_HEIGHT = 240;
    private final String yLabel;

    public MassifGraph(AbstractBuild<?, ?> owner, CategoryDataset categoryDataset, String yLabel,
                         int chartWidth, int chartHeight) {
        super(owner.getTimestamp(), chartWidth, chartHeight);
        this.yLabel = yLabel;
        this.categoryDataset = categoryDataset;
    }

    @Override
    protected JFreeChart createGraph() {
        final JFreeChart chart = ChartFactory.createStackedAreaChart(
                null, // chart
                null, // unused
                yLabel, // range axis label
                categoryDataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips
                false // urls
        );

        final LegendTitle legend = chart.getLegend();
        legend.setPosition(RectangleEdge.RIGHT);

        chart.setBackgroundPaint(Color.white);

        final CategoryPlot plot = chart.getCategoryPlot();

        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.black);

        CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
        plot.setDomainAxis(domainAxis);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        domainAxis.setCategoryMargin(0.0);

        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setLowerBound(0);
        rangeAxis.setAutoRange(true);

        final StackedAreaRenderer renderer = (StackedAreaRenderer) plot.getRenderer();
        renderer.setBaseStroke(new BasicStroke(2.0f));

        // crop extra space around the graph
        plot.setInsets(new RectangleInsets(5.0, 0, 0, 5.0));

        return chart;
    }
}
