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

import javax.swing.JColorChooser;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.MouseInputListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
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
   	public ArrayList<POEData> associatedPOEs;
   	
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
 		this.addFocusListener(parent);
 		
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
		this.setClosable(true);
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
		this.title += (this.title.isEmpty() ? "" : " && ") + poe.title;
	}
	
	public void removePOEFromView(int index){
		POEDataset.removeSeries(index);
		associatedPOEs.get(index).AssociatedPOEViews.remove(this);
		associatedPOEs.remove(index);
	}
 	
	protected String[] getAllPOEList(){
		String[] list = new String[sb.poes.size()];
		for(int i=0; i < sb.poes.size(); i++){
			list[i] = sb.poes.get(i).title;
		}
		return list;
	}
	
	protected String[] getDispPOEList(){
		String[] list = new String[associatedPOEs.size()];
		for(int i=0; i < associatedPOEs.size(); i++){
			list[i] = associatedPOEs.get(i).title;
		}
		return list;
	}
	
	
	
	
	/* MENU ITEMS */
	protected void AppendStoredPE()
	{
		String[] poeList = getAllPOEList();
		List_Dialog poe_list_dialog = new List_Dialog(parent, poeList, 1);
		poe_list_dialog.SetCaption("Choose a P(E) to append:");
		poe_list_dialog.Execute();
		if (!poe_list_dialog.ID) return;
		
		POEData poe = sb.poes.get(poe_list_dialog.GetChosenIndex()[0]);
		System.out.println("Adding " + poe.title);
		addPOEToView(poe);
		

	}
	
	protected void RemovePEFromDisplay() {
		String[] poeList = getDispPOEList();
		List_Dialog poe_list_dialog = new List_Dialog(parent, poeList, 1);
		poe_list_dialog.SetCaption("Choose a P(E) to remove:");

		poe_list_dialog = new List_Dialog(parent, poeList, 1);
		poe_list_dialog.Execute();
		if(!poe_list_dialog.ID) return; //check if ok clicked
		int chosen_index = poe_list_dialog.GetChosenIndex()[0];
		
		removePOEFromView(chosen_index);
	}
	
	protected void AxisRange(){
		Param_Dialog param_dialog = new Param_Dialog(parent, 2);
		
		param_dialog.SetDefault1("0.0");
		param_dialog.SetDefault2("1.0");
		param_dialog.SetValue1("" + (-Float.MIN_VALUE));
		param_dialog.SetValue2("" + Float.MAX_VALUE);
		
		param_dialog.Execute();
		if(!param_dialog.ID){
			return;
		}
		float starting_energy = Float.parseFloat(param_dialog.edit1.getText());
		float ending_energy = Float.parseFloat(param_dialog.edit2.getText());
		
		NumberAxis domain = (NumberAxis) POEPlot.getDomainAxis();
        domain.setRange(starting_energy, ending_energy);
		
	}
	
	protected void SetColors(){
		String[] tofList = getDispPOEList();
		List_Dialog tof_list_dialog = new List_Dialog(parent, tofList, 1);
		tof_list_dialog.SetCaption("Choose a TOF:");
		tof_list_dialog.Execute();
		if(!tof_list_dialog.ID) return; //check if ok clicked
		int chosen_index = tof_list_dialog.GetChosenIndex()[0];
		
		Color c = JColorChooser.showDialog(this, "Choose Color", Color.black);
		if(c != null){
			associatedPOEs.get(chosen_index).poe_color = c;
			POEPlot.getRendererForDataset(POEDataset).setSeriesPaint(chosen_index, c);
		}
	}
	
	
	
	
	
	
	
	/* Listener Methods */

	@Override
	public void internalFrameOpened(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		for (int i = 0; i < associatedPOEs.size(); i++) {
			associatedPOEs.get(i).AssociatedPOEViews.remove(this);
			// Each TOF can only be in view once!
			associatedPOEs.get(i).is_Visible = -2;// CHANGE:set not visible
		}
		parent.internalClosed(this);		
		
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		for (int i = 0; i < associatedPOEs.size(); i++) {
			associatedPOEs.get(i).AssociatedPOEViews.remove(this);
			// Each TOF can only be in view once!
			associatedPOEs.get(i).is_Visible = -2;// CHANGE:set not visible
		}
		parent.internalClosed(this);
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
			
			//extrema handling
			if(newY < 0f){
				newY = 0;
			}
			
			
			boolean endpoint = index == 0 || index == poe.num_points-1;
	 		poe.updatePOE(x, newY);
	 		poe.FindNewTOFs(newY, endpoint);
	 		
	 		//scale the axis
	 		float maximum = poe.maxValue();
			if(maximum == 0.0f) maximum = 1.0f;
			POEPlot.getRangeAxis().setRange(0, maximum * 1.1);
			
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
			item.setY(yVal);
			chartPanel.repaint();
		}
		
	}

}
