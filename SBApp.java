package sbeam2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import sbeam2.gui.POEView;
import sbeam2.gui.POE_Info_Dialog_1;
import sbeam2.POEData;
import sbeam2.gui.CalculateMainDialog;
import sbeam2.gui.Instr_Param_Dialog;
import sbeam2.gui.List_Dialog;
import sbeam2.gui.MainFrame;
import sbeam2.gui.TOFView;
import sbeam2.gui.TOF_Edit_Dialog_2;
import sbeam2.gui.TOF_Input_1_Dialog;


public class SBApp {
	public MainFrame mf;
	public ArrayList<TOFData> tofs;
	public ArrayList<TOFView> tofViews;
	public ArrayList<POEData> poes;
	public ArrayList<POEView> poeViews;
	public ArrayList<CalcData> calcs;
	public InstrumentParameters instrParam;
	
	public boolean InstParamsSet;
	public int energy_unit;

	public SBApp() {
		// TODO Auto-generated constructor stub
		MainFrame mf = new MainFrame("Sbeam",this);
		this.mf = mf;
		tofs = new ArrayList<TOFData>();
		tofViews = new ArrayList<TOFView>();
		poes = new ArrayList<POEData>();
		poeViews = new ArrayList<POEView>();
		calcs = new ArrayList<CalcData>();

		InstParamsSet = false;
		instrParam = new InstrumentParameters();
		
		mf.Execute();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SBApp app = new SBApp();
	}
	
	
	
	
	/* BEGIN FILE METHODS */
	
	public void Save() {
		SaveAs();
		
	}
	
