package sbeam2;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import sbeam2.gui.TOF_Input_1_Dialog;
import sbeam2.scale;
import sbeam2.gui.TOFView;


public class TOFData {
	
	// Information common to both real and calculated TOFs
	public String title; // Title of TOF
	public float dwell;   // Dwell time (channel width) of multichannel scalar
	public float offset; // Offset time (time delay between trigger and laset shot)
	public scale dwell_scale, offset_scale;  // Time scale of dwell time and offest (ps, ns, us, ms, etc)
	public int num_tot_channels;  // Total # of MCS channels contained in TOF
	public float ion_m_e, lab_angle;
	public float[] actual_flight_time_micro; // Real flight time (as determined from offset & ion flight time) for flight from interaction region toionization.  Time in microseconds.
	public float[] extrema;
	public boolean is_real_TOF;
	public Color time_of_flight_color;
	public int is_Visible; //CHANGE: -2 = not visible, -1 = visible, 0+ = appended to viewnumber of value

	
	
	// Information specific to real TOFs
	public float primary_mass1, primary_mass2;  // Masses of primary fragments for inversion
	public float beta_for_inversion, peak_beam_velocity;
	public boolean polarized_laser;
	public float polarization_angle, ion_flight_time;
	public float[] channel_counts, tof_flight_time;
	public int format;

	
	
	// Data particular to calculated TOFs
	public float energy_increment; // Used when adjusting TOFs after P(E)
	public int num_current_poes, number_included_poes;
	public int[] number_channels;
	public float[][][] individual_tofs;
	public String[] detach_poe_titles;
	public int detach_num_poes;
	public boolean is_being_changed;
	public float[][] delta_tofs;
	public int delta_tof_poe_number;
	public float delta_tof_poe_amplitude;
	public int associated_calc_number, TOF_num; // TOF_num used to track TOF when TOFs are added & deleted
	public Color[] tof_colors; // For several TOFs calculated from different P(E)'s

	
	
	// Information stored for calculated TOF
	//public POE_calc_data[] calc_data_for_poes;
	public int num_beam_angle_segs, num_det_angle_segs, num_lab_vel_segs;
	public int num_ionization_segs, num_beam_vel_segs;
	public String calculation_title;
	public boolean is_number_density_calc, is_ionization_gaussian;
	public float depolarization, minimum_lab_vel, maximum_lab_vel;
	public float flight_length, beam_angle_width, detector_angle_width, ionizer_length;
	public float beam_vel_alpha, beam_vel_speed_ratio;
	public float[] unaltered_TOF_data;
	public boolean TOF_has_been_altered;
	public int num_points_unaltered_tof;
	public float unaltered_dwell;
	public ArrayList<TOFView> AssociatedTOFViews;
	
	
	
	public TOFData() {
		// TODO Auto-generated constructor stub
		is_real_TOF = true;
		is_being_changed = false;
		delta_tofs = null;
		delta_tof_poe_number = 0;
		delta_tof_poe_amplitude = 0;
		num_current_poes = 0;
		number_channels = null;
		individual_tofs = null;

		//calc_data_for_poes = null;
		detach_poe_titles = null;

		tof_colors = null;
		energy_increment = 0;
		calculation_title = null;
		title = null;
		dwell = 1.0f;
		is_Visible = -2;	//CHANGE: default value
		extrema = new float[2];

		TOF_has_been_altered = false;
		unaltered_TOF_data = null;
		num_points_unaltered_tof = -1;
		unaltered_dwell = 0;

		offset = 0.0f; 

		num_tot_channels = 0;
		channel_counts = null;
		tof_flight_time = null;
		format = 1;
		dwell_scale = scale.ns;
		offset_scale = scale.ns;
		ion_m_e = 1.0f;
		lab_angle = 10;
		polarization_angle = 90;
		depolarization = 1.0f;

		primary_mass1 = 1.0f;
		primary_mass2 = 1.0f;
		peak_beam_velocity = 0.0f;
		beta_for_inversion = 0.0f;

		// Initialize boolean variables
		polarized_laser = false;

		actual_flight_time_micro = null;

		AssociatedTOFViews = new ArrayList<TOFView>();
		//AssociatedCalcs = 0;
		time_of_flight_color = new Color(0,0,0);
	}
	
