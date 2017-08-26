package alphatronics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.Timer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class charts extends javax.swing.JPanel {

    private TimeSeries series;
    
    private final String chartHeader , xAxisHeader , yAxisHeader ;
    private final float range  ;
    
    public static final int defaultWidth = 400;
    
    public static final int defaultHight = 300;
    
    public charts(String chartTitle , String chartHeader , String xAxisHeader , String yAxisHeader , float range , int width , int hight){
        this.range = range;
        this.chartHeader = chartHeader;
        this.xAxisHeader = xAxisHeader;
        this.yAxisHeader = yAxisHeader;
        this.series = new TimeSeries("", Millisecond.class);
        final TimeSeriesCollection dataset = new TimeSeriesCollection(this.series);
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(width, hight));
        this.add(chartPanel);
    }

    private JFreeChart createChart(TimeSeriesCollection dataset) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
            chartHeader,
            xAxisHeader,
            yAxisHeader,
            dataset,
            true,
            true,
            false
        );
        final XYPlot plot = result.getXYPlot();

        plot.setBackgroundPaint(new Color(0xffffe0));
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.lightGray);
        
        ValueAxis xaxis = plot.getDomainAxis();
        xaxis.setAutoRange(true);

        xaxis.setFixedAutoRange(60000.0);  // 60 seconds
        xaxis.setVerticalTickLabels(true);

        ValueAxis yaxis = plot.getRangeAxis();
        yaxis.setRange(0.0, range);

        return result;
    }
    
    public void addValue(float data){
        series.add(new Millisecond(), data);
    }
    
    public static void main (String args[]){
        
    }
    
}
