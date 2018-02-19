package sbeam2.gui;

import java.awt.Dimension;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.MouseInputListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import sbeam2.gui.Param_Dialog;
import sbeam2.gui.MainFrame;
import sbeam2.SBApp;
import sbeam2.TOFData;

public class TOFView extends JInternalFrame implements MouseInputListener, InternalFrameListener{
	
	protected ArrayList<TOFData> associatedTOFs;
	protected MainFrame mainWindow;
	protected SBApp sb;
	
	protected XYSeriesCollection realTOFDataset;
	protected XYPlot TOFPlot;
	protected ChartPanel chartPanel;
	protected JFreeChart xylineChart;
	protected XYLineAndShapeRenderer renderer;

	public TOFView(SBApp app, MainFrame parent) {
		// TODO Auto-generated constructor stub
		mainWindow = parent;
		sb = app;
		
		this.addFocusListener(parent);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		associatedTOFs = new ArrayList<TOFData>();
		this.addInternalFrameListener(this);
		
		realTOFDataset = new XYSeriesCollection( );
		xylineChart = ChartFactory.createXYLineChart(
		         null,
		         "time (us)" ,
		         "Counts" ,
		         realTOFDataset ,
		         PlotOrientation.VERTICAL ,
		         true , true , false);
		chartPanel = new ChartPanel( xylineChart );
		TOFPlot = xylineChart.getXYPlot( );
		renderer = new XYLineAndShapeRenderer( );
		TOFPlot.setRenderer(renderer); 
		setContentPane(chartPanel); 
	}

	public void setupWindow(){
		xylineChart.removeLegend();
	}
	
	public void execute() {
		mainWindow.addFrame(this);
		setupWindow();
		this.setResizable(true);
		this.setPreferredSize(new Dimension(400, 200));
		this.pack();
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setFocusable(true);
		this.setEnabled(true);
		this.setVisible(true);
	}
	
	
	public void addTOFToView(TOFData tof){
		associatedTOFs.add(tof);
		XYSeries tofSeries = new XYSeries(tof.title);
		for(int i=0; i < tof.actual_flight_time_micro.length; i++){
			tofSeries.add(tof.actual_flight_time_micro[i], tof.channel_counts[i]);
		}
		realTOFDataset.addSeries(tofSeries);
		
		tof.AssociatedTOFViews.add(this);
	}
	
	public void addCalcTOFToView(TOFData tof){
		associatedTOFs.add(tof);
		
		//get scaling factor
		float[] maxMin = tof.GetMaxMinCounts(tof.actual_flight_time_micro[0], tof.actual_flight_time_micro[tof.actual_flight_time_micro.length-1]);
		float[] totalMaxMin = associatedTOFs.get(0).GetMaxMinCounts(associatedTOFs.get(0).actual_flight_time_micro[0], associatedTOFs.get(0).actual_flight_time_micro[tof.actual_flight_time_micro.length-1]);
		float scaling = totalMaxMin[0]/maxMin[0];
		
		XYSeries tofSeries = new XYSeries(tof.title);
		for(int i=0; i < tof.actual_flight_time_micro.length; i++){
			tofSeries.add(tof.actual_flight_time_micro[i], tof.channel_counts[i]*scaling);
		}
		realTOFDataset.addSeries(tofSeries);
		
		tof.AssociatedTOFViews.add(this);
	}
	
	public void reloadCalcTOF(TOFData tof){
		int index = associatedTOFs.indexOf(tof);
		System.out.println("Reloading series at index " + index);
		removeTOFFromView(index);
		addCalcTOFToView(tof);
	}
	
	public void removeTOFFromView(int index){
		realTOFDataset.removeSeries(index);
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
		param_dialog.SetValue1("" + (-Float.MIN_VALUE));
		param_dialog.SetValue2("" + Float.MAX_VALUE);
		
		param_dialog.Execute();
		if(!param_dialog.ID){
			return;
		}
		float starting_time = Float.parseFloat(param_dialog.edit1.getText());
		float ending_time = Float.parseFloat(param_dialog.edit2.getText());
		
		NumberAxis domain = (NumberAxis) TOFPlot.getDomainAxis();
        domain.setRange(starting_time, ending_time);
		
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