	public void loadFromFile(File f){
		try{
			Scanner is = new Scanner(f);
			int i = 0, new_array_counter = 1;
			float dwell_time, input_value1, input_value2;
			float[] tof_pointer = new float[1000];
			float[] time_pointer = new float[1000];
			float[] reserve_pointer1, reserve_pointer2, placeholder1, placeholder2;
			boolean first = true;

			if (format != 1) {
				while (is.hasNext()) {
					input_value1 = is.nextFloat();
					if (first) {
						i = 0;
						first = false;
					} else
						i++;

					if (i >= (new_array_counter * 1000)) {
						new_array_counter++;
						reserve_pointer1 = new float[new_array_counter * 1000];
						reserve_pointer2 = new float[new_array_counter * 1000];
						for (int j = 0; j < i; j++) {
							reserve_pointer1[j] = tof_pointer[j];
							reserve_pointer2[j] = time_pointer[j];
						}
						placeholder1 = tof_pointer;
						tof_pointer = reserve_pointer1;

						placeholder2 = time_pointer;
						time_pointer = reserve_pointer2;
					}
					tof_pointer[i] = input_value1;
					time_pointer[i] = 0;
				}
			} else {
				first = true;
				for (i = 0; i < 5; i++) {
					is.nextLine();
				}
				while (is.hasNext()) {
					input_value1 = is.nextFloat();
					if (first) {
						i = 0;
						first = false;
					} else
						i++;

					if (i >= (new_array_counter * 1000)) {
						new_array_counter++;
						reserve_pointer1 = new float[new_array_counter * 1000];
						reserve_pointer2 = new float[new_array_counter * 1000];
						for (int j = 0; j < i; j++) {
							reserve_pointer1[j] = tof_pointer[j];
							reserve_pointer2[j] = time_pointer[j];
						}
						placeholder1 = tof_pointer;
						tof_pointer = reserve_pointer1;

						placeholder2 = time_pointer;
						time_pointer = reserve_pointer2;
					}
					tof_pointer[i] = input_value1;
					input_value2 = is.nextFloat();
					is.nextLine();
					time_pointer[i] = input_value2;
				}
				dwell_time = time_pointer[1] - time_pointer[0]; // Determine
																// experimental
																// dwell time
				dwell = dwell_time;
				dwell_scale = scale.μs;
			}
			num_tot_channels = i+1;
			channel_counts = tof_pointer;
			tof_flight_time = time_pointer;
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

	}
	
	public void loadFromInputDialog(TOF_Input_1_Dialog tof_input_dialog){
		scale temporary_scale;


		offset = Float.parseFloat(tof_input_dialog.GetTriggerOffset());
		// Add new data to TOF file
		title = tof_input_dialog.GetTOFTitle();


		ion_m_e = Float.parseFloat(tof_input_dialog.GetDetectedme());
		lab_angle = Float.parseFloat(tof_input_dialog.GetLabAngle());
		if(tof_input_dialog.IsLaserPolarized())
		{
			polarized_laser = true;
			polarization_angle = Float.parseFloat(tof_input_dialog.GetPolarizationAngle());
			depolarization = Float.parseFloat(tof_input_dialog.GetDegreePolarization());
		}
		else
		{
			polarized_laser = false;
		}

		if(tof_input_dialog.dwell_time_edit.isEnabled()){
			temporary_scale = tof_input_dialog.GetDwellScale();
			dwell_scale = temporary_scale;
			dwell = Float.parseFloat(tof_input_dialog.GetDwellTime());
		}
		
		temporary_scale = tof_input_dialog.GetOffsetScale();
		offset_scale = temporary_scale;
		//time_of_flight.SetIonFlightTime(document.GetIonFlightConst());	
	}
	
	public void SetRealFlightTime(){
		int i, scaling_factor;
		float offset_in_microseconds;
		float dwell_in_microseconds;
		float starting_time_in_microseconds;

		actual_flight_time_micro = new float[num_tot_channels];

		scaling_factor = (int) dwell_scale.value() + 6;  // To convert to microseconds
		dwell_in_microseconds = (float) (dwell * Math.pow(10, scaling_factor));
		if(is_real_TOF)
		{
			starting_time_in_microseconds = (float) (tof_flight_time[0] * Math.pow(10, scaling_factor));
		}
		else
		{
			starting_time_in_microseconds = 0.0f;
		}

		scaling_factor = (int) offset_scale.value() + 6; // To convert to microseconds
		offset_in_microseconds = (float) (offset * Math.pow(10, scaling_factor));

		for(i = 0; i < num_tot_channels; i++)
		{
			actual_flight_time_micro[i] = ((float)i * dwell_in_microseconds) - ion_flight_time
					+ offset_in_microseconds + starting_time_in_microseconds;
		}
	}
	
	
	public float[] GetMaxMinCounts(float starting_time, float ending_time){
		int i, j, k;
		float current_value, max_counts=0, min_counts =0;

		int num_channel_tofs;


		float[][] this_poe_tofs;
		float[] this_channel_tof;
		float this_time_point;

		if((starting_time == 0) && (ending_time == 0))
		{
			starting_time = actual_flight_time_micro[0];
			ending_time = actual_flight_time_micro[num_tot_channels - 1];
		}

		boolean first = true;
		max_counts = 0;
		for(i = 0; i < num_tot_channels; i++)
		{
			this_time_point = actual_flight_time_micro[i];
			if((this_time_point >= starting_time) && (this_time_point <= ending_time))
			{
				current_value = channel_counts[i];
				if(first)
				{
					min_counts = current_value;
					first = false;
				}
				min_counts = Math.min(min_counts, current_value);
				max_counts = Math.max(max_counts, current_value);
			}
		}

		if((starting_time != 0) || (ending_time != 0))
		{
			if((is_real_TOF == false) && (min_counts > 0)) // See if any of the included TOFs have smaller mins
			{
				for(i = 0; i < num_tot_channels; i++)
				{
					this_time_point = actual_flight_time_micro[i];
					if((this_time_point >= starting_time) && (this_time_point <= ending_time))
					{
						for(j = 0; j < num_current_poes; j++)
						{
							this_poe_tofs = individual_tofs[j];
							if(this_poe_tofs != null)
							{
								num_channel_tofs = number_channels[j];
								if(num_channel_tofs == 1)      // Look only at the overall P(E) TOF if only one channel
								{
									this_channel_tof = this_poe_tofs[0];
									current_value = this_channel_tof[i];
									min_counts = Math.min(min_counts, current_value);
								}
								else
								{
									for(k = 1; k <= num_channel_tofs; k++)
									{
										this_channel_tof = this_poe_tofs[k];
										current_value = this_channel_tof[i];
										min_counts = Math.min(min_counts, current_value);
									}
								}
							}
						}
					}
				}
			}
		}
		extrema[0] = max_counts;
		extrema[1] = min_counts;
		return(extrema);
	}
	
	public float GetAverageCounts(float time1, float time2){
		int i;
		float lower_time, upper_time;
		float array_starting_time;
		float dwell_in_microsecs;

		float sum;
		float average;

		int lower_point, upper_point;

		lower_time = Math.min(time1, time2);
		upper_time = Math.max(time1, time2);

		array_starting_time = actual_flight_time_micro[0];
		dwell_in_microsecs = actual_flight_time_micro[1] - array_starting_time;

		lower_point = (int)((lower_time - array_starting_time) / dwell_in_microsecs);
		upper_point = (int)((upper_time - array_starting_time) / dwell_in_microsecs);



		if(lower_point < 0)
		{
			lower_point = 0;
		}

		if(upper_point > (num_tot_channels - 1))
		{
			upper_point = num_tot_channels - 1;
		}

		sum = 0;
		for(i = lower_point; i <= upper_point; i++)
		{
			sum += channel_counts[i];
		}
		average = sum / (upper_point - lower_point + 1);


		return average;
	}

}
