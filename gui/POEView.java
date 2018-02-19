package sbeam2.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.MouseInputListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import sbeam2.gui.MainFrame;
import sbeam2.CalcData;
import sbeam2.POEData;
import sbeam2.SBApp;
import sbeam2.TOFData;

public class POEView extends JInternalFrame implements InternalFrameListener, ChartMouseListener{
	protected SBApp sb;
	protected MainFrame parent;
   	protected ArrayList<POEData> associatedPOEs;
   	
	protected XYSeriesCollection POEDataset;
	protected XYPlot POEPlot;
	protected ChartPanel chartPanel;
	protected JFreeChart xyScatter;
	protected XYItemRenderer renderer;
	
	protected XYItemEntity dragPoint;

	public POEView(SBApp app, MainFrame p) {
		// TODO Auto-generated constructor stub
		sb = app;
		parent = p;
 		associatedPOEs = new ArrayList<POEData>();
 		
 		this.addInternalFrameListener(this);
 		
 		POEDataset = new XYSeriesCollection( );
		xyScatter = ChartFactory.createScatterPlot(null, "Energy", "Units", POEDataset, PlotOrientation.VERTICAL, false, false, false);
		chartPanel = new ChartPanel(xyScatter);
		POEPlot = xyScatter.getXYPlot( );
		POEPlot.setRenderer(renderer); 
		setContentPane(chartPanel); 
	    POEPlot.setRenderer(new XYLineAndShapeRenderer(false, true) {

	        @Override
	        public Shape getItemShape(int row, int col) {
	            if (dragPoint != null && row == dragPoint.getSeriesIndex() & col == dragPoint.getItem()) {
	                return ShapeUtilities.createDiagonalCross(5, 2);
	            } else {
	                return new Ellipse2D.Double(-5,-5,10,10);
	            }
	        }
	        
	        @Override
	        public Paint getItemPaint(int series, int item){
	        	if (dragPoint != null && series == dragPoint.getSeriesIndex() & item == dragPoint.getItem()) {
	                return Color.GREEN;
	            } else {
	                return super.getItemPaint(series, item);
	            }
	        	
	        }
	    });
		
 		chartPanel.addChartMouseListener(this);
 		chartPanel.setPopupMenu(null);
 		chartPanel.setDomainZoomable(false);
 		chartPanel.setRangeZoomable(false);

	}
	
 	public void Execute(){
 		parent.addFrame(this);
		this.setPreferredSize(new Dimension(400, 200));
		this.pack();
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setFocusable(true);
		this.setEnabled(true);
		this.setVisible(true);
		this.setResizable(true);
 	}
 	
 	
 	
	public void addPOEToView(POEData poe){
		associatedPOEs.add(poe);
		XYSeries poeSeries = new XYSeries(poe.title);
		for(int i=0; i < poe.poe_amplitudes.length; i++){
			poeSeries.add(poe.energy_values[i], poe.poe_amplitudes[i]);
		}
		POEDataset.addSeries(poeSeries);
		
		poe.AssociatedPOEViews.add(this);
	}
 	
	
	
	
	
	
	/* Listener Methods */

	@Override
	public void internalFrameOpened(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameIconified(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameActivated(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void chartMouseClicked(ChartMouseEvent e) {
		// TODO Auto-generated method stub
		if(dragPoint != null){
			// change the TOFs
			XYSeries series = POEDataset.getSeries(dragPoint.getSeriesIndex());
			float x = series.getDataItem(dragPoint.getItem()).getX().floatValue();
			float newY = series.getDataItem(dragPoint.getItem()).getY().floatValue();
			POEData poe = associatedPOEs.get(0); //probably fix this
			int index = (int) ((x - poe.energy_values[0]) / poe.energy_spacing); //find which point has changed 		
			boolean endpoint = index == 0 || index == poe.num_points-1;
	 		poe.updatePOE(x, newY);
	 		poe.FindNewTOFs(newY, endpoint);
			
	 		//stop changing this point
			dragPoint = null;
			chartPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	 		
		}else if (e.getEntity() instanceof XYItemEntity) {
            dragPoint = (XYItemEntity) e.getEntity();
            chartPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
			XYSeries series = POEDataset.getSeries(dragPoint.getSeriesIndex());
			float x = series.getDataItem(dragPoint.getItem()).getX().floatValue();
            POEData poe = associatedPOEs.get(0); //probably fix this
	 		poe.calcTOFDelta(x);
        }
	}

	@Override
	public void chartMouseMoved(ChartMouseEvent e) {
		// TODO Auto-generated method stub
		if(dragPoint != null){
			double yVal = POEPlot.getRangeAxis().java2DToValue(e.getTrigger().getY(), chartPanel.getChartRenderingInfo().getPlotInfo().getDataArea(), POEPlot.getRangeAxisEdge());
			XYSeries series = POEDataset.getSeries(dragPoint.getSeriesIndex());
			XYDataItem item = series.getDataItem(dragPoint.getItem());
			//series.delete(dragPoint.getSeriesIndex(),dragPoint.getItem());
			item.setY(yVal);
			//series.add(item);
		}
		
	}

}
