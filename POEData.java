package sbeam2;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import sbeam2.gui.POEView;

public class POEData {
	public int num_points;
	public float min_energy, max_energy;
	public int is_Visible; //CHANGE: -2 = not visible, -1 = visible, 0+ = appended to viewnumber of value
	public int POE_num;

	public String title;
	public float[] poe_amplitudes, energy_values;
	public float energy_spacing, average_energy;
	public float[] extrema = new float[2];

	public ArrayList<POEView> AssociatedPOEViews;
	public ArrayList<CalcData> AssociatedCalcs;
	public Color poe_color;

	
	public POEData() {
		// TODO Auto-generated constructor stub
		num_points = 100;
		min_energy = 0;
		max_energy = 200;

		average_energy = 0.0f;

		AssociatedPOEViews = new ArrayList<POEView>();
		AssociatedCalcs = new ArrayList<CalcData>();
		title = null;
		poe_color = new Color(0,0,0);
		poe_amplitudes = null;
		energy_values = null;
		energy_spacing = 0;

		is_Visible = -2;	//CHANGE: default value
	}
	
	
	public void SaveAsPOEFile(BufferedWriter os) throws IOException{
		int i;

		os.write(title + "\n\n");
		os.write(num_points + "\n");
		os.write(min_energy + "     " + max_energy + "\n\n");

		// Output the three components of the energy distributions color
		os.write((int) poe_color.getRed() + "     ");
		os.write((int) poe_color.getGreen() + "     ");
		os.write((int) poe_color.getBlue() + "\n\n");

		for (i = 0; i < num_points; i++) {
			os.write(poe_amplitudes[i] + "  \t");
			if ((((i + 1) % 5) == 0) || (i == num_points - 1))
				os.write("\n");
		}
		//return os;
	}
	
	public void LoadFromFile(File f){
		try{
			Scanner is = new Scanner(f);
			int i;
			int red, green, blue;
			String title_temp;
	
			title_temp = is.nextLine();
			is.nextLine();
			title = title_temp;
	
			num_points = is.nextInt();
			is.nextLine();
			min_energy = is.nextFloat();
			max_energy = is.nextFloat();
			is.nextLine();
			is.nextLine();
	
			red = is.nextInt();
			green = is.nextInt();
			blue = is.nextInt();
			is.nextLine();
			is.nextLine();
	
			poe_amplitudes = new float[num_points];
			energy_values = new float[num_points];
	
			energy_spacing = (max_energy - min_energy) / (num_points - 1);
	
			for (i = 0; i < num_points; i++) {
				poe_amplitudes[i] = is.nextFloat();
				if ((((i + 1) % 5) == 0) || (i == num_points - 1)) {
					is.nextLine();
				}
				energy_values[i] = min_energy + i * energy_spacing;
			}
	
			poe_color = new Color(red, green, blue);
			
			is.close();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	public float maxValue(){
		float maxVal = 0;
		for(int i = 0; i < num_points; i++){
			if(poe_amplitudes[i] > maxVal){
				maxVal = poe_amplitudes[i];
			}
		}
		return maxVal;
	}
	
	public void updatePOE(float x, float newY){
		int index = (int) ((x - energy_values[0]) / energy_spacing); //find which point has changed 		
 		poe_amplitudes[index] = newY; //change poe data
 		
	}
	
	public void calcTOFDelta(float x){
		int index = (int) ((x - energy_values[0]) / energy_spacing); //find which point has changed 		
		//now update dependent TOFS
 		for(int calcInd = 0; calcInd < AssociatedCalcs.size(); calcInd++){
 			CalcData calc = AssociatedCalcs.get(calcInd);
 			int numTOFs = calc.num_tofs;
 			
 			
 			TOFData[] tofsForCalc = calc.calculated_tofs;
 			//calc.calculated_tofs = tofsForCalc; //maybe change this?
 			calc.CalcTOFDeltaFunctions(this.POE_num, this, index);
 		}
	}
	
	public void FindNewTOFs(float new_amplitude, boolean is_endpoint){
		for(int i = 0; i < AssociatedCalcs.size(); i++)
		{
			CalcData calc = AssociatedCalcs.get(i);
			for(int j = 0; j < calc.calculated_tofs.length; j++){
				calc.calculated_tofs[j].AddOnDeltaTOF(new_amplitude, is_endpoint);
			}
		}
	}
	
	public String out(boolean compat){
		int i;
		String returner = "";

		returner += this.title + "\n\n";
		returner += this.POE_num + "\n";
		returner += this.num_points + "     "+ this.min_energy + "     " + this.max_energy + "     ";
		if(!compat) returner += this.is_Visible; //CHANGE: visibilty in save file
		returner += "\n\n";

		// Output the three components of the energy distributions color
		returner += (int) this.poe_color.getRed() + "     ";
		returner += (int) this.poe_color.getGreen() + "     ";
		returner += (int) this.poe_color.getBlue() + "\n\n";


		for(i = 0; i < this.num_points; i++)  {
			returner += this.poe_amplitudes[i] + "  \t";
			if((((i + 1) % 20) == 0) || (i == this.num_points - 1))
				returner += "\n";
		}
		
		return returner;
	}
	public static POEData in(Scanner is) {
		int i, number_of_points;
		float energy_increment;
		int red, green, blue;
		String title_temp;
		float[] amplitude;
		float[] energy;
		float minimum_energy, maximum_energy;
		POEData poe = new POEData();

		poe.title = is.nextLine();
		is.nextLine();

		poe.POE_num = is.nextInt();
		is.nextLine();

		number_of_points = is.nextInt();
		minimum_energy = is.nextFloat();
		maximum_energy = is.nextFloat();
		// CHANGE:open feature
		String temp = is.nextLine();
		if (!temp.equals("")) {
			poe.is_Visible = Integer.parseInt(temp.trim());
		} else {
			poe.is_Visible = -2;
		}
		// END CHANGE
		is.nextLine();

		poe.num_points = number_of_points;
		poe.min_energy = minimum_energy;
		poe.max_energy = maximum_energy;

		red = is.nextInt();
		green = is.nextInt();
		blue = is.nextInt();
		is.nextLine();
		is.nextLine();

		amplitude = new float[number_of_points];
		energy = new float[number_of_points];

		energy_increment = (maximum_energy - minimum_energy)
				/ (number_of_points - 1);

		for (i = 0; i < number_of_points; i++) {
			amplitude[i] = is.nextFloat();
			energy[i] = minimum_energy + i * energy_increment;
		}

		Color color = new Color(red, green, blue);
		poe.poe_color = color;
		poe.poe_amplitudes = amplitude;
		poe.energy_values = energy;
		poe.energy_spacing = energy_increment;
		is.nextLine(); // Gets last '/n' in POE

		return poe;
	}
}
