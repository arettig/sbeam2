package sbeam2;

import java.awt.Color;

import javax.swing.JFrame;

import sbeam2.POEData;
import sbeam2.scale;
import sbeam2.gui.MessageDialog;
import sbeam2.POECalcData;
import sbeam2.TOFData;

public class CalcData {

	public int CalcNumber;
	public TOFData[] calculated_tofs;

	public String title;
	public int num_poes, num_total_poes;
	public float ion_m_e;

	public int num_beam_ang_segs, num_det_ang_segs, num_ionizer_segs;
	public boolean is_ionizer_gaussian;
	public float[] ionizer_prob_dist;    // Normalized probability distribution
	public float[] ionizer_distance;     // Array of distances from the ionizer center (in cm)
	public float[] beam_velocities;    // In m/s
	public float[] bm_vel_prob_dist, bm_vel_squared;   // Boltzmann distrib or input data

	public float[] lab_velocities, lab_vel_squared;

	public float[] cos_theta_lab, sin_theta_lab, cos_polar_angle, sin_polar_angle;
	public float[] depolar_major, depolar_minor;

	public float alpha, speed_ratio;
	public int num_beam_vel_segs;
	public float min_lab_velocity, max_lab_velocity;

	public float flight_length, beam_angular_width, detector_angular_width, ionizer_length;
	public float starting_time, ending_time;
	public int num_tof_points, num_lab_vel_segs, num_tofs;
	public float[] tof_lab_angles, tof_polarization_angles, depolarization;
	public boolean[] tof_is_polarized;

	public double[] cos_theta_beam, sin_theta_beam;
	public double[] cos_theta_detector, sin_theta_detector;

	public float[][] cos_phi_beam, sin_phi_beam;
	public float[][] cos_phi_detector, sin_phi_detector;

	public float[][][][][] TOF_2cos_gamma;  // YIKES!!!

	public POECalcData[] data_for_poes, temp_data_for_poes;

	public float[][][][] min_u_squared_array, max_u_squared_array;

	public boolean bm_angle_has_changed, det_angle_has_changed, is_number_density_calc;

	public int old_num_tofs, old_num_theta_bm, old_num_theta_det;
	public int[] included_tof_num_array;

	public JFrame main_frame_window;
	public MessageDialog message_dialog;
	public SBApp sb;
	
	
	public CalcData() {
		// TODO Auto-generated constructor stub
	}
	
	public CalcData(int title_calc_number, int num_tot_poes, float ionizer_len, JFrame main_window, SBApp app)
	{
		int i;

		sb = app;
		main_frame_window = main_window;
		message_dialog = new MessageDialog(main_window);
		ionizer_length = ionizer_len;

		// Set default calculation numbers
		title = "";
		String title_number;
		CalcNumber = title_calc_number;

		// Default title is just Convolution # XX
		title = "Convolution #";
		title_number = "" + title_calc_number;
		title += title_number;

		num_poes = 0;
		num_tofs = 0;
		num_total_poes = num_tot_poes;
		ion_m_e = 1.0f;

		num_beam_ang_segs = 4;
		num_det_ang_segs = 4;

		is_ionizer_gaussian = true;
		alpha = 76;
		speed_ratio = 13;
		num_beam_vel_segs = 5;

		starting_time = 0;
		ending_time = 1000;
		num_tof_points = 1001;

		bm_angle_has_changed = true;
		det_angle_has_changed = true;
		is_number_density_calc = true;
		
		cos_theta_beam = null;
		sin_theta_beam = null;

		depolar_major = null;
		depolar_minor = null;

		SetIonizerDist(3, ionizer_length, ionizer_prob_dist);
		SetBmVelDistrib(5, 76, 13, null, null);
		SetLabVelocityArrays(0, 5000, 400);
		//SetNumBeamAngleSegs(DEFAULT_ANG_SEGS);

		data_for_poes = new POECalcData[num_total_poes];

		// Initialize all POE_calc_data structs
		for(i = 0; i < num_total_poes; i++)
		{
			data_for_poes[i] = new POECalcData();
			// Give each one a single ion channel, but not contributing to calculation
			data_for_poes[i].beta_param = 0.0f;
			data_for_poes[i].num_channels = 1;
			data_for_poes[i].is_included = false;
			data_for_poes[i].mass_1 = new float[1];
			data_for_poes[i].mass_1[0] = 1.0f;
			data_for_poes[i].mass_2 = new float[1];
			data_for_poes[i].mass_2[0] = 1.0f;
			data_for_poes[i].rel_weight = new float[1];
			data_for_poes[i].rel_weight[0] = 1.0f;
			data_for_poes[i].mass_ratio = null;
		}
		ReplacePOECalcData(false);
		tof_lab_angles = null;
		tof_polarization_angles = null;


		old_num_tofs = 0;
		old_num_theta_bm = 0;
		old_num_theta_det = 0;

		detector_angular_width = 0;
		beam_angular_width = 0;
	}
	
	
	
	
	
	/* CALCULATION STUFF TAKEN FROM SBEAM1 */
	
	
	public void SetNumBeamAngleSegs(int new_value){
		// Sets number of segments and calculates

		// Note:  The division of the beam angle was devised such that each discrete
		// angle theta is representative of 1/(num_beam_ang_segs - 1) of the beam.  i.e. the
		// theta angles are determined by splitting the beam up into theta segments such
		// that the probability of finding a molecule at an angle in each specific segment
		// is equal.  This allows simplification in later calculations since the probability
		// of each theta does not need to be stored.  The probability of each discrete theta
		// will simply be 1/(num_beam_ang_segs - 1).  This can be done relatively easily since
		// a cos^2 distribution is assumed for the beam angle between 0 and the full beam angle,
		// which can be solved analytically, so a determination of which discrete thetas to
		// choose is relatively straightforward.

		// The division of the azimuthal angle, phi, is even more straightforward, since
		// all possible phi are equally probable.  Thus, the ith theta value is divided up
		// into (3i + 1) discrete phi pieces, each of which carries equal probability.

		// In the end, the total probability of having a velocity vector in some spherical
		// shell in velocity space is simply the product of the P(v) from the speed distribution,
		// and the P(theta) and P(phi) determined here, all of which can be normalized individually.

		double cos_cubed, cos_full_bm_angle, normalization_factor, difference;
		double temp_cos;
		float temp_phi, phi_difference;
		float[] temp_cos_phi_array, temp_sin_phi_array;
		int i, j, num_phis;


		if(((new_value <= 0) || (new_value == num_beam_ang_segs)) && (bm_angle_has_changed == false))
			return;  // Don't make any changes in this case!



		// Delete any information which has already been stored

		
		// Store new beam angle information
		num_beam_ang_segs = new_value;

		cos_theta_beam = new double[num_beam_ang_segs];
		sin_theta_beam = new double[num_beam_ang_segs];

		cos_phi_beam = new float[num_beam_ang_segs][];
		sin_phi_beam = new float[num_beam_ang_segs][];



		cos_full_bm_angle = Math.cos(Math.PI/180*(beam_angular_width));

		normalization_factor = 1.0 - Math.pow(cos_full_bm_angle, 3);
		if(num_beam_ang_segs == 1)
			difference = 0.0;
		else
			difference = normalization_factor / (num_beam_ang_segs - 1);


		// Iteratively get new cos_theta_beam from old one.  The new theta is determined
		// by finding a theta such that the probability of having an angle between the old
		// theta and the new theta is equal to 1/(num_beam_ang_segs - 1).
		for(i = 0; i < num_beam_ang_segs; i++)
		{
			cos_cubed = 1.0 - i * difference;
			temp_cos = Math.pow(cos_cubed, (1.0/3.0));
			cos_theta_beam[i] = temp_cos;
			sin_theta_beam[i] = Math.sqrt(1.0 - temp_cos * temp_cos);

			num_phis = 3*i + 1;
			phi_difference = (float) ((2 * Math.PI)/num_phis); // Don't need num_phis - 1 since last
			// phi is not 2*Pi!
			cos_phi_beam[i] = new float[num_phis];
			sin_phi_beam[i] = new float[num_phis];

			// Use the following to save time lost by dereferencing in for loop over many j values
			temp_cos_phi_array = cos_phi_beam[i];
			temp_sin_phi_array = sin_phi_beam[i];

			// Each of these will have equal probability, so the probabilities won't need to be stored
			for(j = 0; j < num_phis; j++)
			{
				temp_phi = j * phi_difference;
				temp_cos_phi_array[j] = (float) Math.cos(temp_phi);
				temp_sin_phi_array[j] = (float) Math.sin(temp_phi);
			}
		} // End of loop over all possible i values from zero to num_bm_ang_segs - 1
		bm_angle_has_changed = false;
	}
	// arrays of cos_theta_beam, sin_theta_beam,
	// cos_phi_beam, and sin_phi_beam

