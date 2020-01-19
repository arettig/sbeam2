package sbeam2;

import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
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
import sbeam2.gui.TOF_Subtract_Dialog;


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
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			new_tof = TOFData.in(is, this);
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
		System.out.println("loading " + num_loaded_poes + " poes");
		for (i = 0; i < num_loaded_poes; i++) {
			// Get a line of "\n"
			temp_string = is.nextLine();
			new_poe = new POEData();
			new_poe = POEData.in(is);
			new_poe.POE_num = poes.size();
			poes.add(new_poe);

			temp_string = is.nextLine();
		}
		temp_string = is.nextLine();

		return -1; // Return value of -1 => Load of *.ufc was successful
	}
	
	
	
	
	
	
	
	
	
	/* BEGIN INSTRUMENTAL PARAMS */
	public void SetInstrumentalParameters(){
		setupInstParam();
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
		
		for(int i = 0; i < this.tofs.size(); i++) {
			this.tofs.get(i).SetRealFlightTime();
		}
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
		TOFData time_of_flight = new TOFData(this);

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
		//Setup Instrument Parameters if not done already
		if (!InstParamsSet) {
			setupInstParam();
		}
		
		
		time_of_flight.loadFromInputDialog(tof_input_1);
		
		//time_of_flight.SetRealFlightTime();
		
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
	
	public void PerformTOFSubtraction(){
		//choose tofs for subtraction
		String[] tofList = getAllTOFList();
		List_Dialog tof_list_dialog = new List_Dialog(mf, tofList, 2);
		tof_list_dialog.SetCaption("Choose two real TOFs for subtraction:");
		tof_list_dialog.Execute();
		if(!tof_list_dialog.ID) return;
		
		//get the tofs from dialog
		TOFData tof_1 = tofs.get(tof_list_dialog.GetChosenIndex()[0]);
		TOFData tof_2 = tofs.get(tof_list_dialog.GetChosenIndex()[1]);
		
		//check to make sure tofs are subtractable
		if(CompareTOFs(tof_1, tof_2) == false)
		{
			JOptionPane.showMessageDialog(mf, "TOFs must have same dwell time, offset, ion m/e, and number of points. Cannot perform subtraction with these TOFs!");
			return;
		}
		
		// show subtraction dialog
		TOF_Subtract_Dialog tof_subtract = new TOF_Subtract_Dialog(mf, tof_1.title, tof_2.title);
		tof_subtract.Execute();
		if(tof_subtract.ID != true) return;
		
		//switch tofs if necessary
		TOFData temp_tof;
		if(!tof_subtract.title1.getText().equals(tof_1.title))
		{
			temp_tof = tof_2;
			tof_2 = tof_1;
			tof_1 = temp_tof;
		}
		
		//calculate new tof
		TOFData new_tof = TOFMath(tof_1, tof_2, false);
		
		//setup the new tof
		TOF_Input_1_Dialog tof_input_1 = new TOF_Input_1_Dialog(mf, new_tof);
		new_tof.loadFromInputDialog(tof_input_1);
		tofs.add(new_tof);
		
		TOFView t = new TOFView(this, mf);
		t.addTOFToView(new_tof);
		tofViews.add(t);
		t.execute();
	    new_tof.is_Visible = -1;  //CHANGE: change var when visible
	}

	public void PerformTOFAddition() {
		//choose tofs for subtraction
		String[] tofList = getAllTOFList();
		List_Dialog tof_list_dialog = new List_Dialog(mf, tofList, 2);
		tof_list_dialog.SetCaption("Choose two real TOFs for addition:");
		tof_list_dialog.Execute();
		if(!tof_list_dialog.ID) return;
		
		//get the tofs from dialog
		TOFData tof_1 = tofs.get(tof_list_dialog.GetChosenIndex()[0]);
		TOFData tof_2 = tofs.get(tof_list_dialog.GetChosenIndex()[1]);
		
		//check to make sure tofs are subtractable
		if(CompareTOFs(tof_1, tof_2) == false)
		{
			JOptionPane.showMessageDialog(mf, "TOFs must have same dwell time, offset, ion m/e, and number of points. Cannot perform addition with these TOFs!");
			return;
		}
		
		//calculate new tof
		TOFData new_tof = TOFMath(tof_1, tof_2, true);
		
		//setup the new tof
		TOF_Input_1_Dialog tof_input_1 = new TOF_Input_1_Dialog(mf, new_tof);
		new_tof.loadFromInputDialog(tof_input_1);
		tofs.add(new_tof);
		
		TOFView t = new TOFView(this, mf);
		t.addTOFToView(new_tof);
		tofViews.add(t);
		t.execute();
	    new_tof.is_Visible = -1;  //CHANGE: change var when visible
	}
	
	public void DeleteLoadedTOFs(){
		String[] tofList = getAllTOFList();
		List_Dialog tof_list_dialog = new List_Dialog(mf, tofList, 0);
		tof_list_dialog.SetCaption("Delete TOFs which are not in use:");
		tof_list_dialog.Execute();
		if(!tof_list_dialog.ID) return;
		
		int num_chosen = tof_list_dialog.GetChosenIndex().length;
		String message;
		if(num_chosen == 1)
		{
			message = "Are you sure you want to delete this TOF?";
		}
		else
		{
			message = "Are you sure you want to delete these " + num_chosen + " TOFs?";
		}
		
		
		if(JOptionPane.showConfirmDialog(mf, message) == JOptionPane.OK_OPTION){
			int[] chosen_array = tof_list_dialog.GetChosenIndex();
			for(int i = num_chosen-1; i >= 0; i--){
				int index = chosen_array[i];
				TOFData tof = tofs.get(index);
				for(int j = 0; j < tof.AssociatedTOFViews.size(); j++){
					TOFView view = tof.AssociatedTOFViews.get(j);
					view.removeTOFFromView(view.associatedTOFs.indexOf(tof));
				}
				tofs.remove(tof);
			}
		}
	}
	
	
	public void OutputTOFforGraphing() {
		// pick a tof from this view
		TOFData tof;
		if(this.tofs.size() == 1) {
			tof = this.tofs.get(0);
		}else {
			String[] tofList = getAllTOFList();
			List_Dialog tof_list_dialog = new List_Dialog(mf, tofList, 1);
			tof_list_dialog.SetCaption("Choose a TOF to output:");

			tof_list_dialog.Execute();
			// check
			if(!tof_list_dialog.ID) return;
			
			tof = tofs.get(tof_list_dialog.GetChosenIndex()[0]);
		}
		
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("tgr file", "tgr"));
		int returnVal = fc.showSaveDialog(this.mf);
		if(returnVal != JFileChooser.APPROVE_OPTION){
			return;
		}
		File f = fc.getSelectedFile();
		if (!f.getName().endsWith(".tgr")) {
		    f = new File(f.getAbsolutePath() + ".tgr");  
		}

		String output = "";
		
		if(this.instrParam.isNumDensity) {
			output += "Flight time (µs),     N(t) (arb. units)\n";
		}else {
			output += "Flight time (µs),     I(t) (arb. units)\n";
		}
		
		if(tof.is_real_TOF) {
			for(int i = 0; i < tof.num_tot_channels; i++) {
				output += tof.actual_flight_time_micro[i] + ",     " + tof.channel_counts[i] + "\n";
			}
		}else {
			for(int i = 0; i < tof.num_tot_channels; i++) {
				output += tof.actual_flight_time_micro[i] + ",     " + tof.channel_counts[i];
				for(int j = 0; j < this.poes.size(); j++) {
					if(tof.individual_tofs[j] != null) {
						int numChans = tof.number_channels[j];
						if(numChans == 1) numChans = 0;  // Don't draw TOF for the individual channel if only one channel exists
						for(int k = numChans; k >= 0; k--) { // Backwards, so total P(E) TOF drawn last
							if((k !=  0) || (tof.number_included_poes > 1)) { // i.e. skip plotting individual TOFs for each contributing P(E) if only one contributes
								output += ",     " + tof.individual_tofs[j][k][i];
							}
						}
					}
				}
			}
			output += "\n";
		}
		
		try {
			f.createNewFile();
			FileWriter fw;
			fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(output);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
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
		poe.POE_num = poes.size();
	

		
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
	
	public void DisplayStoredPE(){
		String[] poeList = getAllPOEList();
		List_Dialog poe_list_dialog = new List_Dialog(mf, poeList, 1);
		poe_list_dialog.SetCaption("Choose a POE to open:");

		poe_list_dialog.Execute();
		// check
		if(!poe_list_dialog.ID) return;
		
		POEData poe = poes.get(poe_list_dialog.GetChosenIndex()[0]);

		//create a view for poe
		POEView p = new POEView(this, mf);
		p.addPOEToView(poe);
		
		poes.add(poe);
		poeViews.add(p);
		
		poe.is_Visible = -1;	//CHANGE: visibility = -1 upon new display
		p.Execute();
	}
	
	public void OutputPEtoFile() {
		//choose save location
		int[] index_array;
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("poe file", "poe"));
		int returnVal = fc.showSaveDialog(mf);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File f = fc.getSelectedFile();
		if (!f.getName().endsWith(".poe")) {
			f = new File(f.getAbsolutePath() + ".poe");
		}

		// let user choose poe
		String[] poeList = getAllPOEList();
		List_Dialog poe_list_dialog = new List_Dialog(mf, poeList, 1);
		poe_list_dialog.SetCaption("Choose a POE to open:");
		poe_list_dialog.Execute();
		if(!poe_list_dialog.ID) return;
		POEData poe = poes.get(poe_list_dialog.GetChosenIndex()[0]);

		try {
			f.createNewFile();
			FileWriter fw;
			fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);
			poe.SaveAsPOEFile(bw);

			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void DeleteStoredPE(){
		String[] poeList = getAllPOEList();
		List_Dialog poe_list_dialog = new List_Dialog(mf, poeList, 1);
		poe_list_dialog.SetCaption("Choose a POE to open:");
		poe_list_dialog.Execute();
		if(!poe_list_dialog.ID) return;
		
		POEData poe = poes.get(poe_list_dialog.GetChosenIndex()[0]);
		for(int i = 0; i < poe.AssociatedPOEViews.size(); i++){
			POEView view = poe.AssociatedPOEViews.get(i);
			view.removePOEFromView(view.associatedPOEs.indexOf(poe));
		}
		
		poes.remove(poe);
	}
	
	public void OutputPEforGraphing() {
		// pick a poe from this view
		POEData poe;
		if(this.poes.size() == 1) {
			poe = this.poes.get(0);
		}else {
			String[] poeList = getAllPOEList();
			List_Dialog poe_list_dialog = new List_Dialog(mf, poeList, 1);
			poe_list_dialog.SetCaption("Choose a P(E) to output:");

			poe_list_dialog.Execute();
			if(!poe_list_dialog.ID) return; // check
			
			poe = poes.get(poe_list_dialog.GetChosenIndex()[0]);
		}
		
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("pgr file", "pgr"));
		int returnVal = fc.showSaveDialog(this.mf);
		if(returnVal != JFileChooser.APPROVE_OPTION){
			return;
		}
		File f = fc.getSelectedFile();
		if (!f.getName().endsWith(".pgr")) {
		    f = new File(f.getAbsolutePath() + ".pgr");  
		}

		String output = "";
		
		output += "Trans. Energy (kcal/mol),     P(E)\n";
		for(int i = 0; i < poe.num_points; i++) {
			output += poe.energy_values[i] + ",     " + poe.poe_amplitudes[i] + "\n";
		}
		
		try {
			f.createNewFile();
			FileWriter fw;
			fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(output);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
	public void ChangeCalculationParameters(){
		String[] calcList = getAllCalcList();
		List_Dialog calc_list_dialog = new List_Dialog(mf, calcList, 1);
		calc_list_dialog.SetCaption("Choose a calculation to edit:");

		calc_list_dialog.Execute();
		if (!calc_list_dialog.ID) return;
		CalcData calc = calcs.get(calc_list_dialog.GetChosenIndex()[0]);
		
		CalculateMainDialog calcDialog = new CalculateMainDialog(mf, this);
		calcDialog.DetachOldTOFs(true);
		calcDialog.SetDialogData(calc);
		calcDialog.SetAreInstParamsSet(InstParamsSet);
		calcDialog.Execute();
		if(!calcDialog.ID) return;
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
	
	
	
	
	
	
	
	
	
	/* BEGIN WINDOW METHODS */
	
	public void TileVertically() {
		// How many frames do we have?
		ArrayList<JInternalFrame> allframes = mf.internalFrames;
		int count = allframes.size();
		if (count == 0)
			return;

		// Determine the necessary grid size
		int sqrt = (int) Math.sqrt(count);
		int rows = sqrt;
		int cols = sqrt;
		if (rows * cols < count) {
			rows++;
			if (rows * cols < count) {
				cols++;
			}
		}

		// Define some initial values for size & location.
		Dimension size = mf.pane.getRootPane().getSize();

		int w = size.width / cols;
		int h = (size.height-20) / rows;
		int x = 0;
		int y = 0;

		// Iterate over the frames, deiconifying any iconified frames and then
		// relocating & resizing each.
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols && ((i * cols) + j < count); j++) {
				JInternalFrame f = allframes.get((i * cols) + j);

				f.setLocation(x, y);
				f.setSize(w+f.getInsets().left+f.getInsets().right, h+f.getInsets().bottom);
				x += w;
			}
			y += h; // start the next row
			x = 0;
		}

	}
	
	public void TileHorizontally() {
		// How many frames do we have?
		ArrayList<JInternalFrame> allframes = mf.internalFrames;
		int count = allframes.size();
		if (count == 0)
			return;

		// Determine the necessary grid size
		int sqrt = (int) Math.sqrt(count);
		int rows = sqrt;
		int cols = sqrt;
		if (rows * cols < count) {
			cols++;
			if (rows * cols < count) {
				rows++;
			}
		}

		// Define some initial values for size & location.
		Dimension size = mf.pane.getRootPane().getSize();

		int w = size.width / cols;
		int h = (size.height-20) / rows;
		int x = 0;
		int y = 0;

		// Iterate over the frames, deiconifying any iconified frames and then
		// relocating & resizing each.
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols && ((i * cols) + j < count); j++) {
				JInternalFrame f = allframes.get((i * cols) + j);

				f.setLocation(x, y);
				f.setSize(w+f.getInsets().left+f.getInsets().right, h+f.getInsets().bottom);
				x += w;
			}
			y += h; // start the next row
			x = 0;
		}
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
	
	protected String[] getRealTOFList(){
		int numReal = 0;
		for(int i=0; i < tofs.size(); i++){
			if(tofs.get(i).is_real_TOF) numReal++;
		}
		
		String[] list = new String[numReal];
		int ind = 0;
		for(int i=0; i < tofs.size(); i++){
			if(tofs.get(i).is_real_TOF){
				list[ind++] = tofs.get(i).title;
			}
		}
		return list;
	}
	
	protected String[] getAllPOEList(){
		String[] list = new String[poes.size()];
		for(int i=0; i < poes.size(); i++){
			list[i] = poes.get(i).title;
		}
		return list;
	}
	
	protected String[] getAllCalcList(){
		String[] list = new String[calcs.size()];
		for(int i=0; i < calcs.size(); i++){
			list[i] = calcs.get(i).title;
		}
		return list;
	}
	
	protected boolean CompareTOFs(TOFData tof_1, TOFData tof_2){
		String temp_value_1;
		String temp_value_2;
		int tof1_num_points, tof2_num_points, dwell_1, dwell_2, offset_1, offset_2;
		float ion_m_e_1, ion_m_e_2;
		boolean same = true;

		tof1_num_points = tof_1.num_tot_channels;
		tof2_num_points = tof_2.num_tot_channels;
		dwell_1 = (int) tof_1.dwell;
		dwell_2 = (int) tof_2.dwell;
		offset_1 = (int) tof_1.offset;
		offset_2 = (int) tof_2.offset;
		ion_m_e_1 = tof_1.ion_m_e;
		ion_m_e_2 = tof_2.ion_m_e;

		// Need to compare TOFs to be sure they can be added
		if((tof_1.dwell_scale) != (tof_2.dwell_scale))
		{
			same = false;
		}
		if((tof_1.offset_scale) != (tof_2.offset_scale))
		{
			same = false;
		}
		if(tof1_num_points != tof2_num_points)
		{
			same = false;
		}

		temp_value_1 = "" + dwell_1;
		temp_value_2 = "" + dwell_2;
		if(!temp_value_1.equals(temp_value_2))
		{
			same = false;
		}

		temp_value_1 = "" + offset_1;
		temp_value_2 = "" + offset_2;
		if(!temp_value_1.equals( temp_value_2) )
		{
			same = false;
		}

		temp_value_1 = "" +  ion_m_e_1;
		temp_value_2 = "" + ion_m_e_2;
		if(!temp_value_1.equals(temp_value_2))
		{
			same = false;
		}
		return same;
	}
	
	protected TOFData TOFMath(TOFData tof_1, TOFData tof_2, boolean should_add){
		TOFData new_tof = new TOFData(this);
		int tof1_num_points, i;
		float[] new_tof_time_array, new_tof_amp_array, time_array;
		float[] tof1_amp_array, tof2_amp_array;

		tof1_num_points = tof_1.num_tot_channels;
		new_tof_time_array = new float[tof1_num_points];
		new_tof_amp_array = new float[tof1_num_points];
		time_array = tof_1.tof_flight_time;
		tof1_amp_array = tof_1.channel_counts;
		tof2_amp_array = tof_2.channel_counts;
		if(should_add == true)
		{
			for(i = 0; i < tof1_num_points; i++)
			{
				new_tof_time_array[i] = time_array[i];
				new_tof_amp_array[i] = tof1_amp_array[i] + tof2_amp_array[i];
			}
			new_tof.title = "TOF sum";
		}
		else
		{
			for(i = 0; i < tof1_num_points; i++)
			{
				new_tof_time_array[i] = time_array[i];
				new_tof_amp_array[i] = tof1_amp_array[i] - tof2_amp_array[i];
			}
			new_tof.title = "TOF difference";
		}
		new_tof.dwell = tof_1.dwell;
		new_tof.offset = tof_1.offset;
		new_tof.num_tot_channels = tof1_num_points;
		new_tof.channel_counts = new_tof_amp_array;
		new_tof.tof_flight_time = new_tof_time_array;

		new_tof.lab_angle = tof_1.lab_angle;
		new_tof.dwell_scale = tof_1.dwell_scale;  
		new_tof.offset_scale = tof_1.offset_scale;
		new_tof.ion_m_e = tof_1.ion_m_e;
		new_tof.polarization_angle = tof_1.polarization_angle;
		new_tof.depolarization = tof_1.depolarization;
		new_tof.polarized_laser = tof_1.polarized_laser;
		new_tof.is_real_TOF = true;
		return new_tof;
	}
}
