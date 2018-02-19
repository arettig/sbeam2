package sbeam2;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import sbeam2.gui.TOF_Input_1_Dialog;
import sbeam2.scale;
import sbeam2.gui.TOFView;
import sbeam2.gui.TOF_Edit_Dialog_2;


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
	public POECalcData[] calc_data_for_poes;
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

		calc_data_for_poes = null;
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
				dwell_scale = scale.ns;
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
	
	public void loadFromInputDialog(TOF_Edit_Dialog_2 tid){
		title = tid.tof_title;
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
	
	public void SetIonFlightTime(float flight_constant)
	{
		if(is_real_TOF)
		{
			ion_flight_time = (float) (flight_constant * Math.sqrt(ion_m_e));
		}
		else
		{
			ion_flight_time = 0.0f;
		}
		SetRealFlightTime();
	}
	
	public void SetDeltaTOFArrays(float[][] delta_tofs_array, int poe_num, float num_amplitude, float energy_inc)
	{
		is_being_changed = true;
		delta_tofs = delta_tofs_array;
		delta_tof_poe_number = poe_num;
		delta_tof_poe_amplitude = num_amplitude;
		if(num_amplitude < 0)
			delta_tof_poe_amplitude = 1.0f;
		energy_increment = energy_inc;
	}
	
	public void AddOnDeltaTOF(float poe_new_amplitude, boolean is_end_point){
		int i, j, this_number_channels;

		float amplitude_change, divisor;
		float[][] this_poe_tofs = individual_tofs[delta_tof_poe_number];

		float[] this_chan_tofs, this_chan_delta_tofs;
		float add_on;


		if(poe_new_amplitude < 0)   // This will only occur if the TOF is zero everywhere
		{
			divisor = 1.0f;
			if(is_end_point)
			{
				// Factor of 2 is needed due to trapezoid rule type integration
				amplitude_change = (float) (2.0 / energy_increment);
			}
			else
			{
				amplitude_change = (float) (1.0 / energy_increment);
			}
		}
		else
		{
			amplitude_change = (poe_new_amplitude - delta_tof_poe_amplitude);
			if(is_end_point)
			{
				if(amplitude_change > -(2.0 / energy_increment))   // i.e. if the P(E) has not just been set identically to zero
				{
					// Factor of 2 is needed due to trapezoid rule type integration
					divisor = (float) (1.0 / (1.0 + (0.5 * amplitude_change * energy_increment)));
				}
				else
				{
					divisor = 0; // So overall TOF will equal zero
				}
			}
			else
			{
				if(amplitude_change > -(1.0 / energy_increment))   // i.e. if the P(E) has not just been set identically to zero
				{
					divisor = (float) (1.0 / (1.0 + (amplitude_change * energy_increment)));
				}
				else
				{
					divisor = 0; // So overall TOF will equal zero
				}
			}
		}
		this_number_channels = number_channels[delta_tof_poe_number];
		if(this_number_channels == 1)
		{
			this_number_channels = 0;
		}
		for(i = 0; i <= this_number_channels; i++)
		{
			this_chan_tofs = this_poe_tofs[i];
			this_chan_delta_tofs = delta_tofs[i];
			for(j = 0; j <  num_tot_channels; j++)
			{
				add_on = this_chan_delta_tofs[j] * amplitude_change;
				if(i == 0)
					channel_counts[j] -= this_chan_tofs[j];  // Subtract off old P(E) TOF from overall TOF
				this_chan_tofs[j] += add_on;
				this_chan_tofs[j] *= divisor;
				if(i == 0)
					channel_counts[j] += this_chan_tofs[j];
			}
		}
		is_being_changed = false;
		
		
		System.out.println("Calculated changes, now need to update views");
		//change all the views
		for(int viewInd = 0; viewInd < AssociatedTOFViews.size(); viewInd++){
			TOFView view = AssociatedTOFViews.get(viewInd);
			view.reloadCalcTOF(this);
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

	
	public String out(boolean compat){
		TOFData tof = this;
		int i, j, k, num_channels;
		float[][] this_poe_indiv_tofs;
		float[] this_chan_indiv_tofs;
		Color this_poe_color;
		POECalcData[] detached_calc_data;
		String[] poe_title_array;
		String os = "";
		
		os +=  tof.title + "\n";
		os +=  tof.TOF_num + " "+ tof.lab_angle + " ";
		os += (compat)?((tof.polarized_laser)?1:0):tof.polarized_laser;
		if(tof.polarized_laser)
		{
			os += " " + tof.polarization_angle + " " + tof.depolarization + " ";
		}
		os +=  "\n" + tof.ion_m_e + " " + tof.ion_flight_time + " " + tof.offset + " " ; 
		if(!compat) os += tof.is_Visible; //CHANGE: save format
		os += "\n";
		os +=  tof.dwell + " " + tof.dwell_scale + "\n";
		os +=  tof.offset + " " + tof.offset_scale + "\n";
		os +=  tof.beta_for_inversion + " " + tof.peak_beam_velocity + " ";
		os +=  tof.primary_mass1 + " " + tof.primary_mass2 + " ";
		os +=  "\n" + tof.num_tot_channels;

		for(i = 0; i < tof.num_tot_channels; i++)
		{
			if((i % 10) == 0)
				os +=  "\n";
			os +=  tof.channel_counts[i] + " " + tof.tof_flight_time[i] + " ";
		}

		os +=  "\n" + (int) tof.time_of_flight_color.getRed() + " ";
		os +=  (int) tof.time_of_flight_color.getGreen() + " ";
		os +=  (int) tof.time_of_flight_color.getBlue() + " ";

		os += "\n" + ((compat)?((tof.TOF_has_been_altered)?1:0):tof.TOF_has_been_altered) + " ";

		if(tof.TOF_has_been_altered)
		{
			os +=  tof.num_points_unaltered_tof + " " + tof.unaltered_dwell;
			for(i = 0; i < tof.num_points_unaltered_tof; i++)
			{
				if((i % 20) == 0)
					os +=  "\n";
				os +=  tof.unaltered_TOF_data[i] + " ";
			}
		}

		os += "\n" + ((compat)?((tof.is_real_TOF)?1:0):tof.is_real_TOF) + " ";
		if(!tof.is_real_TOF)
		{
			os +=  tof.associated_calc_number + " ";
			os +=  tof.energy_increment + " " + tof.num_current_poes + " ";
			os +=  tof.number_included_poes + "\n";

			os +=  tof.num_beam_angle_segs + " " + tof.num_det_angle_segs + " ";
			os +=  tof.num_lab_vel_segs + " " + tof.num_ionization_segs + " ";
			os +=  tof.num_beam_vel_segs + "\n";

			os += ((compat)?((tof.is_number_density_calc)?1:0) + " " + ((tof.is_ionization_gaussian)?1:0):tof.is_number_density_calc + " " +tof.is_ionization_gaussian) + " ";
			os +=  tof.depolarization + " " + tof.minimum_lab_vel + " ";
			os +=  tof.maximum_lab_vel + " " + tof.flight_length + " ";
			os +=  tof.beam_angle_width + " " + tof.detector_angle_width + " ";   
			os +=  tof.ionizer_length + " " + tof.beam_vel_alpha + " ";
			os +=  tof.beam_vel_speed_ratio + "\n";

			for(i = 0; i < tof.num_current_poes; i++)
			{
				num_channels = tof.number_channels[i];
				this_poe_indiv_tofs = tof.individual_tofs[i];
				os +=  "\n" + num_channels + " ";
				if(num_channels != 0)
				{
					for(j = 0; j < (num_channels + 1); j++)
					{
						this_chan_indiv_tofs = this_poe_indiv_tofs[j];
						for(k = 0; k < tof.num_tot_channels; k++)
						{
							if((k % 20) == 0)
								os +=  "\n";
							os +=  this_chan_indiv_tofs[k] + " ";
						}
					}
				}
			}

			if(tof.associated_calc_number == -1) //i.e. calc is detached
			{
				// Output POE_calc_data information for detached TOF
				// (this is not necessary if the calculation is still attached,
				// since that POE_calc_data is stored by the calculation and
				// can therefore be passed to the TOF by pointer once it is reloaded
				// to the calculation.)
				os +=  "\n" + tof.calculation_title + "\n";
				os +=  tof.detach_num_poes + " ";
				detached_calc_data = tof.calc_data_for_poes;
				poe_title_array = tof.detach_poe_titles;
				for(i = 0; i < tof.detach_num_poes; i++)
				{
					// Output POE title first
					os +=  "\n" + poe_title_array[i] + "\n";
					// Output POE calc data member
					os +=  detached_calc_data[i].is_included + " ";
					if(detached_calc_data[i].is_included)
					{
						this_poe_color = tof.tof_colors[i];

						os +=  "\n" + detached_calc_data[i].beta_param + " ";
						os +=  detached_calc_data[i].num_channels + " ";
						for(j = 0; j < detached_calc_data[i].num_channels; j++)
						{
							os +=  "\n" + detached_calc_data[i].mass_1[j] + " ";
							os +=  detached_calc_data[i].mass_2[j] + " ";
							os +=  detached_calc_data[i].rel_weight[j] + " ";
							os +=  detached_calc_data[i].mass_ratio[j] + " ";
						}

						os +=  "\n" + (int) this_poe_color.getRed() + " ";
						os +=  (int) this_poe_color.getGreen() + " ";
						os +=  (int) this_poe_color.getBlue() + " ";
					}
				}
			}
		}
		return os;
	}
	
	public static TOFData in(Scanner is) {
		int i, j, k, number_of_channels;
		int red, green, blue;
		String title_temp;
		float[] tof_chan_counts, loaded_flight_time;
		int[] num_chan_array;
		float[][][] individ_tofs;
		float[][] this_poe_indiv_tofs;
		float[] this_chan_indiv_tofs;
		String[] poe_titles;
		POECalcData[] calc_data_array;
		Color[] temp_color_array;
		TOFData tof = new TOFData();

		tof.title = is.nextLine();
		System.out.println("Loading in tof: " + tof.title);
		tof.TOF_num = is.nextInt();
		tof.lab_angle = is.nextFloat();
		if(is.hasNextBoolean()){
			tof.polarized_laser = is.nextBoolean();
		}else{
			tof.polarized_laser = (is.nextInt() == 1) ? true:false;
		}

		if (tof.polarized_laser) {
			tof.polarization_angle = is.nextFloat();
			tof.depolarization = is.nextFloat();
		}
		is.nextLine();

		tof.ion_m_e = is.nextFloat();
		tof.ion_flight_time = is.nextFloat();
		tof.offset = is.nextFloat();
		// CHANGE:add visibilty to open feature
		String temp = is.nextLine();
		if (!temp.equals("")) {
			tof.is_Visible = Integer.parseInt(temp.trim());
		} else {
			tof.is_Visible = -2;
		}
		// END CHANGE
		tof.dwell = is.nextFloat();	
		if(is.hasNextInt()){
			tof.dwell_scale = scale.valueOf(is.nextInt());
		}else{
			tof.dwell_scale = scale.valueOf(is.next());
		}
		is.nextLine();
		tof.offset = is.nextFloat();
		if(is.hasNextInt()){
			tof.offset_scale = scale.valueOf(is.nextInt());
		}else{
			tof.offset_scale = scale.valueOf(is.next());
		}
		is.nextLine();
		tof.beta_for_inversion = is.nextFloat();
		tof.peak_beam_velocity = is.nextFloat();
		tof.primary_mass1 = is.nextFloat();
		tof.primary_mass2 = is.nextFloat();
		is.nextLine();
		number_of_channels = is.nextInt();
		is.nextLine();

		tof.num_tot_channels = number_of_channels;
		tof_chan_counts = new float[number_of_channels];
		loaded_flight_time = new float[number_of_channels];

		for (i = 0; i < number_of_channels; i++) {
			tof_chan_counts[i] = is.nextFloat();
			loaded_flight_time[i] = is.nextFloat();
		}
		is.nextLine();

		tof.channel_counts = tof_chan_counts;
		tof.tof_flight_time = loaded_flight_time;

		red = is.nextInt();
		green = is.nextInt();
		blue = is.nextInt();
		is.nextLine();
		Color color = new Color(red, green, blue);
		tof.time_of_flight_color = color;

		tof.TOF_has_been_altered = (is.hasNextBoolean()) ? is.nextBoolean(): (is.nextInt() == 1) ? true:false;
		is.nextLine();

		if (tof.TOF_has_been_altered) {
			tof.num_points_unaltered_tof = is.nextInt();
			tof.unaltered_dwell = is.nextFloat();
			is.nextLine();
			tof.unaltered_TOF_data = new float[tof.num_points_unaltered_tof];
			for (i = 0; i < tof.num_points_unaltered_tof; i++) {
				tof.unaltered_TOF_data[i] = is.nextFloat();
				is.nextLine();
			}
		}

		tof.is_real_TOF = (is.hasNextBoolean()) ? is.nextBoolean(): (is.nextInt() == 1) ? true:false;;
		if (!tof.is_real_TOF) {
			int tempInt = is.nextInt();
			tof.associated_calc_number = tempInt;//(tempInt == -1)? -1: 1000000;
			tof.energy_increment = is.nextFloat();
			tof.num_current_poes = is.nextInt();
			tof.number_included_poes = is.nextInt();
			is.nextLine();

			tof.num_beam_angle_segs = is.nextInt();
			tof.num_det_angle_segs = is.nextInt();
			tof.num_lab_vel_segs = is.nextInt();
			tof.num_ionization_segs = is.nextInt();
			tof.num_beam_vel_segs = is.nextInt();
			is.nextLine();

			tof.is_number_density_calc = (is.hasNextBoolean()) ? is.nextBoolean(): (is.nextInt() == 1) ? true:false;;
			tof.is_ionization_gaussian = (is.hasNextBoolean()) ? is.nextBoolean(): (is.nextInt() == 1) ? true:false;;
			tof.depolarization = is.nextFloat();
			tof.minimum_lab_vel = is.nextFloat();
			tof.maximum_lab_vel = is.nextFloat();
			tof.flight_length = is.nextFloat();
			tof.beam_angle_width = is.nextFloat();
			tof.detector_angle_width = is.nextFloat();
			tof.ionizer_length = is.nextFloat();
			tof.beam_vel_alpha = is.nextFloat();
			tof.beam_vel_speed_ratio = is.nextFloat();
			is.nextLine();

			num_chan_array = new int[tof.num_current_poes];
			individ_tofs = new float[tof.num_current_poes][][];
			for (i = 0; i < tof.num_current_poes; i++) {
				num_chan_array[i] = is.nextInt();
				number_of_channels = num_chan_array[i];
				if (number_of_channels != 0) {
					individ_tofs[i] = new float[number_of_channels + 1][];
					this_poe_indiv_tofs = individ_tofs[i];

					for (j = 0; j < (number_of_channels + 1); j++) {
						this_poe_indiv_tofs[j] = new float[tof.num_tot_channels];
						this_chan_indiv_tofs = this_poe_indiv_tofs[j];
						for (k = 0; k < tof.num_tot_channels; k++) {
							this_chan_indiv_tofs[k] = is.nextFloat();
						}
					}
				} else {
					individ_tofs[i] = null;
				}
			}
			tof.individual_tofs = individ_tofs;
			tof.number_channels = num_chan_array;

			if (tof.associated_calc_number == -1) // i.e. calc is detached
			{
				// Output POE_calc_data information for detached TOF
				// (this is not necessary if the calculation is still attached,
				// since that POE_calc_data is stored by the calculation and
				// can therefore be passed to the TOF by pointer once it is
				// reloaded
				// to the calculation.)
				is.nextLine();
				tof.calculation_title = is.nextLine();
				System.out.println("Loading in detached tof: " + tof.calculation_title);

				tof.detach_num_poes = is.nextInt();
				poe_titles = new String[tof.detach_num_poes];

				calc_data_array = new POECalcData[tof.detach_num_poes];
				temp_color_array = new Color[tof.detach_num_poes];

				for (i = 0; i < tof.detach_num_poes; i++) {
					// Get POE Title
					is.nextLine();
					title_temp = is.nextLine();

					poe_titles[i] = title_temp;
					//System.out.println("title: " +title_temp);
					calc_data_array[i] = new POECalcData();
					calc_data_array[i].is_included = (is.hasNextBoolean()) ? is.nextBoolean(): ((is.nextInt() == 1) ? true:false);
					if (calc_data_array[i].is_included) {
						calc_data_array[i].beta_param = is.nextFloat();
						calc_data_array[i].num_channels = is.nextInt();
						number_of_channels = calc_data_array[i].num_channels;
						calc_data_array[i].mass_1 = new float[number_of_channels];
						calc_data_array[i].mass_2 = new float[number_of_channels];
						calc_data_array[i].rel_weight = new float[number_of_channels];
						calc_data_array[i].mass_ratio = new float[number_of_channels];

						for (j = 0; j < number_of_channels; j++) {
							is.nextLine();
							calc_data_array[i].mass_1[j] = is.nextFloat();
							calc_data_array[i].mass_2[j] = is.nextFloat();
							calc_data_array[i].rel_weight[j] = is.nextFloat();
							calc_data_array[i].mass_ratio[j] = is.nextFloat();
						}
						is.nextLine();
						red = is.nextInt();
						green = is.nextInt();
						blue = is.nextInt();
						temp_color_array[i] = new Color(red, green, blue);
						
						//is.nextLine();
					} else {
						calc_data_array[i].mass_1 = null;
						calc_data_array[i].mass_2 = null;
						calc_data_array[i].rel_weight = null;
						calc_data_array[i].mass_ratio = null;
						temp_color_array[i] = null;
					}
				}
				tof.calc_data_for_poes = calc_data_array;
				tof.tof_colors = temp_color_array;
				tof.detach_poe_titles = poe_titles;
			}
		}
		is.nextLine(); // Gets any characters at end of tof.
		return tof;
	}
}