	public void SetNumDetAngleSegs(int new_value){
		// Sets number of segments and calculates

		// The angle of the detector relative to the center of the molecular beam at the
		// interaction is divided up exactly analogously to the beam angle division.
		// (See the description in the function Calc_data::SetNumBeamAngleSegs(int)).
		// In this case, however, the beam is simply divided such that the integrated
		// area between two theta values will be the same and equal to the total area of
		// the detector over (num_det_ang_segs - 1).  i.e. This is like the above division
		// except the initial probability distribution is uniform rather than proportional
		// to cos^2

		// The detector theta angle is measured relative to the detector axis whereas the
		// detector phi angle is the azimuthal angle which can take on any value from zero
		// to 2*PI radians, again uniformly.  The ith theta annulus is divided into 3i + 1
		// phi pieces.


		double cos_full_det_angle, normalization_factor, difference;
		double temp_cos;
		float temp_phi, phi_difference;
		float[] temp_cos_phi_array, temp_sin_phi_array;
		int i, j, num_phis;


		if((new_value <= 0) || ((new_value == num_det_ang_segs) && (det_angle_has_changed == false)))
			return;  // Don't make any changes in this case!



		// Delete any information which has already been stored
		
		// Store new beam angle information
		num_det_ang_segs = new_value;

		cos_theta_detector = new double[num_det_ang_segs];
		sin_theta_detector = new double[num_det_ang_segs];

		cos_phi_detector = new float[num_det_ang_segs][];
		sin_phi_detector = new float[num_det_ang_segs][];



		cos_full_det_angle = Math.cos(Math.PI/180*(detector_angular_width));

		normalization_factor = 1.0 - cos_full_det_angle;
		if(num_det_ang_segs == 1)
			difference = 0.0;
		else
			difference = normalization_factor / (num_det_ang_segs - 1);


		// Iteratively get new cos_theta_detector from old one.  The new theta is determined
		// by finding a theta such that the probability of having an angle between the old
		// theta and the new theta is equal to 1/(num_det_ang_segs - 1).
		for(i = 0; i < num_det_ang_segs; i++)
		{
			temp_cos = 1.0 - i * difference;
			cos_theta_detector[i] = temp_cos;
			sin_theta_detector[i] = Math.sqrt(1.0 - temp_cos * temp_cos);


			num_phis = 3*i + 1;
			phi_difference = (float) ((2 * Math.PI)/num_phis); // Don't need num_phis - 1 since last
			// phi is not 2*Pi!
			cos_phi_detector[i] = new float[num_phis];
			sin_phi_detector[i] = new float[num_phis];

			// Use the following to save time lost by dereferencing in for loop over many j values
			temp_cos_phi_array = cos_phi_detector[i];
			temp_sin_phi_array = sin_phi_detector[i];

			// Each of these will have equal probability, so the probabilities won't need to be stored
			for(j = 0; j < num_phis; j++)
			{
				temp_phi = j * phi_difference;
				temp_cos_phi_array[j] = (float) Math.cos(temp_phi);
				temp_sin_phi_array[j] = (float) Math.sin(temp_phi);
			}
		} // End of loop over all possible i values from zero to num_bm_ang_segs - 1
		det_angle_has_changed = false;



	}
	// arrays of cos_theta_detector, sin_theta_detector,
	// cos_phi_detector, and sin_phi_detector


	public void SetIonizerDist(int num_segments, float ionizer_len, float[] array){
		// Note that normalization is not done at this point since, in the end, everything
		// is scaled anyway, so the multiplicative constant doesn't matter

		int i;
		float ionizer_spacings, x_over_sigma, /*normalization_const,*/ exponent_value;
		float ionizer_length_spacings;

		ionizer_length = ionizer_len;

		num_ionizer_segs = num_segments;
		
		// If array = 0 (default), then assume a Gaussian distribution and calculate
		// that distribution
		if(array == null)
		{
			is_ionizer_gaussian = true;

			// Initialize ionizer distribution (Gaussian)
			ionizer_prob_dist = new float[num_ionizer_segs];
			ionizer_distance = new float[num_ionizer_segs];

			if(num_ionizer_segs == 1)
			{
				ionizer_prob_dist[0] = 1.0f;
				ionizer_distance[0] = 0.0f;
				return;
			}
			// The numbers 4.90 & 2.45 are used here since the Gaussian distribution is
			// assumed to have a width such that the probability drops to ~exp(-3) at the
			// outer edges of the ionizer.  Thus, x^2/2*sigma^2 ~ 3, so x/sigma~2.45
			ionizer_spacings = (float) (2 * 2.58 / (num_ionizer_segs - 1));
			ionizer_length_spacings = ionizer_len / (num_ionizer_segs - 1);
			//normalization_const = 0.0;
			for(i = 0; i < num_ionizer_segs; i++)
			{

				x_over_sigma = (float) (- 2.58 + i * ionizer_spacings);
				exponent_value = (float) Math.exp(-(x_over_sigma * x_over_sigma) / 2);
				ionizer_prob_dist[i] = exponent_value;
				ionizer_distance[i] = -(ionizer_len / 2) + i * ionizer_length_spacings;
				//normalization_const += (ionizer_spacings * exponent_value);
			}
			// Normalize the distribution:
			/*for(i = 0; i < num_ionizer_segs; i++)
		   {
		   	ionizer_prob_dist[i] /= normalization_const;
		   } */
		}

		else
		{
			is_ionizer_gaussian = false;
			ionizer_prob_dist = array;
		}
	}

	public void SetIonizerLen(float ionizer_len){
		// Used when ionizer length changes
		// Used only by Document to change ionizer length without changing the probability
		// distribution & # of points; thus, only need to put new info into ionizer_distance
		// array.

		// Note:  may need to renormalize ionization distribution if necessary!!

		int i;
		float ionizer_length_spacings;
		ionizer_length = ionizer_len;

		ionizer_length_spacings = ionizer_len / (num_ionizer_segs - 1);
		for(i = 0; i < num_ionizer_segs; i++)
		{
			ionizer_distance[i] = -(ionizer_len / 2) + i * ionizer_length_spacings;
		}
	}
	
	public void SetBeamAng(float bm_angle_width){
		// Used when angular width of beam changes
		if(beam_angular_width == bm_angle_width)
			return;
		beam_angular_width = bm_angle_width;
		bm_angle_has_changed = true;
		SetNumBeamAngleSegs(num_beam_ang_segs);
	}
	
	public void SetDetectorAng(float detector_angle_width){
		// Used when angular width of detector changes
		if(detector_angular_width == detector_angle_width)
			return;
		detector_angular_width = detector_angle_width;
		det_angle_has_changed = true;
		SetNumDetAngleSegs(num_det_ang_segs);
	}


	public void SetBmVelDistrib(int num_segs, float alfa, float spd_ratio, float[] prob_array, float[] array){
		// This function uses alpha, the speed_ratio, and the number of beam velocity
		// segments to find the beam velocity probability distribution as a function
		// of beam velocity.  Is the user so desires, an input pair of arrays for the
		// velocity and probability distribution are sent to this function directly.

		// The function also normalizes the distribution.  Although this is not explicitly
		// required at this point due to the fact that the final TOF will be scaled arbitrarily,
		// resulting in a lack of necessity for the multiplicative constant,
		// the normalization helps to maintain reasonable orders of magnitude throughout the
		// calculation.  Finally, the square of the beam velocity is determined since this is
		// used in later calculations involving the Law of Cosines.



		int i;
		float peak_vel, upper_lim, lower_lim, square_root_value;
		float prob_pk_vel, prob_upper_lim, prob_vel, bm_vel_spacings;
		float difference_over_alpha, step_size, velocity;
		float alpha_multiplier;
		float normalization_const;

		if((prob_array != null) && (num_beam_vel_segs == num_segs) && (alpha == alfa) && (speed_ratio == spd_ratio))
			return;

		num_beam_vel_segs = num_segs;

		bm_vel_squared = new float[num_beam_vel_segs];

		if(prob_array == null)
		{
			beam_velocities = new float[num_beam_vel_segs];
			bm_vel_prob_dist = new float[num_beam_vel_segs];


			alpha = alfa;
			speed_ratio = spd_ratio;

			alpha_multiplier = 2.1f; // Initially start with 2.1

			square_root_value = (float) (1 + (4.0 / (speed_ratio * speed_ratio)));
			peak_vel = (float) ((alpha * speed_ratio * (1 + Math.sqrt(square_root_value))) / 2.0);

			if(num_beam_vel_segs == 1)
			{
				beam_velocities[0] = peak_vel;
				bm_vel_squared[0] = peak_vel * peak_vel;
				bm_vel_prob_dist[0] = 1;
				normalization_const = 1.0f;
			}
			else
			{
				difference_over_alpha = (peak_vel - speed_ratio * alpha) / alpha;
				prob_pk_vel = (float) (peak_vel * peak_vel * Math.exp(-(difference_over_alpha * difference_over_alpha)));

				// Find a good upper limit of beam velocity distribution.  (i.e. one in which
				// the probability of the upper limit is ~0.01 to 0.025 that of the peak probability)
				upper_lim = peak_vel + (alpha_multiplier * alpha);
				lower_lim = peak_vel;

				difference_over_alpha = (upper_lim - speed_ratio * alpha) / alpha;
				prob_upper_lim = (float) (upper_lim * upper_lim * Math.exp(-(difference_over_alpha * difference_over_alpha)));

				while((prob_upper_lim/prob_pk_vel) <= 0.01 || (prob_upper_lim/prob_pk_vel) >= 0.025)
				{
					if((prob_upper_lim/prob_pk_vel) <= 0.01)
					{
						//i.e. too far out!
						upper_lim = (upper_lim + lower_lim) / 2;
						difference_over_alpha = (upper_lim - speed_ratio * alpha) / alpha;
						prob_upper_lim = (float) (upper_lim * upper_lim * Math.exp(-(difference_over_alpha * difference_over_alpha)));

					}
					if((prob_upper_lim/prob_pk_vel) >= 0.025)
					{
						// not far enough!
						step_size = upper_lim - lower_lim;
						lower_lim += step_size;
						upper_lim += step_size;
						difference_over_alpha = (upper_lim - speed_ratio * alpha) / alpha;
						prob_upper_lim = (float) (upper_lim * upper_lim * Math.exp(-(difference_over_alpha * difference_over_alpha)));
					}
				}

				lower_lim = 2 * peak_vel - upper_lim;
				if(lower_lim < 0)
					lower_lim = 0;
				bm_vel_spacings = (upper_lim - lower_lim) / (num_beam_vel_segs - 1);

				normalization_const = 0.0f;
				for(i = 0; i < num_beam_vel_segs; i++)
				{
					velocity = lower_lim + i * bm_vel_spacings;
					beam_velocities[i] = velocity;
					bm_vel_squared[i] = velocity * velocity;

					difference_over_alpha = (velocity - speed_ratio * alpha) / alpha;
					prob_vel = (float) (velocity * velocity * Math.exp(-(difference_over_alpha * difference_over_alpha)));

					// Use Trapezoid rule to "integrate" the probabilities
					if((i == 0) || (i == num_beam_vel_segs - 1))
					{
						normalization_const += 0.5 * prob_vel;
					}
					else
					{
						normalization_const += prob_vel;
					}

					bm_vel_prob_dist[i] = prob_vel;
				}
				//normalization_const *= bm_vel_spacings;  // Not included since numbers get too small!
			} // End of else for if(num_bm_vel_segs == 1)
		}
		else
		{
			beam_velocities = array;
			bm_vel_prob_dist = prob_array;

			if(num_beam_vel_segs == 1)
			{
				bm_vel_squared[0] = beam_velocities[0] * beam_velocities[0];
				bm_vel_prob_dist[0] = 1;
				normalization_const = 1.0f;
			}
			else
			{
				// Normalize probabilities
				normalization_const = 0.0f;

				bm_vel_spacings = beam_velocities[1] - beam_velocities[0];
				for(i = 0; i < num_beam_vel_segs; i++)
				{
					bm_vel_squared[i] = beam_velocities[i] * beam_velocities[i];
					// Use Trapezoid rule to "integrate" the probabilities
					if((i == 0) || (i == num_beam_vel_segs - 1))
					{
						normalization_const += 0.5 * bm_vel_prob_dist[i];
					}
					else
					{
						normalization_const += bm_vel_prob_dist[i];
					}
				}
				normalization_const *= bm_vel_spacings;
			}// End of else for if(num_bm_vel_segs == 1)

		}

		// Divide everything in the array by the normalization constant
		for(i = 0; i < num_beam_vel_segs; i++)
		{
			bm_vel_prob_dist[i] /= normalization_const;
		}  
	}

