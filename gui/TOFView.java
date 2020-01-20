package sbeam2.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.MouseInputListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import sbeam2.gui.Param_Dialog;
import sbeam2.gui.MainFrame;
import sbeam2.SBApp;
import sbeam2.TOFData;

public class TOFView extends JInternalFrame implements MouseInputListener, InternalFrameListener{
	
	public ArrayList<TOFData> associatedTOFs;
	protected MainFrame mainWindow;
	protected SBApp sb;
	
	protected XYSeriesCollection TOFDataset;
	protected XYPlot TOFPlot;
	protected ChartPanel chartPanel;
	protected JFreeChart TOFChart;
	protected XYLineAndShapeRenderer renderer;
	
	protected ArrayList<Boolean> isReal;

	public TOFView(SBApp app, MainFrame parent) {
		// TODO Auto-generated constructor stub
		mainWindow = parent;
		sb = app;
		
		this.addFocusListener(parent);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		associatedTOFs = new ArrayList<TOFData>();
		isReal = new ArrayList<Boolean>();
		this.addInternalFrameListener(this);
		
		TOFDataset = new XYSeriesCollection( );
		TOFChart = ChartFactory.createXYLineChart(null, "time", "Intensity (arb.)", TOFDataset, PlotOrientation.VERTICAL, false, false, false);
		TOFPlot = TOFChart.getXYPlot();
		chartPanel = new ChartPanel(TOFChart);
		
		setContentPane(chartPanel); 
	}

	public void setupWindow(){
		//xylineChart.removeLegend();
	}
	
	public void execute() {
		mainWindow.addFrame(this);
		setupWindow();
		this.setResizable(true);
		this.setPreferredSize(new Dimension(400, 200));
		this.pack();
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setClosable(true);
		this.setFocusable(true);
		this.setEnabled(true);
		this.setVisible(true);
	}
	
	
	public void addTOFToView(TOFData tof){
		if(!tof.is_real_TOF) {
			addCalcTOFToView(tof);
			return;
		}
		
		associatedTOFs.add(tof);
		XYSeries tofSeries = new XYSeries(tof.title);
		for(int i=0; i < tof.actual_flight_time_micro.length; i++){
			tofSeries.add(tof.actual_flight_time_micro[i], tof.channel_counts[i]);
		}
		TOFDataset.addSeries(tofSeries);
		
		Shape marker = new Ellipse2D.Double(0, 0, 3, 3);
		((XYLineAndShapeRenderer)TOFPlot.getRenderer()).setSeriesLinesVisible(TOFDataset.getSeriesCount()-1, false);
		((XYLineAndShapeRenderer)TOFPlot.getRenderer()).setSeriesShapesVisible(TOFDataset.getSeriesCount()-1, true);
		TOFPlot.getRenderer().setSeriesShape(TOFDataset.getSeriesCount() - 1, marker);

		
		tof.AssociatedTOFViews.add(this);
		this.title += (this.title.isEmpty() ? "" : " && ") + tof.title;
	}
	
	public void addCalcTOFToView(TOFData tof){
		associatedTOFs.add(tof);
		
		//get scaling factor
		float[] maxMin = tof.GetMaxMinCounts(tof.actual_flight_time_micro[0], tof.actual_flight_time_micro[tof.actual_flight_time_micro.length-1]);
		float[] totalMaxMin = associatedTOFs.get(0).GetMaxMinCounts(associatedTOFs.get(0).actual_flight_time_micro[0], associatedTOFs.get(0).actual_flight_time_micro[associatedTOFs.get(0).actual_flight_time_micro.length-1]); // fix this
		float scaling = totalMaxMin[0]/maxMin[0];
		
		XYSeries tofSeries = new XYSeries(tof.title);
		for(int i=0; i < tof.actual_flight_time_micro.length; i++){
			tofSeries.add(tof.actual_flight_time_micro[i], tof.channel_counts[i]*scaling);
		}
		TOFDataset.addSeries(tofSeries);
		((XYLineAndShapeRenderer)TOFPlot.getRenderer()).setSeriesLinesVisible(TOFDataset.getSeriesCount()-1, true);
		((XYLineAndShapeRenderer)TOFPlot.getRenderer()).setSeriesShapesVisible(TOFDataset.getSeriesCount()-1, false);

		tof.AssociatedTOFViews.add(this);
		this.title += (this.title.isEmpty() ? "" : " && ") + tof.title;
	}
	
	public void reloadTOF(TOFData tof){
		int index = associatedTOFs.indexOf(tof);
		removeTOFFromView(index);
		addTOFToView(tof);
	}
	
	public void removeTOFFromView(int index){
		TOFDataset.removeSeries(index);
		associatedTOFs.get(index).AssociatedTOFViews.remove(this);
		associatedTOFs.remove(index);
	}
	
	
	protected String[] getAllTOFList(){
		String[] list = new String[sb.tofs.size()];
		for(int i=0; i < sb.tofs.size(); i++){
			list[i] = sb.tofs.get(i).title;
		}
		return list;
	}
	
