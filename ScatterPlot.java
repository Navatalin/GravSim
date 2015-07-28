import java.io.*;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.*;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.FastScatterPlot;
import org.jfree.chart.ChartFactory;
import org.jfree.data.xy.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.util.ShapeUtilities;

public class ScatterPlot
{
    private JFreeChart chart;
    private XYDataset data;
    private double maxX, minX, maxY, minY = 0;
    
    public ScatterPlot(Body[] bodies)
    {
        data = populateData(bodies);
        NumberAxis domainAxis = new NumberAxis("X");
        NumberAxis rangeAxis = new NumberAxis("Y");
        
        chart = ChartFactory.createScatterPlot("Solar System Simulation","X","Y",data);
        
    }
    public void writeImage(String outputDir,double maxX, double maxY) throws Exception
    {
        
        Shape cross = ShapeUtilities.createDiagonalCross(1,1);
        XYPlot plot = (XYPlot) chart.getPlot();
        XYItemRenderer renderer = plot.getRenderer();
        
        //Sun
        renderer.setSeriesShape(0,new Ellipse2D.Double(-5.0,-5.0,10.0,10.0));
        renderer.setSeriesPaint(0, Color.yellow);
        
        //Small Asteroid
        renderer.setSeriesShape(1,new Ellipse2D.Double(-0.5,-0.5,1.0,1.0));
        renderer.setSeriesPaint(1, Color.black);
        
        //Large Asteroid
        renderer.setSeriesShape(2,new Ellipse2D.Double(-1,-1,2.0,2.0));
        renderer.setSeriesPaint(2, Color.red);
        
        //Small Planet
        renderer.setSeriesShape(3,new Ellipse2D.Double(-2,-2,4.0,4.0));
        renderer.setSeriesPaint(3, Color.green);
        
        //Med Planet
        renderer.setSeriesShape(4,new Ellipse2D.Double(-3,-3,6.0,6.0));
        renderer.setSeriesPaint(4, Color.blue);
        
        //Large Planet
        renderer.setSeriesShape(5,new Ellipse2D.Double(-4,-4,8.0,8.0));
        renderer.setSeriesPaint(5, Color.orange);
        
        
        

        
        NumberAxis domain = (NumberAxis) plot.getDomainAxis();
        NumberAxis range = (NumberAxis) plot.getRangeAxis();
        
        domain.setRange(-1*maxX,maxX);
        range.setRange(-1*maxY,maxY);
        
        File outputfile = new File(outputDir);
        
        ChartUtilities.saveChartAsJPEG(outputfile,0.8f,chart,1024,720);
        
    }
    private XYDataset populateData(Body[] bodies)
    {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries small = new XYSeries("Asteroid Small");
        XYSeries large = new XYSeries("Asteroid Large");
        XYSeries psmall = new XYSeries("Planet Small");
        XYSeries pmed = new XYSeries("Planet Medium");
        XYSeries plarge = new XYSeries("Planet Large");
        XYSeries sun = new XYSeries("Sun");
        
        sun.add(bodies[0].rx,bodies[0].ry);

        
        for(int i = 1; i < bodies.length; i++)
        {
            if(bodies[i].mass > 8.62e25)
                plarge.add(bodies[i].rx,bodies[i].ry);
            else
            if(bodies[i].mass > 5.97e24)
                pmed.add(bodies[i].rx,bodies[i].ry);
            else
            if(bodies[i].mass > 4.867e24)
                psmall.add(bodies[i].rx,bodies[i].ry);
            else
            if(bodies[i].mass > 5e15)
                large.add(bodies[i].rx,bodies[i].ry);
            else
            if(bodies[i].mass > 0)
                small.add(bodies[i].rx,bodies[i].ry);
            
        }

        
        dataset.addSeries(sun);
        dataset.addSeries(small);
        dataset.addSeries(large);
        dataset.addSeries(psmall);
        dataset.addSeries(pmed);
        dataset.addSeries(plarge);
        
        return dataset;
    }
    
    
    
}