	public void SetLabVelocityArrays(float min_vel, float max_vel, int num_segs){
		int i;
		float difference, temp_velocity;

		// Quick fixes for now;  don't allow bad input to screw up calculations
		if(num_segs > 0)
			num_lab_vel_segs = num_segs;
		if((min_vel < max_vel) && (min_vel >= 0))
		{
			min_lab_velocity = min_vel;
			max_lab_velocity = max_vel;
		}

		difference = (max_lab_velocity - min_lab_velocity)/ (num_lab_vel_segs - 1);

		
		lab_velocities = new float[num_lab_vel_segs];
		lab_vel_squared = new float[num_lab_vel_segs];

		for(i = 0; i < num_lab_vel_segs; i++)
		{
			temp_velocity = min_lab_velocity + i * difference;
			lab_velocities[i] = temp_velocity;
			lab_vel_squared[i] = temp_velocity * temp_velocity;
		}
	}
	
	public void SetTOFInfo(float[] lab_array, float[] polarization_array, float[] depol, boolean[] is_polar){
		int i;
		float temp_angle, temp_depolar;

		tof_lab_angles = lab_array;
		tof_polarization_angles = polarization_array;
		depolarization = depol;
		tof_is_polarized = is_polar;

		// Find cosines and sines of the angles and find polarization information
		cos_theta_lab = new float[num_tofs];
		sin_theta_lab = new float[num_tofs];
		cos_polar_angle = new float[num_tofs];
		sin_polar_angle = new float[num_tofs];
		depolar_major = new float[num_tofs];
		depolar_minor = new float[num_tofs];

		for(i = 0; i < num_tofs; i++)
		{
			temp_angle = (float) (Math.PI/180f*(tof_lab_angles[i]));
			cos_theta_lab[i] = (float) Math.cos(temp_angle);
			sin_theta_lab[i] = (float) Math.sin(temp_angle);

			if(tof_is_polarized[i] == true)
			{
				temp_angle = (float) (Math.PI/180f*(tof_polarization_angles[i]));
				cos_polar_angle[i] = (float) Math.cos(temp_angle);
				sin_polar_angle[i] = (float) Math.sin(temp_angle);

				temp_depolar = depolarization[i];
				depolar_major[i] = 1/(1 + temp_depolar);
				depolar_minor[i] = temp_depolar/(1 + temp_depolar);
			}
			else
			{
				cos_polar_angle[i] = 1.0f;
				sin_polar_angle[i] = 0.0f;
				depolar_major[i] = 0.5f;
				depolar_minor[i] = 0.5f;
			}
		}


	}
	
	public void SetTOFTimeInfo(float start_time, float end_time, int num_points)
	{
		starting_time = start_time;
		ending_time = end_time;
		num_tof_points = num_points;
	}
	
	public void ResetNumContribPOEs(){
		int i;

		num_poes = 0;

		for(i = 0; i < num_total_poes; i++)
		{
			if(temp_data_for_poes[i].is_included)
			{
				num_poes++;
			}
		}
	}
	
	public void ReplacePOECalcData(boolean t_or_f){
		if(t_or_f == true)
		{
			data_for_poes = temp_data_for_poes;
		}
		else
		{
			temp_data_for_poes = data_for_poes;
		}


		ResetNumContribPOEs();
	}
	
	
	public void SetPOECalcData(POECalcData []new_poe_calc){
		int i, j, number_channels;
		float mass1, mass2, temp_mass_ratio;
		float sum;
		POECalcData this_poecalcdata;

		num_poes = 0;

		// Delete old data for P(E)'s
		for(i = 0; i < num_total_poes; i++)
		{
			/*if(data_for_poes[i].mass_1)
		      	delete data_for_poes[i].mass_1;
		      if(data_for_poes[i].mass_2)
		      	delete data_for_poes[i].mass_2;
		      if(data_for_poes[i].rel_weight)
		      	delete data_for_poes[i].rel_weight;
		      if(data_for_poes[i].mass_ratio)
		      	delete data_for_poes[i].mass_ratio;   */

			// Can't delete all this since new_poe_calc has these pointers in it

			if(new_poe_calc[i].is_included)
				num_poes++;
		}
		//delete data_for_poes;

		temp_data_for_poes = new_poe_calc;
		//data_for_poes = new_poe_calc;

		sum = 0.0f;
		// Determine mass_ratios and scale all relative_weights
		//
		// Here, mass ratio is (m1 * (m1 + m2))/ (2*m2)
		// Extra factor of 1/(4.184e6) is so units will be (kcal sec^2 / m^2 mol) so energies
		// will be in units of kcal/mol
		for(i = 0; i < num_total_poes; i++)
		{
			this_poecalcdata = (temp_data_for_poes[i]);
			if(this_poecalcdata.is_included)
			{
				number_channels = this_poecalcdata.num_channels;
				this_poecalcdata.mass_ratio = new float[number_channels];
				for(j = 0; j < number_channels; j++)
				{
					mass1 = this_poecalcdata.mass_1[j];
					mass2 = this_poecalcdata.mass_2[j];
					temp_mass_ratio = (float) ((mass1 * (mass1 + mass2)) / (2 * 4.184e6 * mass2));
					this_poecalcdata.mass_ratio[j] = temp_mass_ratio;

					sum += this_poecalcdata.rel_weight[j];
				}
			}
		}

		// Normalize the relative weights of all the channels
		if(sum != 0.0)
		{
			for(i = 0; i < num_total_poes; i++)
			{
				this_poecalcdata = (temp_data_for_poes[i]);
				if(this_poecalcdata.is_included)
				{
					for(j = 0; j < this_poecalcdata.num_channels; j++)
					{
						this_poecalcdata.rel_weight[j] /= sum;
					}
				}
			}
		}
	}