	protected String[] getDispTOFList(){
		String[] list = new String[associatedTOFs.size()];
		for(int i=0; i < associatedTOFs.size(); i++){
			list[i] = associatedTOFs.get(i).title;
		}
		return list;
	}
	
	
	
	
	
	/* MENU ITEMS */
	protected void AppendLoadedTOF()
	{
		String[] tofList = getAllTOFList();
		List_Dialog tof_list_dialog = new List_Dialog(mainWindow, tofList, 1);
		tof_list_dialog.SetCaption("Choose a TOF to append:");

		tof_list_dialog.Execute();
		// check
		if (tof_list_dialog.ID != true){
			return;
		}
		
		TOFData time_of_flight = sb.tofs.get(tof_list_dialog.GetChosenIndex()[0]);
		System.out.println("Adding " + time_of_flight.title);
		if(time_of_flight.is_real_TOF){
			addTOFToView(time_of_flight);
		}else{
			addCalcTOFToView(time_of_flight);
		}

	}
	
	protected void RemoveTOFFromDisplay() {
		String[] tofList = getDispTOFList();
		List_Dialog tof_list_dialog = new List_Dialog(mainWindow, tofList, 1);
		tof_list_dialog.SetCaption("Choose a TOF to remove:");

		tof_list_dialog = new List_Dialog(mainWindow, tofList, 1);

		tof_list_dialog.Execute();
		if(!tof_list_dialog.ID) return; //check if ok clicked

		int chosen_index = tof_list_dialog.GetChosenIndex()[0];
		
		removeTOFFromView(chosen_index);
		
		//fix residuals here

	}
	
	protected void AxisRange(){
		Param_Dialog param_dialog = new Param_Dialog(mainWindow, 1);
		
		param_dialog.SetDefault1("0.0");
		param_dialog.SetDefault2("1.0");
		param_dialog.SetValue1("" + (TOFPlot.getDomainAxis().getRange().getLowerBound()));
		param_dialog.SetValue2("" + (TOFPlot.getDomainAxis().getRange().getUpperBound()));
		
		param_dialog.Execute();
		if(!param_dialog.ID){
			return;
		}
		float starting_time = Float.parseFloat(param_dialog.edit1.getText());
		float ending_time = Float.parseFloat(param_dialog.edit2.getText());
		
		NumberAxis domain = (NumberAxis) TOFPlot.getDomainAxis();
        domain.setRange(starting_time, ending_time);
		
	}
	
	protected void SetColors(){
		String[] tofList = getDispTOFList();
		List_Dialog tof_list_dialog = new List_Dialog(mainWindow, tofList, 1);
		tof_list_dialog.SetCaption("Choose a TOF:");
		tof_list_dialog.Execute();
		if(!tof_list_dialog.ID) return; //check if ok clicked
		int chosen_index = tof_list_dialog.GetChosenIndex()[0];
		
		Color c = JColorChooser.showDialog(this, "Choose Color", Color.black);
		if(c != null){
			associatedTOFs.get(chosen_index).time_of_flight_color = c;
			TOFPlot.getRendererForDataset(TOFDataset).setSeriesPaint(chosen_index, c);
		}
	}
	
	protected void EditViewTOFParameters() {
		// pick a tof from this view
		TOFData tof;
		if(this.associatedTOFs.size() == 1) {
			tof = this.associatedTOFs.get(0);
		}else {
			String[] tofList = getDispTOFList();
			List_Dialog tof_list_dialog = new List_Dialog(mainWindow, tofList, 1);
			tof_list_dialog.SetCaption("Choose a TOF to edit:");
			tof_list_dialog = new List_Dialog(mainWindow, tofList, 1);

			tof_list_dialog.Execute();
			if(!tof_list_dialog.ID) return; //check if ok clicked
			int chosen_index = tof_list_dialog.GetChosenIndex()[0];
			
			tof = this.associatedTOFs.get(chosen_index);
		}

		//Allow User to input params for TOF
		TOF_Input_1_Dialog tof_input_1 = new TOF_Input_1_Dialog(this.mainWindow, tof);
		tof_input_1.execute();
		if(tof_input_1.ID == false){
			return;
		}
		
		tof.loadFromInputDialog(tof_input_1);
		reloadTOF(tof);
	}
	
	
	
	
	
	
	
	/* LISTENER METHODS */

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

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
		for (int i = 0; i < associatedTOFs.size(); i++) {
			associatedTOFs.get(i).AssociatedTOFViews.remove(this);
			// Each TOF can only be in view once!
			associatedTOFs.get(i).is_Visible = -2;// CHANGE:set not visible
		}
		mainWindow.internalClosed(this);
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
	
}