	public void SaveInCompatibilityMode(){
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("UFC file", "ufc"));
		int returnVal = fc.showSaveDialog(mf);
		if(returnVal != JFileChooser.APPROVE_OPTION){
			return;
		}
		File f = fc.getSelectedFile();
		if (!f.getName().endsWith(".ufc")) {
		    f = new File(f.getAbsolutePath() + ".ufc");  
		}
		SaveFile(f, true);
	}

	public void SaveAs() {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("UFC file", "ufc"));
		int returnVal = fc.showSaveDialog(mf);
		if(returnVal != JFileChooser.APPROVE_OPTION){
			return;
		}
		File f = fc.getSelectedFile();
		if (!f.getName().endsWith(".ufc")) {
		    f = new File(f.getAbsolutePath() + ".ufc");  
		}
		SaveFile(f, false);
	}

	public void Open() {
		String message;
		String old_file_name;

		//check if changed?


		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("UFC file", "ufc"));
		int i = fc.showOpenDialog(mf);
		if (i == JFileChooser.APPROVE_OPTION) {
			Scanner is;
			try {
				is = new Scanner(fc.getSelectedFile());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}

			switch (LoadUFCData(is)) {
			case 0: // Error = 0 . File not in correct *.ufc format
				System.out
						.println("File not in correct *.ufc format File Error");
				break;
			case 1: // Error = 1 . File only correct to load TOF data
				System.out
						.println("File not in correct format.  Only TOFs correctly loaded.File Error");
				break;
			default: // Everything is OK; don't do anything
				// CHANGE: add displaying feature here
				TOFData toffers;
				for (int loopMe = 0; loopMe < tofs.size(); loopMe++) {
					toffers = tofs.get(loopMe);
					switch (toffers.is_Visible) {
					case -2:
						break;
					case -1:

						TOFView t = new TOFView(this, mf);
						t.addTOFToView(toffers);
						tofViews.add(t);
						t.execute();
						break;
					default:
						
						/*
						 * toffers.AddAssociatedView(session_document.toffers.
						 * GetIsVisible());
						 * session_document.ResetTOFsInTOFViews();
						 */
						break;
					}
				}

				POEData poffers;
				for (int loopMe = 0; loopMe < poes.size(); loopMe++) {
					poffers = poes.get(loopMe);
					switch (poffers.is_Visible) {
					case -2:
						break;
					case -1:

						POEView p = new POEView(this,mf);
						p.addPOEToView(poffers);
						poeViews.add(p);
						p.Execute();
						break;
					default:
						break;
					}
				}

				//TileVertically();
				break;
			}
		}

	}
	
	
	protected void SaveFile(File f, boolean compatibilityMode){	
		try {
			f.createNewFile();
			String saveLoc = f.getAbsolutePath();
			FileWriter fw;
			fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);

			OutputUFCData(bw, compatibilityMode);

			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void OutputUFCData(BufferedWriter os, boolean compatMode) {
		int i, num_tofs, num_poes;

		num_tofs = tofs.size();
		num_poes = poes.size();

		try {
			os.write("...SBEAM *.ufc file...\n\n");

			os.write(num_tofs + "\n");

			for (i = 0; i < num_tofs; i++) {
				os.write(tofs.get(i).out(compatMode) + "\n\n");
			}

			os.write(instrParam.ionFlightConst + " " + instrParam.ionizerLen + " "
					+ instrParam.flightLen + " ");
			os.write(instrParam.beamAng + " " + instrParam.detectAng + " "
					+ ((compatMode)?((instrParam.isNumDensity)?1:0):instrParam.isNumDensity) + "\n");

			os.write("\n...SBEAM *.ufc file...\n\n");
			os.write(num_poes + "\n)");

			for (i = 0; i < num_poes; i++) {
				os.write(poes.get(i).out(compatMode) + "\n\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int LoadUFCData(Scanner is) {
		TOFData new_tof;
		POEData new_poe;

		int i, num_loaded_tofs, num_loaded_poes;
		String temp_string;

		temp_string = is.nextLine();
		if (!temp_string.equals("...SBEAM *.ufc file...")) {
			return 0; // Return value of zero => Incorrect file format
		}
		is.nextLine();

		num_loaded_tofs = is.nextInt();
		System.out.println("loading " + num_loaded_tofs + " tofs");
		for (i = 0; i < num_loaded_tofs; i++) {
			// Get a line of "\n"
			temp_string = is.nextLine();
			new_tof = TOFData.in(is);
			tofs.add(new_tof);
		}

		instrParam.ionFlightConst = is.nextFloat();
		instrParam.ionizerLen = is.nextFloat();
		instrParam.flightLen = is.nextFloat();
		instrParam.beamAng = is.nextFloat();
		instrParam.detectAng = is.nextFloat();
		instrParam.isNumDensity = (is.hasNextBoolean()) ? is.nextBoolean(): (is.nextInt() == 1) ? true:false;

		for(i = 0; i < tofs.size(); i++){
			tofs.get(i).SetIonFlightTime(instrParam.ionFlightConst);
		}

		// Get a line of "\n"
		is.nextLine();
		is.nextLine();
		temp_string = is.nextLine();
		if (!temp_string.equals("...SBEAM *.ufc file...")) {
			return 1; // Return value of one => Incorrect file format after TOFs
						// are loaded
		}

		is.nextLine();
		num_loaded_poes = is.nextInt();
		for (i = 0; i < num_loaded_poes; i++) {
			// Get a line of "\n"
			temp_string = is.nextLine();
			new_poe = new POEData();
			new_poe = POEData.in(is);
			poes.add(new_poe);

			temp_string = is.nextLine();
		}
		temp_string = is.nextLine();

		return -1; // Return value of -1 => Load of *.ufc was successful
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
		TOF_Input_1_Dialog tof_input_1 = new TOF_Input_1_Dialog(mf, time_of_flight);
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
	
	public void DisplayLoadedTOF(){
		String[] tofList = getAllTOFList();
		List_Dialog tof_list_dialog = new List_Dialog(mf, tofList, 1);
		tof_list_dialog.SetCaption("Choose a TOF to append:");

		tof_list_dialog.Execute();
		// check
		if(!tof_list_dialog.ID) return;
		
		TOFData time_of_flight = tofs.get(tof_list_dialog.GetChosenIndex()[0]);
		
		if (time_of_flight.is_real_TOF) {
			TOF_Input_1_Dialog tof_input_1 = new TOF_Input_1_Dialog(mf, time_of_flight);
			tof_input_1.execute();
			if(!tof_input_1.ID) return;
			
			time_of_flight.loadFromInputDialog(tof_input_1);


			//Create a View for TOF
			TOFView t = new TOFView(this, mf);
			t.addTOFToView(time_of_flight);
			tofViews.add(t);
			t.execute();
		    time_of_flight.is_Visible = -1;  //CHANGE: change var when visible
		}
		else
		{
			/*TOF_Edit_Dialog_2 tof_input_2 = new TOF_Edit_Dialog_2(mf, time_of_flight);
			
			tof_input_2.Execute();
			if(!tof_input_2.ID) return;
			
			time_of_flight.loadFromInputDialog(tof_input_2);*/
			
			//Create a View for TOF
			TOFView t = new TOFView(this, mf);
			t.addTOFToView(time_of_flight);
			tofViews.add(t);
			t.execute();
		    time_of_flight.is_Visible = -1;  //CHANGE: change var when visible
		}
	}
	
	
	
	
	
	
	
	
	
	/* BEGIN POE METHODS */
	public void Graphically(){
		//create a new POE object
		POEData poe = new POEData();
		poe.title = "POE1";
		

		// Open a Dialog window to get user params
		POE_Info_Dialog_1 poe_info_1 = new POE_Info_Dialog_1(mf);
		poe_info_1.SetEnergyUnits("" + energy_unit);
		poe_info_1.SetClearEnergyLines();
		
		poe_info_1.SetMinPossibleEnergy(Float.MIN_VALUE);
		poe_info_1.SetTitle(poe.title);
		poe_info_1.SetMinEnergy("" + 0);
		poe_info_1.SetMaxEnergy("" + 100);
		poe_info_1.SetNumPoints("" + 101);

		poe_info_1.Execute();
		// check
		if (!poe_info_1.ID) return;

		// load user params into object
		poe.title = poe_info_1.GetTitle();
		float convert_kcal_to_units = Convert_kcal_TO_session_units(1.0f);
		poe.min_energy = (Float.parseFloat(poe_info_1.GetMinEnergy()) / convert_kcal_to_units);
		poe.max_energy = (Float.parseFloat(poe_info_1.GetMaxEnergy()) / convert_kcal_to_units);
		poe.num_points = Integer.parseInt(poe_info_1.GetNumPoints());

		// create poe data arrays
		float energy_increment = (poe.max_energy - poe.min_energy) / (poe.num_points - 1);
		float[] energy_values = new float[(int) poe.num_points];
		float[] energy_amplitudes = new float[(int) poe.num_points];

		for(int i = 0; i < poe.num_points; i++)
		{
			energy_values[i] = poe.min_energy + (i * energy_increment);
			energy_amplitudes[i] = 0.0f;
		}
		poe.poe_amplitudes = energy_amplitudes;
		poe.energy_values = energy_values;
		poe.energy_spacing = energy_values[1] - energy_values[0];
	

		
		// create poe view
		POEView p = new POEView(this, mf);
		p.addPOEToView(poe);
		
		poes.add(poe);
		poeViews.add(p);
		
		p.Execute();
		poe.is_Visible = -1;	//CHANGE: visibility = -1 upon new display

	}
	
	public void OpenDisplaypoeFile() {
		//create new poe object
		POEData poe = new POEData();
		
		//let user choose a file
		JFileChooser fc = new JFileChooser();
		int ID = fc.showOpenDialog(mf);
		if (ID != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File f = fc.getSelectedFile();

		//load data from file into object
		poe.LoadFromFile(f);

		//create a view for poe
		POEView p = new POEView(this, mf);
		p.addPOEToView(poe);
		
		poes.add(poe);
		poeViews.add(p);
		
		poe.is_Visible = -1;	//CHANGE: visibility = -1 upon new display
		p.Execute();

	}
	
	
	
	
	
	
	/* BEGIN CALC METHODS */
	public void CalculateTOFsforagivenme() {
		float ionizer_len = instrParam.ionizerLen;
		int num_current_poes = poes.size();
		
		CalculateMainDialog calc_dialog;
		CalcData calculation = new CalcData(calcs.size(), num_current_poes,
				ionizer_len, mf, this);

		calculation.flight_length = instrParam.flightLen;
		calculation.beam_angular_width = instrParam.beamAng;
		calculation.detector_angular_width = instrParam.detectAng;
		calculation.is_number_density_calc = instrParam.isNumDensity;

		calcs.add(calculation);


		calc_dialog = new CalculateMainDialog(mf, this);
		calc_dialog.SetDialogData(calculation);
		calc_dialog.SetAreInstParamsSet(InstParamsSet);

		calc_dialog.Execute();
		if(!calc_dialog.ID) return;
		
		calculation.CalcNumber = calcs.size()-1;
	}
	
	public void CalculateTOFs(CalcData calc){
		TOFData[] new_tofs;                             
		POEData[] current_poes;
		Integer num_new_tofs = 0;

		int[] included_tof_num_array;
		
		current_poes = new POEData[poes.size()];
		for(int i = 0; i < poes.size(); i++){
			current_poes[i] = poes.get(i);
		}

		calc.SetTOF2CosGamma();
		new_tofs = calc.RunMainFlightTimeCalculation(current_poes, num_new_tofs, instrParam.beamAng, instrParam.detectAng);

		System.out.println("Adding " + new_tofs.length + " new calculated tofs");
		num_new_tofs = calc.num_tofs;
		if(new_tofs == null)
			return;

		included_tof_num_array = new int[new_tofs.length];
		for(int i = 0; i < new_tofs.length; i++)
		{
			tofs.add(new_tofs[i]);
			included_tof_num_array[i] = new_tofs[i].TOF_num;
		}
		calc.included_tof_num_array = included_tof_num_array;
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
	
	
	
	
	
	/* BEGIN HELPER METHODS */
	public float Convert_kcal_TO_session_units(float old_number){
		switch(energy_unit)
		{
		case 0:
			return old_number;
		case 1:
			return (old_number * 4.184f);
		case 2:
			return (old_number * 349.64f);
		default:
			return (old_number * 0.043348f);
		}
	}
	
	protected String[] getAllTOFList(){
		String[] list = new String[tofs.size()];
		for(int i=0; i < tofs.size(); i++){
			list[i] = tofs.get(i).title;
		}
		return list;
	}
	
}