	public void SetTOF2CosGamma(){
		int tof_num, theta_bm_num, phi_bm_num, theta_det_num, phi_det_num;

		int num_bm_phis, num_det_phis;
		//float time;

		float[][][][] this_tof_2cos_gamma;
		float[][][] this_det_theta_2cos_gamma;
		float[][] this_det_phi_2cos_gamma;
		float[] this_bm_theta_2cos_gamma;

		float[] dereferenced_cos_phi_beam;
		float[] dereferenced_sin_phi_beam;
		float[] dereferenced_cos_phi_det;
		float[] dereferenced_sin_phi_det;
		float cos_lab_thistof;
		float sin_lab_thistof;
		float cos_this_beam_theta;
		float sin_this_beam_theta;
		float cos_this_beam_phi;
		float sin_this_beam_phi;
		float cos_this_det_theta;
		float sin_this_det_theta;
		float cos_this_det_phi;
		float sin_this_det_phi;

		//clock_t start, end;
		//start = clock();


		old_num_tofs = num_tofs;
		old_num_theta_bm = num_beam_ang_segs;
		old_num_theta_det = num_det_ang_segs;

		if(num_tofs == 0)
		{
			TOF_2cos_gamma = null;
			return;
		}

		// Allocate memory for this 5 dimensional array
		TOF_2cos_gamma = new float[num_tofs][][][][];
		for(tof_num = 0; tof_num < num_tofs; tof_num++)
		{
			TOF_2cos_gamma[tof_num] = new float[num_det_ang_segs][][][];
			this_tof_2cos_gamma = TOF_2cos_gamma[tof_num];

			cos_lab_thistof = cos_theta_lab[tof_num];
			sin_lab_thistof = sin_theta_lab[tof_num];

			for(theta_det_num = 0; theta_det_num < num_det_ang_segs; theta_det_num++)
			{
				num_det_phis = 3 * theta_det_num + 1;
				//TOF_2cos_gamma[tof_num][theta_det_num] = new float**[num_det_phis];
				this_tof_2cos_gamma[theta_det_num] = new float[num_det_phis][][];
				this_det_theta_2cos_gamma = this_tof_2cos_gamma[theta_det_num];

				cos_this_det_theta = (float) cos_theta_detector[theta_det_num];
				sin_this_det_theta = (float) sin_theta_detector[theta_det_num];
				dereferenced_cos_phi_det = cos_phi_detector[theta_det_num];
				dereferenced_sin_phi_det = sin_phi_detector[theta_det_num];

				for(phi_det_num = 0; phi_det_num < num_det_phis; phi_det_num++)
				{
					//TOF_2cos_gamma[tof_num][theta_det_num][phi_det_num] = new float*[num_bm_ang_segs];
					this_det_theta_2cos_gamma[phi_det_num] = new float[num_beam_ang_segs][];
					this_det_phi_2cos_gamma = this_det_theta_2cos_gamma[phi_det_num];

					cos_this_det_phi = dereferenced_cos_phi_det[phi_det_num];
					sin_this_det_phi = dereferenced_sin_phi_det[phi_det_num];


					for(theta_bm_num = 0; theta_bm_num < num_beam_ang_segs; theta_bm_num++)
					{
						num_bm_phis = 3 * theta_bm_num + 1;
						//TOF_2cos_gamma[tof_num][theta_det_num][phi_det_num][theta_bm_num] = new float[num_bm_phis];
						this_det_phi_2cos_gamma[theta_bm_num] = new float[num_bm_phis];
						this_bm_theta_2cos_gamma = this_det_phi_2cos_gamma[theta_bm_num];

						cos_this_beam_theta = (float) cos_theta_beam[theta_bm_num];
						sin_this_beam_theta = (float) sin_theta_beam[theta_bm_num];
						dereferenced_cos_phi_beam = cos_phi_beam[theta_bm_num];
						dereferenced_sin_phi_beam = sin_phi_beam[theta_bm_num];

						for(phi_bm_num = 0; phi_bm_num < num_bm_phis; phi_bm_num++)
						{
							cos_this_beam_phi = dereferenced_cos_phi_beam[phi_bm_num];
							sin_this_beam_phi = dereferenced_sin_phi_beam[phi_bm_num];

							// Determine 2*cos(gamma) for each pair of detector & beam angles
							// for later use in the Law of Cosines:  c^2 = a^2 + b^2 - 2abcos(gamma)

							// (For each TOF!)
							this_bm_theta_2cos_gamma[phi_bm_num] = 2 *
									( (sin_this_beam_theta*cos_this_beam_phi*sin_this_det_theta*cos_this_det_phi)
											+ (sin_this_beam_theta*sin_this_beam_phi*sin_this_det_theta*sin_this_det_phi*cos_lab_thistof)
											+ (sin_this_beam_theta*sin_this_beam_phi*cos_this_det_theta*sin_lab_thistof)
											-	(cos_this_beam_theta*sin_this_det_theta*sin_this_det_phi*sin_lab_thistof)
											+ (cos_this_beam_theta*cos_this_det_theta*cos_lab_thistof) );

						}
					}
				}
			}
		}

		//end = clock();
		//time = (end - start)/CLK_TCK;
		//time *= 1.0;

		//end = clock();
	}

