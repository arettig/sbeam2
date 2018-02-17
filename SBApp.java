package sbeam2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFileChooser;

import sbeam2.gui.Instr_Param_Dialog;
import sbeam2.gui.MainFrame;
import sbeam2.gui.TOFView;
import sbeam2.gui.TOF_Input_1_Dialog;


public class SBApp {
	public MainFrame mf;
	public ArrayList<TOFData> tofs;
	public ArrayList<TOFView> tofViews;
	public InstrumentParameters instrParam;
	
	public boolean InstParamsSet;

	public SBApp() {
		// TODO Auto-generated constructor stub
		MainFrame mf = new MainFrame("Sbeam",this);
		this.mf = mf;
		tofs = new ArrayList<TOFData>();
		tofViews = new ArrayList<TOFView>();
		
		InstParamsSet = false;
		instrParam = new InstrumentParameters();
		
		mf.Execute();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SBApp app = new SBApp();
	}
	
	
	
	
	
	
	/* BEGIN TOF METHODS*/
	public void DisplayNewTOF(){
		//let user choose a TOF
		JFileChooser fc = new JFileChooser();
		int ID = fc.showOpenDialog(mf);
		if (ID != JFileChooser.APPROVE_OPTION) {
			return;
		}
		
		File f = fc.getSelectedFile();
		TOFData time_of_flight = new TOFData();

		String extension;
		extension = f.getName().substring(f.getName().length() - 4);
		if (extension.equals(".tuf")) {
			time_of_flight.format= 0;
		} else {
			time_of_flight.format= 1;
		}
		
		
		//Read TOF from file
		time_of_flight.loadFromFile(f);
		tofs.add(time_of_flight);		


		//Allow User to input params for TOF
		TOF_Input_1_Dialog tof_input_1 = new TOF_Input_1_Dialog(mf);
		tof_input_1.execute();
		if(tof_input_1.ID == false){
			return;
		}
		time_of_flight.loadFromInputDialog(tof_input_1);
		
		
		//Setup Instrument Parameters if not done already
		if (!InstParamsSet) {
			setupInstParam();
		}
		
		time_of_flight.SetRealFlightTime();
		
		//Create a View for TOF
		TOFView t = new TOFView(this, mf);
		t.addTOFToView(time_of_flight);
		tofViews.add(t);
		t.execute();
	    time_of_flight.is_Visible = -1;  //CHANGE: change var when visible
	}
	
	
	
	
	
	
	public void setupInstParam(){
		Instr_Param_Dialog instr_params = new Instr_Param_Dialog(mf, instrParam);
		instr_params.Execute();
		
		if(!instr_params.ID){
			return;
		}
		
		instrParam.ionFlightConst = Float.parseFloat(instr_params.GetIonFlightConst());
		instrParam.ionizerLen = Float.parseFloat(instr_params.GetIonizerLen());
		instrParam.flightLen = Float.parseFloat(instr_params.GetFlightLen());
		instrParam.beamAng = Float.parseFloat(instr_params.GetBeamAng());
		instrParam.detectAng = Float.parseFloat(instr_params.GetDetectAng());
		instrParam.isNumDensity = instr_params.isNumDensity();
		
		InstParamsSet = true;
	}
	
	
	
	
}