	public TOFData[] RunMainFlightTimeCalculation(POEData[] poes, int num_new_tofs,
			float beam_half_width, float det_half_width){
		// For initial run-through, need to step through each new TOF which is being created
		// and determine the TOF from all the input data.  Then, the TOF must me created in the
		// session document so it can be displayed with other TOF's.

		/*float time;
		   clock_t start, end;
		    start = clock();   */

		int[] number_channels;
		Color[] tof_colors; 

		// Need to get the P(E)'s from the document when needed.
		int i, tof_num, poe_num, chan_num, vel_num, bm_theta_num, bm_phi_num;
		int bm_vel_num, det_theta_num, det_phi_num;
		int this_num_channels, this_poe_num_points;
		int this_num_det_phi_segs, this_num_bm_phi_segs;
		int int_energy_pt, num_included_poes;

		int min_lab_vel_num, max_lab_vel_num;


		float half_width_sum_deg = beam_angular_width + detector_angular_width;
		float half_width_sum = (float) (Math.PI/180*(half_width_sum_deg));

		float cos_half_width_sum = (float) Math.cos(half_width_sum);
		float sin_half_width_sum = (float) Math.sin(half_width_sum);

		float cos_this_theta_lab, sin_this_theta_lab;
		float cos_this_theta_bm, sin_this_theta_bm;
		float sin_this_phi_bm, sin_this_phi_det, cos_theta_polar_major;

		float poe_energy_inc_inverse, first_two_usqr_terms, lab_vel_times_bm_vel;
		float u_squared, u_inverse;

		float energy_point, lower_energy_amp, energy_amp;

		float this_poe_beta;
		float this_chan_mass_ratio, this_chan_inverse_mass_ratio, this_chan_rel_weight;
		float this_poe_energy_min, this_poe_energy_max, this_poe_energy_increment;
		float this_lab_velocity, this_lab_velocity_squared;
		float this_bm_velocity, this_bm_vel_squared;

		float[] this_poe_mass_ratios, this_poe_rel_weights;
		float[] this_poe_energy_amp;

		float cos_cos_term, sin_sin_term;
		float cos_smallest_gamma, cos_largest_gamma;

		float cos_theta_recoil, sin_theta_recoil;
		float[] deref_sin_phi_det, deref_sin_phi_bm;

		float this_depolar_minor, depolar_difference;
		float this_cos_polar, this_sin_polar;

		float cos_this_theta_det, sin_this_theta_det;   


		float[][][][] this_tof_2cos_gamma;
		float[][][] this_det_theta_2cos_gamma;
		float[][] this_det_phi_2cos_gamma;
		float[] this_bm_theta_2cos_gamma;

		float[][][][] prob_lab_vel;
		float[][][] prob_lab_vel_this_tof;
		float[][] pr_lv_this_poe;
		float[] prob_lab_vel_this_chan;

		float[][] tof_arrays_this_poe;
		float[] tof_array_this_channel;
		float[] tof_array_poe;

		int tof_point_num, ion_point_num, time_seg_num;


		float first_lab_vel;
		float inverse_lab_vel_spacings;
		float velocity_point, lower_vel_prob, this_tof_time, this_ionizer_count, this_distance;
		float this_time_count, this_time_inverse, real_lab_vel;

		int vel_point_int, count;
		int count_vel = 0;


		POECalcData current_poe_data;
		POEData this_poe;

		float u_smallest_squared, u_min_squared_poe;
		float u_largest_squared, u_max_squared_poe;
		float u_squared_v_first, u_squared_v_last;
		float v_beam_smallest_u;
		float v_beam_first, v_beam_last;
		float v_beam_first_squared, v_beam_last_squared;

		float amount_per_vel_seg, percent_done, amount_per_chan, amount_per_poe;

		float lab_vel_times_cos_theta_det, bm_vel_times_cos_theta_bm_times_cos_theta_lab;
		float bm_vel_times_cos_theta_bm_times_sin_theta_lab, lab_vel_times_sin_theta_det;
		float lab_vel_times_sin_th_det_times_sin_phi_det;
		float bm_vel_times_sin_theta_bm_times_cos_theta_lab, bm_vel_times_sin_theta_bm_times_sin_theta_lab;

		float dwell_time_micro;

		float det_phi_prob_sum, det_theta_prob_sum, bm_theta_prob_sum, bm_phi_prob_sum;
		//float this_bm_vel_prob;

		float min_u_squared_this_vel, max_u_squared_this_vel;

		float[][][] this_tof_min_u_squared_array, this_tof_max_u_squared_array;
		float[][] this_poe_min_u_squared_array, this_poe_max_u_squared_array;
		float[] this_chan_min_u_squared_array, this_chan_max_u_squared_array;

		float energy_min_over_energy_inc, mass_ratio_over_energy_inc;

		float depolar_term_1, depolar_term_2;

		float[] time_array_for_tofs = null;

		float[][][] tof_arrays;
		float[] total_tof_counts;

		num_new_tofs = num_tofs;
		if(num_tofs == 0)
		{
			calculated_tofs = null;
			return null;
		}
		TOFData[] new_calc_tofs = new TOFData[num_tofs];
		calculated_tofs = new_calc_tofs;

		TOFData this_TOF;
		//float*dummy1, *dummy2;

		first_lab_vel = lab_velocities[0];
		inverse_lab_vel_spacings = (float) (1.0 / (lab_velocities[1] - first_lab_vel));

		v_beam_first = beam_velocities[0];
		v_beam_first_squared = bm_vel_squared[0];
		v_beam_last = beam_velocities[num_beam_vel_segs - 1];
		v_beam_last_squared = bm_vel_squared[num_beam_vel_segs - 1];

		dwell_time_micro = (ending_time - starting_time) / (num_tof_points - 1);



		String tof_title;
		String dummy;

		prob_lab_vel = new float[num_tofs][][][];



		tof_title = "";

		min_u_squared_array = new float[num_tofs][][][];
		max_u_squared_array = new float[num_tofs][][][];


		if(message_dialog.GetStatus() == false)
			message_dialog.setVisible(true);

			// Do this for every TOF which is to be calculated.
			for(tof_num = 0; tof_num < num_tofs; tof_num++)
			{


				tof_title = "";
				tof_title = "Calc #";

				dummy = "" + CalcNumber;

				tof_title += dummy;
				tof_title += ".";

				dummy = "" + (tof_num + 1);
				tof_title += dummy;

				tof_title += ":  m/e = ";
				dummy = "" + ion_m_e;
				tof_title += dummy;
				tof_title += ", Lab Ang. = ";
				dummy = "" + tof_lab_angles[tof_num];
				tof_title += dummy;
				tof_title += ", Polar Ang. = ";
				if(tof_is_polarized[tof_num])
				{
					dummy = "" + tof_polarization_angles[tof_num];
					tof_title += dummy;
				}
				else
				{
					tof_title += "none";
				}




				message_dialog.SetMessage(tof_title);
				message_dialog.SetPercent(0);

				number_channels = new int[num_total_poes];
				tof_colors = new Color[num_total_poes];


				prob_lab_vel_this_tof = new float[num_total_poes][][];
				tof_arrays = new float[num_total_poes][][];
				total_tof_counts = new float[num_tof_points];
				for(i = 0; i < num_tof_points; i++)
				{
					total_tof_counts[i] = 0.0f;
				}

				//prob_lab_vel[tof_num] = new float**[num_total_poes];
				//prob_lab_vel_this_tof = prob_lab_vel[tof_num];
				prob_lab_vel[tof_num] = prob_lab_vel_this_tof;

				this_tof_2cos_gamma = TOF_2cos_gamma[tof_num];

				//this_depolar_major = depolar_major[tof_num];
				this_depolar_minor = depolar_minor[tof_num];

				depolar_difference = depolar_major[tof_num] - this_depolar_minor;

				this_cos_polar = cos_polar_angle[tof_num];
				this_sin_polar = sin_polar_angle[tof_num];

				cos_this_theta_lab = cos_theta_lab[tof_num];
				sin_this_theta_lab = sin_theta_lab[tof_num];

				this_TOF = new TOFData();
				new_calc_tofs[tof_num] = this_TOF;
				this_TOF.is_real_TOF = false;
				
				this_TOF.title = tof_title;
				this_TOF.dwell = dwell_time_micro;
				this_TOF.dwell_scale = scale.μs;
				this_TOF.offset = starting_time;
				this_TOF.offset_scale = scale.μs;

				min_u_squared_array[tof_num] = new float[num_total_poes][][];
				max_u_squared_array[tof_num] = new float[num_total_poes][][];
				this_tof_min_u_squared_array = min_u_squared_array[tof_num];
				this_tof_max_u_squared_array = max_u_squared_array[tof_num];

				num_included_poes = 0;
				// Find num_included_poes!
				for(poe_num = 0; poe_num < num_total_poes; poe_num++)
				{
					current_poe_data = data_for_poes[poe_num];
					if(current_poe_data.is_included == true)
					{
						num_included_poes++;
					}
				}
				if(num_included_poes != 0)
				{
					amount_per_poe = (float) (1.0/num_included_poes);
				}
				else
				{
					amount_per_poe = 0;
				}
				count = 0;
				for(poe_num = 0; poe_num < num_total_poes; poe_num++)
				{
					current_poe_data = data_for_poes[poe_num];
					if(current_poe_data.is_included == true)
					{
						sb.poes.get(poe_num).AssociatedCalcs.add(this);
						this_num_channels = current_poe_data.num_channels;

						number_channels[poe_num] = this_num_channels;
						pr_lv_this_poe = new float[this_num_channels][];
						tof_arrays[poe_num] = new float[this_num_channels + 1][];
						tof_arrays_this_poe = tof_arrays[poe_num];
						tof_arrays_this_poe[0] = new float[num_tof_points];
						tof_array_poe = tof_arrays_this_poe[0];
						for(i = 0; i < num_tof_points; i++)
							tof_array_poe[i] = (float) 0.0;
						prob_lab_vel_this_tof[poe_num] = pr_lv_this_poe;
						this_poe = poes[poe_num];
						this_poe_energy_min = this_poe.min_energy;
						this_poe_energy_max = this_poe.max_energy;
						tof_colors[poe_num] = this_poe.poe_color;

						this_poe_num_points = this_poe.num_points;
						this_poe_energy_increment = (this_poe_energy_max - this_poe_energy_min) / (this_poe_num_points - 1);
						poe_energy_inc_inverse = (float) (1.0 / this_poe_energy_increment);

						energy_min_over_energy_inc = this_poe_energy_min * poe_energy_inc_inverse;

						this_poe_energy_amp = this_poe.poe_amplitudes;
						this_poe_beta = current_poe_data.beta_param;
						this_poe_mass_ratios = current_poe_data.mass_ratio;
						this_poe_rel_weights = current_poe_data.rel_weight;

						this_tof_min_u_squared_array[poe_num] = new float[this_num_channels][];
						this_tof_max_u_squared_array[poe_num] = new float[this_num_channels][];
						this_poe_min_u_squared_array = this_tof_min_u_squared_array[poe_num];
						this_poe_max_u_squared_array = this_tof_max_u_squared_array[poe_num];

						depolar_term_1 = (float) (1.0 + 1.5 * this_poe_beta * this_depolar_minor - 0.5 * this_poe_beta);
						depolar_term_2 = (float) (1.5 * this_poe_beta * depolar_difference);

						amount_per_chan = (float) (1.0/this_num_channels);
						for(chan_num = 0; chan_num < this_num_channels; chan_num++)
						{
							pr_lv_this_poe[chan_num] = new float[num_lab_vel_segs];
							tof_arrays_this_poe[chan_num + 1] = new float[num_tof_points];
							tof_array_this_channel = tof_arrays_this_poe[chan_num + 1];
							prob_lab_vel_this_chan = pr_lv_this_poe[chan_num];

							this_chan_mass_ratio = this_poe_mass_ratios[chan_num];
							this_chan_inverse_mass_ratio = 1 / this_chan_mass_ratio;
							this_chan_rel_weight = this_poe_rel_weights[chan_num];

							mass_ratio_over_energy_inc = this_chan_mass_ratio * poe_energy_inc_inverse;

							u_min_squared_poe = this_poe_energy_min * this_chan_inverse_mass_ratio;
							u_max_squared_poe = this_poe_energy_max * this_chan_inverse_mass_ratio;
							//start = clock();
							//counter=0;

							this_poe_min_u_squared_array[chan_num] = new float[num_lab_vel_segs];
							this_poe_max_u_squared_array[chan_num] = new float[num_lab_vel_segs];
							this_chan_min_u_squared_array = this_poe_min_u_squared_array[chan_num];
							this_chan_max_u_squared_array = this_poe_max_u_squared_array[chan_num];

							min_lab_vel_num = num_lab_vel_segs;
							max_lab_vel_num = 0;

							amount_per_vel_seg = (float) (1.0/num_lab_vel_segs);

							for(vel_num = 0; vel_num < num_lab_vel_segs; vel_num++)
							{

								//counter++;
								//end = clock();
								//time = (end - start)/CLK_TCK;
								this_lab_velocity = lab_velocities[vel_num];
								//System.out.println(Arrays.toString(lab_velocities));
								//start = clock();

								this_lab_velocity_squared = lab_vel_squared[vel_num];
								// See if this P(E) channel can contribute to this specific lab velocity
								// by finding extrema of u_squared which will give signal for this channel.

								// Find cosines of smallest and largest angle between beam and detector
								cos_cos_term = cos_this_theta_lab * cos_half_width_sum;
								sin_sin_term = sin_this_theta_lab * sin_half_width_sum;

								if(sin_this_theta_lab < 0)    // i.e. theta_lab < 0
								{
									cos_smallest_gamma = cos_cos_term - sin_sin_term;
									cos_largest_gamma = cos_cos_term + sin_sin_term;
								}
								else
								{
									cos_smallest_gamma = cos_cos_term + sin_sin_term;
									cos_largest_gamma = cos_cos_term - sin_sin_term;
								}

								v_beam_smallest_u = this_lab_velocity * cos_smallest_gamma;
								if(v_beam_smallest_u < v_beam_first)
								{
									u_smallest_squared = v_beam_first_squared + this_lab_velocity_squared -
											2 * v_beam_first * this_lab_velocity * cos_smallest_gamma;
								}
								else
								{
									if(v_beam_smallest_u > v_beam_last)
									{
										u_smallest_squared = v_beam_last_squared + this_lab_velocity_squared -
												2 * v_beam_last * this_lab_velocity * cos_smallest_gamma;
									}
									else  // i.e. v_beam_smallest_u is between v_beam_first and v_beam_last
									{
										u_smallest_squared = this_lab_velocity_squared *
												(1 - cos_smallest_gamma * cos_smallest_gamma);
									}
								}

								u_squared_v_first = v_beam_first_squared + this_lab_velocity_squared -
										2 * v_beam_first * this_lab_velocity * cos_largest_gamma;
								u_squared_v_last = v_beam_last_squared + this_lab_velocity_squared -
										2 * v_beam_last * this_lab_velocity * cos_largest_gamma;


								u_largest_squared = Math.max(u_squared_v_first, u_squared_v_last);

								min_u_squared_this_vel = u_largest_squared;
								max_u_squared_this_vel = u_smallest_squared;

								if((u_min_squared_poe <= u_largest_squared) && (u_max_squared_poe >= u_smallest_squared))
								{
									count_vel++;
									min_lab_vel_num = Math.min(min_lab_vel_num, vel_num);
									max_lab_vel_num = Math.max(max_lab_vel_num, vel_num);

									// Begin moving through each angle:  first the detector angle theta
									det_theta_prob_sum = 0.0f;
									for(det_theta_num = 0; det_theta_num < num_det_ang_segs; det_theta_num++)
									{
										cos_this_theta_det = (float) cos_theta_detector[det_theta_num];
										sin_this_theta_det = (float) sin_theta_detector[det_theta_num];

										lab_vel_times_cos_theta_det = this_lab_velocity * cos_this_theta_det;
										lab_vel_times_sin_theta_det = this_lab_velocity * sin_this_theta_det;

										this_det_theta_2cos_gamma = this_tof_2cos_gamma[det_theta_num];
										this_num_det_phi_segs = (int) (3.0 * det_theta_num + 1);


										deref_sin_phi_det = sin_phi_detector[det_theta_num];
										det_phi_prob_sum = 0.0f;

										for(det_phi_num = 0; det_phi_num < this_num_det_phi_segs; det_phi_num++)
										{
											this_det_phi_2cos_gamma = this_det_theta_2cos_gamma[det_phi_num];
											sin_this_phi_det = deref_sin_phi_det[det_phi_num];

											lab_vel_times_sin_th_det_times_sin_phi_det = lab_vel_times_sin_theta_det * sin_this_phi_det;

											for(bm_vel_num = 0; bm_vel_num < num_beam_vel_segs; bm_vel_num++)
											{
												this_bm_velocity = beam_velocities[bm_vel_num];
												this_bm_vel_squared = bm_vel_squared[bm_vel_num];

												first_two_usqr_terms = this_bm_vel_squared + this_lab_velocity_squared;
												lab_vel_times_bm_vel = this_bm_velocity * this_lab_velocity;

												bm_theta_prob_sum = 0.0f;
												for(bm_theta_num = 0; bm_theta_num < num_beam_ang_segs; bm_theta_num++)
												{
													this_bm_theta_2cos_gamma = this_det_phi_2cos_gamma[bm_theta_num];
													this_num_bm_phi_segs = (int) (3.0 * bm_theta_num + 1);

													sin_this_theta_bm = (float) sin_theta_beam[bm_theta_num];
													cos_this_theta_bm = (float) cos_theta_beam[bm_theta_num];
													deref_sin_phi_bm = sin_phi_beam[bm_theta_num];

													bm_vel_times_sin_theta_bm_times_cos_theta_lab =
															this_bm_velocity * sin_this_theta_bm * cos_this_theta_lab;
													bm_vel_times_sin_theta_bm_times_sin_theta_lab =
															this_bm_velocity * sin_this_theta_bm * sin_this_theta_lab;
													bm_vel_times_cos_theta_bm_times_cos_theta_lab =
															this_bm_velocity * cos_this_theta_bm * cos_this_theta_lab;
													bm_vel_times_cos_theta_bm_times_sin_theta_lab =
															this_bm_velocity * cos_this_theta_bm * sin_this_theta_lab;

													bm_phi_prob_sum = 0.0f;
													for(bm_phi_num = 0; bm_phi_num < this_num_bm_phi_segs; bm_phi_num++)
													{
														u_squared = first_two_usqr_terms - lab_vel_times_bm_vel * this_bm_theta_2cos_gamma[bm_phi_num];
														if((u_squared >= u_min_squared_poe) && (u_squared < u_max_squared_poe))
														{
															energy_point = u_squared * mass_ratio_over_energy_inc - energy_min_over_energy_inc;

															if((energy_point < this_poe_num_points) && (energy_point >= 0))
															{
																min_u_squared_this_vel = Math.min(min_u_squared_this_vel, u_squared);
																max_u_squared_this_vel = Math.max(max_u_squared_this_vel, u_squared);

																int_energy_pt = (int) energy_point;

																lower_energy_amp = this_poe_energy_amp[int_energy_pt];
																energy_amp = lower_energy_amp + (energy_point - int_energy_pt) *
																		((this_poe_energy_amp[int_energy_pt + 1] - lower_energy_amp));


																sin_this_phi_bm = deref_sin_phi_bm[bm_phi_num];

																cos_theta_recoil = lab_vel_times_cos_theta_det -
																		sin_this_phi_bm * bm_vel_times_sin_theta_bm_times_sin_theta_lab
																		- bm_vel_times_cos_theta_bm_times_cos_theta_lab;

																sin_theta_recoil = lab_vel_times_sin_th_det_times_sin_phi_det +
																		bm_vel_times_cos_theta_bm_times_sin_theta_lab -
																		sin_this_phi_bm * bm_vel_times_sin_theta_bm_times_cos_theta_lab;


																u_inverse = (float) (1 / (Math.sqrt(u_squared)));
																cos_theta_polar_major = u_inverse *(cos_theta_recoil * this_cos_polar -
																		sin_theta_recoil * this_sin_polar);

																bm_phi_prob_sum += (energy_amp * u_inverse * (depolar_term_1 +
																		depolar_term_2 * cos_theta_polar_major * cos_theta_polar_major));
																// Last factor (in parentheses) is rearranged form of angular distribution function,
																// i.e. (1 + beta * P2(cos theta)), including contribution from both
																// major and minor axis, as determined by depolarization of laser
																// See derivation on pg 101 of lab notebook entitled SBEAM INFO, Hans Stauffer


															} // End of if statement which MAKES SURE this energy contributes
														}  // End of if statement which screens out energies which won't contribute
														// at this lab velocity
													} // End of sum over all beam phi angles

													bm_theta_prob_sum += (bm_phi_prob_sum / this_num_bm_phi_segs);
													// The prob is the same for all phi angles,
													// so it can be pulled out of the inner sum
												} // End of sum over all beam theta angles
												// Multiply by the probability of this specific beam angle
												det_phi_prob_sum += (bm_theta_prob_sum * /*this_bm_vel_prob*/bm_vel_prob_dist[bm_vel_num]);
											} // End of sum over all possible beam velocities
										} // End of sum over all possible detector phi angles
										det_theta_prob_sum += det_phi_prob_sum / this_num_det_phi_segs;
										// The prob. of each detector phi only
										// depends on which theta annulus we are
										// in, so this can be pulled from the inner sum
									} // End of sum over all possible detector theta angles

									prob_lab_vel_this_chan[vel_num] = det_theta_prob_sum * this_chan_rel_weight *
											this_lab_velocity_squared /** 2*/ * this_chan_mass_ratio /*/ (num_beam_ang_segs * num_det_ang_segs)*/;
									if(is_number_density_calc == false)
									{
										prob_lab_vel_this_chan[vel_num] *= this_lab_velocity;
									}

									this_chan_min_u_squared_array[vel_num] = min_u_squared_this_vel;
									this_chan_max_u_squared_array[vel_num] = max_u_squared_this_vel;
								}
								else
								{
									prob_lab_vel_this_chan[vel_num] = 0.0f;
									this_chan_min_u_squared_array[vel_num] = -1.0f;
									this_chan_max_u_squared_array[vel_num] = -1.0f;
								}

								if((vel_num % 50) == 0)
								{
									percent_done = (float) (100.0 *(tof_num +(amount_per_poe * (count + (amount_per_chan * (chan_num + (vel_num * amount_per_vel_seg)))))) / num_tofs);
									message_dialog.SetPercent((int)percent_done);
								}

							} // End of loop over all possible lab velocities
							// Find a TOF for this channel

							if(min_lab_vel_num == 0)
								min_lab_vel_num++;
							if(max_lab_vel_num == (num_lab_vel_segs - 1))
								max_lab_vel_num--;
							time_array_for_tofs = new float[num_tof_points];
							for(tof_point_num = 0; tof_point_num < num_tof_points; tof_point_num++)
							{
								this_tof_time = starting_time + tof_point_num * dwell_time_micro;

								this_ionizer_count = 0.0f;
								if(this_tof_time != 0.0)
								{
									for(ion_point_num = 0; ion_point_num < num_ionizer_segs; ion_point_num++)
									{
										this_distance = (float) ((flight_length + ionizer_distance[ion_point_num]) * 1.0e4);
										// Factor of 10000 so distance in units of micrometers . velocity in m/s
										this_time_count = 0.0f;
										for(time_seg_num = 0; time_seg_num < 10; time_seg_num++)   // Split this time point into 10 different segments
										{
											this_time_inverse = (float) (1.0 / (this_tof_time + time_seg_num * 0.1 * dwell_time_micro));  // Time in microseconds
											real_lab_vel = this_distance * this_time_inverse;   // Velocity in m/s

											velocity_point = (real_lab_vel - first_lab_vel) * inverse_lab_vel_spacings;
											vel_point_int = (int) velocity_point;

											if((velocity_point < (max_lab_vel_num + 1)) && (velocity_point >= (min_lab_vel_num - 1)))
											{
												lower_vel_prob = prob_lab_vel_this_chan[vel_point_int];

												this_time_count += (lower_vel_prob + (velocity_point - vel_point_int) *
														(prob_lab_vel_this_chan[vel_point_int + 1] -
																lower_vel_prob)) * this_time_inverse;

											}
										}
										this_ionizer_count += this_time_count * ionizer_prob_dist[ion_point_num];
										//System.out.println("CHECK:\t" + this_time_count +":"+ionizer_prob_dist[ion_point_num]);

									}
								}
								time_array_for_tofs[tof_point_num] = this_tof_time;
								tof_array_this_channel[tof_point_num] = (float) (0.1 * this_ionizer_count);
								tof_array_poe[tof_point_num] += (0.1 * this_ionizer_count);  // Add tof data from each channel!
								//System.out.println("THIS: " + tof_array_poe[tof_point_num]);

								total_tof_counts[tof_point_num] += (0.1 * this_ionizer_count);
							}
						} // End of loop over all channels for this P(E)
						count++;
					} // End of if statement for whether this P(E) should be included in the calculation
					else
					{
						prob_lab_vel_this_tof[poe_num] = null;
						tof_colors[poe_num] = null;
						tof_arrays[poe_num] = null;
						number_channels[poe_num] = 0;
					}

				} // End of loop over all P(E)'s
				this_TOF.num_current_poes = num_total_poes;
				this_TOF.number_channels = number_channels;
				this_TOF.individual_tofs = tof_arrays;
				this_TOF.channel_counts = total_tof_counts;
				this_TOF.tof_flight_time = time_array_for_tofs;
				this_TOF.num_tot_channels = num_tof_points;
				this_TOF.tof_colors = tof_colors;
				this_TOF.number_included_poes = num_included_poes;
				this_TOF.SetIonFlightTime(0.0f);   // Sets flight const to zero and calculates real flight times
				this_TOF.ion_m_e = ion_m_e;
				this_TOF.lab_angle = tof_lab_angles[tof_num];
				this_TOF.beam_vel_alpha = alpha;
				this_TOF.beam_vel_speed_ratio = speed_ratio;
				this_TOF.num_beam_vel_segs = num_beam_vel_segs;
				this_TOF.is_ionization_gaussian = is_ionizer_gaussian;
				this_TOF.num_ionization_segs = num_ionizer_segs;
				this_TOF.is_number_density_calc = is_number_density_calc;
				this_TOF.flight_length = flight_length;
				this_TOF.beam_angle_width = beam_angular_width;
				this_TOF.detector_angle_width = detector_angular_width;
				this_TOF.ionizer_length = ionizer_length;
				this_TOF.minimum_lab_vel = min_lab_velocity;
				this_TOF.maximum_lab_vel = max_lab_velocity;
				this_TOF.num_lab_vel_segs = num_lab_vel_segs;
				this_TOF.polarized_laser = tof_is_polarized[tof_num];
				this_TOF.polarization_angle = tof_polarization_angles[tof_num];
				this_TOF.depolarization = depolarization[tof_num];
				this_TOF.title = tof_title;
				this_TOF.num_beam_angle_segs = num_beam_ang_segs;
				this_TOF.num_det_angle_segs = num_det_ang_segs;
				this_TOF.calc_data_for_poes = data_for_poes;
				this_TOF.associated_calc_number = CalcNumber;
			} // End of loop over all TOF's



			message_dialog.CloseWindow();
			return new_calc_tofs;
	}
	
	
	public void CalcTOFDeltaFunctions(int poe_number, POEData poe, int poe_point){
		/*float time;
		   clock_t start, end;
		    start = clock();     */

		int i;
		int tof_num, chan_num, vel_num, bm_theta_num, bm_phi_num;
		int bm_vel_num, det_theta_num, det_phi_num;
		int this_num_channels, this_num_det_phi_segs, this_num_bm_phi_segs;
		int int_energy_pt, tof_point_num, ion_point_num, time_seg_num, vel_point_int;
		int this_poe_num_points, min_lab_vel_num, max_lab_vel_num;

		float cos_this_theta_lab, sin_this_theta_lab;
		float cos_this_theta_bm, sin_this_theta_bm;
		float sin_this_phi_bm;
		float sin_this_phi_det;
		float cos_theta_polar_major;

		float poe_energy_inc_inverse, first_two_usqr_terms, lab_vel_times_bm_vel;
		float u_squared, u_inverse;

		float energy_point, energy_amp;

		float this_poe_beta;
		float this_chan_mass_ratio, this_chan_inverse_mass_ratio, this_chan_rel_weight;
		float this_poe_energy_min, this_poe_energy_max, this_poe_energy_increment;
		float this_lab_velocity, this_lab_velocity_squared;
		float this_bm_velocity, this_bm_vel_squared;


		float[] this_poe_mass_ratios, this_poe_rel_weights;




		float cos_theta_recoil, sin_theta_recoil;
		float[] deref_sin_phi_det;
		float[] deref_sin_phi_bm;

		float this_depolar_minor, depolar_difference;
		float this_cos_polar, this_sin_polar;

		float cos_this_theta_det, sin_this_theta_det;






		float[][][][] this_tof_2cos_gamma;
		float[][][] this_det_theta_2cos_gamma;
		float[][] this_det_phi_2cos_gamma;
		float[] this_bm_theta_2cos_gamma;

		float[][] prob_lab_vel;
		float[] prob_lab_vel_this_channel;

		float[][][] all_tofs;
		float[][] calculated_delta_tofs;
		float[] calculated_delta_tof_this_channel;
		float[] total_delta_tof;

		float first_lab_vel;
		float inverse_lab_vel_spacings;
		float velocity_point, lower_vel_prob, this_tof_time, this_ionizer_count, this_distance;
		float this_time_count, this_time_inverse, real_lab_vel;

		POECalcData current_poe_data = data_for_poes[poe_number];

		float u_min_squared_poe, u_max_squared_poe;

		float lab_vel_times_cos_theta_det, bm_vel_times_cos_theta_bm_times_cos_theta_lab;
		float bm_vel_times_cos_theta_bm_times_sin_theta_lab, lab_vel_times_sin_theta_det;
		float lab_vel_times_sin_th_det_times_sin_phi_det;
		float bm_vel_times_sin_theta_bm_times_cos_theta_lab, bm_vel_times_sin_theta_bm_times_sin_theta_lab;

		float dwell_time_micro;

		float det_phi_prob_sum, det_theta_prob_sum, bm_theta_prob_sum, bm_phi_prob_sum;

		float[][] this_poe_min_u_squared_array, this_poe_max_u_squared_array;
		float[] this_chan_min_u_squared_array, this_chan_max_u_squared_array;

		int count_vel = 0;


		float energy_min_over_energy_inc, mass_ratio_over_energy_inc;
		float depolar_term_1, depolar_term_2;

		float poe_amplitude = poe.poe_amplitudes[poe_point];
		float num_poe_points = poe.poe_amplitudes.length;
		float[] time_array_for_tofs;  // Can just store this in the Calc_data

		int next_poe_point, previous_poe_point;

		if(poe_point == (num_poe_points - 1))
		{
			next_poe_point = poe_point;
		}
		else
		{
			next_poe_point = poe_point + 1;
		}

		if(poe_point == 0)
		{
			previous_poe_point = poe_point;
		}
		else
		{
			previous_poe_point = poe_point - 1;
		}


		if(num_tofs == 0)
		{
			return;
		}

		TOFData this_TOF;


		first_lab_vel = lab_velocities[0];
		inverse_lab_vel_spacings = (float) (1.0 / (lab_velocities[1] - first_lab_vel));

		dwell_time_micro = (ending_time - starting_time) / (num_tof_points - 1);

		float lower_energy, point_energy, upper_energy;

		this_num_channels = current_poe_data.num_channels;


		all_tofs = new float[num_tofs][][];
		// Do this for every TOF which is to be calculated.
		for(tof_num = 0; tof_num < num_tofs; tof_num++)
		{
			this_TOF = calculated_tofs[tof_num];
			this_tof_2cos_gamma = TOF_2cos_gamma[tof_num];

			this_depolar_minor = depolar_minor[tof_num];

			depolar_difference = depolar_major[tof_num] - this_depolar_minor;

			this_cos_polar = cos_polar_angle[tof_num];
			this_sin_polar = sin_polar_angle[tof_num];

			cos_this_theta_lab = cos_theta_lab[tof_num];
			sin_this_theta_lab = sin_theta_lab[tof_num];




			prob_lab_vel = new float[this_num_channels][];


			all_tofs[tof_num] = new float[this_num_channels + 1][];
			calculated_delta_tofs = all_tofs[tof_num];
			calculated_delta_tofs[0] = new float[num_tof_points];
			total_delta_tof = calculated_delta_tofs[0];


			// Initially set all points in the total tof to zero
			for(i = 0; i < num_tof_points; i++)
				total_delta_tof[i] = 0.0f;

			this_poe_energy_min = poe.min_energy;
			this_poe_energy_max = poe.max_energy;
			//tof_colors[poe_num] = poe.GetPOEColor();

			this_poe_num_points = poe.num_points;
			this_poe_energy_increment = (this_poe_energy_max - this_poe_energy_min) / (this_poe_num_points - 1);
			poe_energy_inc_inverse = (float) (1.0 / this_poe_energy_increment);

			energy_min_over_energy_inc = this_poe_energy_min * poe_energy_inc_inverse;

			point_energy = this_poe_energy_min + this_poe_energy_increment * (poe_point);
			lower_energy = point_energy - this_poe_energy_increment;
			upper_energy = point_energy + this_poe_energy_increment;

			this_poe_beta = current_poe_data.beta_param;
			this_poe_mass_ratios = current_poe_data.mass_ratio;
			this_poe_rel_weights = current_poe_data.rel_weight;

			this_poe_min_u_squared_array = min_u_squared_array[tof_num][poe_number];
			this_poe_max_u_squared_array = max_u_squared_array[tof_num][poe_number];

			depolar_term_1 = (float) (1.0 + 1.5 * this_poe_beta * this_depolar_minor - 0.5 * this_poe_beta);
			depolar_term_2 = (float) (1.5 * this_poe_beta * depolar_difference);
			for(chan_num = 0; chan_num < this_num_channels; chan_num++)
			{
				prob_lab_vel[chan_num] = new float[num_lab_vel_segs];
				calculated_delta_tofs[chan_num + 1] = new float[num_tof_points];
				calculated_delta_tof_this_channel = calculated_delta_tofs[chan_num + 1];
				prob_lab_vel_this_channel = prob_lab_vel[chan_num];

				this_chan_mass_ratio = this_poe_mass_ratios[chan_num];
				this_chan_inverse_mass_ratio = 1 / this_chan_mass_ratio;
				this_chan_rel_weight = this_poe_rel_weights[chan_num];

				mass_ratio_over_energy_inc = this_chan_mass_ratio * poe_energy_inc_inverse;

				u_min_squared_poe = lower_energy * this_chan_inverse_mass_ratio;
				u_max_squared_poe = upper_energy * this_chan_inverse_mass_ratio;

				this_chan_min_u_squared_array = this_poe_min_u_squared_array[chan_num];
				this_chan_max_u_squared_array = this_poe_max_u_squared_array[chan_num];

				min_lab_vel_num = num_lab_vel_segs;
				max_lab_vel_num = 0;
				for(vel_num = 0; vel_num < num_lab_vel_segs; vel_num++)
				{
					this_lab_velocity = lab_velocities[vel_num];

					this_lab_velocity_squared = lab_vel_squared[vel_num];

					if((u_min_squared_poe <= this_chan_max_u_squared_array[vel_num]) &&
							(u_max_squared_poe >= this_chan_min_u_squared_array[vel_num]))
					{
						count_vel++;

						min_lab_vel_num = Math.min(min_lab_vel_num, vel_num);
						max_lab_vel_num = Math.max(max_lab_vel_num, vel_num);

						// Begin moving through each angle:  first the detector angle theta
						//counter++;
						det_theta_prob_sum = 0.0f;
						for(det_theta_num = 0; det_theta_num < num_det_ang_segs; det_theta_num++)
						{
							cos_this_theta_det = (float) cos_theta_detector[det_theta_num];
							sin_this_theta_det = (float) sin_theta_detector[det_theta_num];

							lab_vel_times_cos_theta_det = this_lab_velocity * cos_this_theta_det;
							lab_vel_times_sin_theta_det = this_lab_velocity * sin_this_theta_det;

							this_det_theta_2cos_gamma = this_tof_2cos_gamma[det_theta_num];
							this_num_det_phi_segs = (int) (3.0 * det_theta_num + 1);

							deref_sin_phi_det = sin_phi_detector[det_theta_num];

							det_phi_prob_sum = 0.0f;

							for(det_phi_num = 0; det_phi_num < this_num_det_phi_segs; det_phi_num++)
							{
								this_det_phi_2cos_gamma = this_det_theta_2cos_gamma[det_phi_num];
								sin_this_phi_det = deref_sin_phi_det[det_phi_num];

								lab_vel_times_sin_th_det_times_sin_phi_det = lab_vel_times_sin_theta_det * sin_this_phi_det;


								for(bm_vel_num = 0; bm_vel_num < num_beam_vel_segs; bm_vel_num++)
								{
									this_bm_velocity = beam_velocities[bm_vel_num];
									this_bm_vel_squared = bm_vel_squared[bm_vel_num];

									first_two_usqr_terms = this_bm_vel_squared + this_lab_velocity_squared;
									lab_vel_times_bm_vel = this_bm_velocity * this_lab_velocity;

									bm_theta_prob_sum = 0.0f;
									for(bm_theta_num = 0; bm_theta_num < num_beam_ang_segs; bm_theta_num++)
									{
										this_bm_theta_2cos_gamma = this_det_phi_2cos_gamma[bm_theta_num];
										this_num_bm_phi_segs = (int) (3.0 * bm_theta_num + 1);

										sin_this_theta_bm = (float) sin_theta_beam[bm_theta_num];
										cos_this_theta_bm = (float) cos_theta_beam[bm_theta_num];
										deref_sin_phi_bm = sin_phi_beam[bm_theta_num];


										bm_vel_times_sin_theta_bm_times_cos_theta_lab =
												this_bm_velocity * sin_this_theta_bm * cos_this_theta_lab;
										bm_vel_times_sin_theta_bm_times_sin_theta_lab =
												this_bm_velocity * sin_this_theta_bm * sin_this_theta_lab;
										bm_vel_times_cos_theta_bm_times_cos_theta_lab =
												this_bm_velocity * cos_this_theta_bm * cos_this_theta_lab;
										bm_vel_times_cos_theta_bm_times_sin_theta_lab =
												this_bm_velocity * cos_this_theta_bm * sin_this_theta_lab;

										bm_phi_prob_sum = 0.0f;
										for(bm_phi_num = 0; bm_phi_num < this_num_bm_phi_segs; bm_phi_num++)
										{
											u_squared = first_two_usqr_terms - lab_vel_times_bm_vel * this_bm_theta_2cos_gamma[bm_phi_num];
											if((u_squared >= u_min_squared_poe) && (u_squared < u_max_squared_poe))
											{
												energy_point = u_squared * mass_ratio_over_energy_inc - energy_min_over_energy_inc;
												if((energy_point <= next_poe_point) && (energy_point >= previous_poe_point))
												{
													int_energy_pt = (int) energy_point;

													// Assume P(point) = 1 and P(surrounding points) = 0, with linear interpolation
															if(energy_point < poe_point)
															{
																energy_amp = energy_point - int_energy_pt;
															}
															else
															{
																energy_amp = 1 + int_energy_pt - energy_point;
															}
															sin_this_phi_bm = deref_sin_phi_bm[bm_phi_num];
															u_inverse = (float) (1 / (Math.sqrt(u_squared)));

															cos_theta_recoil = lab_vel_times_cos_theta_det -
																	sin_this_phi_bm * bm_vel_times_sin_theta_bm_times_sin_theta_lab
																	- bm_vel_times_cos_theta_bm_times_cos_theta_lab;

															sin_theta_recoil = lab_vel_times_sin_th_det_times_sin_phi_det +
																	bm_vel_times_cos_theta_bm_times_sin_theta_lab -
																	sin_this_phi_bm * bm_vel_times_sin_theta_bm_times_cos_theta_lab;


															cos_theta_polar_major = u_inverse *(cos_theta_recoil * this_cos_polar -
																	sin_theta_recoil * this_sin_polar);


															bm_phi_prob_sum += (energy_amp * u_inverse * (depolar_term_1 +
																	depolar_term_2 * cos_theta_polar_major * cos_theta_polar_major));

												}
											}  // End of if statement which screens out energies which won't contribute
											// at this lab velocity
										} // End of sum over all beam phi angles

										bm_theta_prob_sum += (bm_phi_prob_sum / this_num_bm_phi_segs);
										// The prob is the same for all phi angles,
										// so it can be pulled out of the inner sum
									} // End of sum over all beam theta angles

									// Multiply by the probability of this specific beam angle
									det_phi_prob_sum += (bm_theta_prob_sum * bm_vel_prob_dist[bm_vel_num]);
								} // End of sum over all possible beam velocities
							} // End of sum over all possible detector phi angles
							det_theta_prob_sum += det_phi_prob_sum / this_num_det_phi_segs;
							// The prob. of each detector phi only
							// depends on which theta annulus we are
							// in, so this can be pulled from the inner sum
						} // End of sum over all possible detector theta angles

						prob_lab_vel_this_channel[vel_num] = det_theta_prob_sum * this_chan_rel_weight *
								this_lab_velocity_squared /** 2*/ * this_chan_mass_ratio /*/ (num_beam_ang_segs * num_det_ang_segs)*/;
						if(is_number_density_calc == false)
						{
							prob_lab_vel_this_channel[vel_num] *= this_lab_velocity;
						}
					}
					else
					{
						prob_lab_vel_this_channel[vel_num] = 0.0f;
					}
				} // End of loop over all possible lab velocities


				// Find the delta TOF for this channel

				if(min_lab_vel_num == 0)
					min_lab_vel_num++;
				if(max_lab_vel_num == (num_lab_vel_segs - 1))
					max_lab_vel_num--;

				time_array_for_tofs = this_TOF.actual_flight_time_micro;
				for(tof_point_num = 0; tof_point_num < num_tof_points; tof_point_num++)
				{
					this_tof_time = time_array_for_tofs[tof_point_num];//starting_time + tof_point_num * dwell_time_micro;

					this_ionizer_count = 0.0f;
					if(this_tof_time != 0.0)
					{
						for(ion_point_num = 0; ion_point_num < num_ionizer_segs; ion_point_num++)
						{
							this_distance = (float) ((flight_length + ionizer_distance[ion_point_num]) * 1.0e4);
							// Factor of 10000 so distance in units of micrometers . velocity in m/s


							this_time_count = 0.0f;
							for(time_seg_num = 0; time_seg_num < 10; time_seg_num++)   // Split this time point into 10 different segments
							{
								this_time_inverse = (float) (1.0 / (this_tof_time + time_seg_num * 0.1 * dwell_time_micro));  // Time in microseconds
								real_lab_vel = this_distance * this_time_inverse;   // Velocity in m/s

								velocity_point = (real_lab_vel - first_lab_vel) * inverse_lab_vel_spacings;
								vel_point_int = (int) velocity_point;

								if((velocity_point < (max_lab_vel_num + 1)) && (velocity_point >= (min_lab_vel_num - 1)))
								{
									lower_vel_prob = prob_lab_vel_this_channel[vel_point_int];

									this_time_count += (lower_vel_prob + (velocity_point - vel_point_int) *
											(prob_lab_vel_this_channel[vel_point_int + 1] -
													lower_vel_prob)) * this_time_inverse;
								}
							}
							this_ionizer_count += this_time_count * ionizer_prob_dist[ion_point_num];
						}
					}

					calculated_delta_tof_this_channel[tof_point_num] = (float) (0.1 * this_ionizer_count);
					total_delta_tof[tof_point_num] += (0.1 * this_ionizer_count);  // Add tof data from each channel!


				}
			} // End of loop over all channels

			this_TOF.SetDeltaTOFArrays(calculated_delta_tofs, poe_number,
					poe_amplitude, this_poe_energy_increment);
			// Delete prob_lab_vel array    
		} // End of loop over all TOF's

		/*end = clock();
		   time = (end - start)/CLK_TCK;
		   time *= 1.0;

		   end = clock();*/

		return;
	}

}
