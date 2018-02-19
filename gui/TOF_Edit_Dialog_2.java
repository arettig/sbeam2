package sbeam2.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dialog.ModalityType;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;

import sbeam2.POECalcData;
import sbeam2.TOFData;

public class TOF_Edit_Dialog_2 extends JDialog implements ActionListener {

	public JFormattedTextField tof_title_edit;

	public JLabel calc_title_static, poe_ion_chan_static, poe_beta_static;
	public JLabel ion_chan_mass1_static, ion_chan_mass2_static,
			ion_chan_weight_static;
	public JLabel det_scheme_static, flight_length_static, bm_ang_static;
	public JLabel det_ang_static, ion_me_static, bm_segs_static,
			det_segs_static;
	public JLabel lab_ang_static, alpha_static, speed_ratio_static,
			bm_vel_segs_static;
	public JLabel ion_dist_static, ion_segs_static, lab_vel_min_static,
			lab_vel_max_static;
	public JLabel lab_vel_segs_static, laser_polar_static, polar_ang_static,
			depol_static;
	public JLabel start_time_static, end_time_static, num_time_points_static;
	public JLabel ion_len_static;

	public JList<String> poe_list_box, chan_list_box;

	public JCheckBox detached_check_box;
	public JButton ok, cancel;


	public String tof_title;
	public String calc_title;
	public String[] poe_title_array;
	public POECalcData[] poe_calc_info;

	public boolean is_number_density, is_gaussian_ionization, is_polarized,
			is_detached;
	public float flight_len, beam_angle, detect_angle, lab_angle, ion_m_e;
	public float polar_angle, depolarization;
	public float alpha_param, speed_ratio, min_lab_vel, max_lab_vel;
	public float start_time, end_time, ionizer_len;

	public int num_beam_ang_segs, num_det_ang_segs, num_ionizer_segs,
			num_beam_vel_segs;
	public int num_lab_vel_segs, num_time_points;
	public int number_of_poes;

	public int[] poe_number_array;

	public int current_chosen_poe_index;
	public MainFrame parent;
	public boolean ID;

	public TOF_Edit_Dialog_2(MainFrame parent, TOFData tof) {
		// TODO Auto-generated constructor stub
		this.parent = parent;
		this.setTitle("Time of Flight Info for Calculated TOF:");
		tof_title_edit = new JFormattedTextField();

		calc_title_static = new JLabel();
		poe_ion_chan_static = new JLabel();
		poe_beta_static = new JLabel();
		ion_chan_mass1_static = new JLabel();
		ion_chan_mass2_static = new JLabel();
		ion_chan_weight_static = new JLabel();
		det_scheme_static = new JLabel();
		flight_length_static = new JLabel();
		bm_ang_static = new JLabel();
		det_ang_static = new JLabel();
		ion_me_static = new JLabel();
		bm_segs_static = new JLabel();
		det_segs_static = new JLabel();
		lab_ang_static = new JLabel();
		alpha_static = new JLabel();
		speed_ratio_static = new JLabel();
		bm_vel_segs_static = new JLabel();
		ion_dist_static = new JLabel();
		ion_segs_static = new JLabel();
		lab_vel_min_static = new JLabel();
		lab_vel_max_static = new JLabel();
		lab_vel_segs_static = new JLabel();
		laser_polar_static = new JLabel();
		polar_ang_static = new JLabel();
		depol_static = new JLabel();
		start_time_static = new JLabel();
		end_time_static = new JLabel();
		num_time_points_static = new JLabel();
		ion_len_static = new JLabel();
		ok = new JButton("ok");
		cancel = new JButton("cancel");

		poe_list_box = new JList<String>(new DefaultListModel<String>());
		chan_list_box = new JList<String>(new DefaultListModel<String>());

		detached_check_box = new JCheckBox();

		tof_title = "";
		poe_number_array = null;

		current_chosen_poe_index = -1;
		
		
		tof_title = tof.title;
		number_of_poes = tof.num_current_poes;
		is_number_density = tof.is_number_density_calc;
		flight_len = tof.flight_length;
		beam_angle = tof.beam_angle_width;
		detect_angle = tof.detector_angle_width;
		ion_m_e = tof.ion_m_e;
		num_beam_ang_segs = tof.num_beam_angle_segs;
		num_det_ang_segs = tof.num_det_angle_segs;
		lab_angle = tof.lab_angle;
		alpha_param = tof.beam_vel_alpha;
		speed_ratio = tof.beam_vel_speed_ratio;
		num_beam_vel_segs = tof.num_beam_vel_segs;
		is_gaussian_ionization = tof.is_ionization_gaussian;
		num_ionizer_segs = tof.num_ionization_segs;
		min_lab_vel = tof.minimum_lab_vel;
		max_lab_vel = tof.maximum_lab_vel;
		num_lab_vel_segs = tof.num_lab_vel_segs;
		is_polarized = tof.polarized_laser;
		start_time = tof.actual_flight_time_micro[0];
		end_time = tof.actual_flight_time_micro[tof.actual_flight_time_micro.length-1];
		num_time_points = tof.actual_flight_time_micro.length;
		ionizer_len = tof.ionizer_length;
	}

	public void SetTOFTitle(String input_text) {
		tof_title = input_text;
	}

	public void SetCalcTitle(String input_text) {
		calc_title = input_text;
	}

	public void SetPOECalcData(POECalcData[] calc_info) {
		poe_calc_info = calc_info;
	}

	public void SetInstParams(boolean IsNumDensity, float flt_len,
			float bm_ang, float det_ang, float ion_len) {
		is_number_density = IsNumDensity;
		flight_len = flt_len;
		beam_angle = bm_ang;
		detect_angle = det_ang;
		ionizer_len = ion_len;
	}

	public void SetMainCalcParams(float lab_ang, float ion_me, int bm_segs,
			int det_segs) {
		lab_angle = lab_ang;
		ion_m_e = ion_me;
		num_beam_ang_segs = bm_segs;
		num_det_ang_segs = det_segs;
	}

	public void SetIonInfo(boolean is_gaussian, int num_ion_segs) {
		is_gaussian_ionization = is_gaussian;
		num_ionizer_segs = num_ion_segs;
	}

	public void SetPolarInfo(boolean is_polar, float polar_ang, float depol) {
		is_polarized = is_polar;
		polar_angle = polar_ang;
		depolarization = depol;
	}

	public void SetBmParams(float alpha, float spd_rat, int num_bm_segs) {
		alpha_param = alpha;
		speed_ratio = spd_rat;
		num_beam_vel_segs = num_bm_segs;
	}

	public void SetLabVelParams(float min_vel, float max_vel, int num_lv_segs) {
		min_lab_vel = min_vel;
		max_lab_vel = max_vel;
		num_lab_vel_segs = num_lv_segs;
	}

	public void SetTimeInfo(float start, float end, int num_points) {
		start_time = start;
		end_time = end;
		num_time_points = num_points;
	}

	public void SetPOETitleArray(int num_total_poes, String[] titles) {
		number_of_poes = num_total_poes;
		poe_title_array = titles;
	}

	public void SetDetached(boolean t_or_f) {
		is_detached = t_or_f;
	}

	public String GetTOFTitle() {
		return tof_title;
	}

	private void SetupWindow(){
		ok.addActionListener(this);
		cancel.addActionListener(this);
		poe_number_array = new int[number_of_poes];

		tof_title_edit.setText(tof_title);

		calc_title_static.setText(calc_title);


		poe_ion_chan_static.setText("" + number_of_poes);
		
		if (is_number_density) {
			det_scheme_static.setText("Ion Number Density");
		} else {
			det_scheme_static.setText("Ion Flux");
		}

		flight_length_static.setText("" + "" + flight_len);

		bm_ang_static.setText("" + beam_angle);

		det_ang_static.setText("" + detect_angle);

		ion_me_static.setText("" + ion_m_e);

		bm_segs_static.setText("" + num_beam_ang_segs);

		det_segs_static.setText("" + num_det_ang_segs);

		lab_ang_static.setText("" + lab_angle);

		alpha_static.setText("" + alpha_param);

		speed_ratio_static.setText("" + speed_ratio);

		bm_vel_segs_static.setText("" + num_beam_vel_segs);
		
		if (is_gaussian_ionization) {
			ion_dist_static.setText("" + "Is Gaussian.");
		} else {
			ion_dist_static.setText("" + "Is Not Gaussian.");
		}

		ion_segs_static.setText("" + num_ionizer_segs);

		lab_vel_min_static.setText("" + min_lab_vel);

		lab_vel_max_static.setText("" + max_lab_vel);

		lab_vel_segs_static.setText("" + num_lab_vel_segs);
		
		if (is_polarized) {
			laser_polar_static.setText("" + "Laser IS Polarized.");

			polar_ang_static.setText("" + polar_angle);

			depol_static.setText("" + depolarization);
		} else {
			laser_polar_static.setText("" + "Laser IS NOT Polarized.");
			polar_ang_static.setText("" + " ");
			depol_static.setText("" + " ");
		}

		start_time_static.setText("" + start_time);

		end_time_static.setText("" + end_time);
		
		num_time_points_static.setText("" + num_time_points);

		ion_len_static.setText("" + ionizer_len);

		((DefaultListModel<String>) poe_list_box.getModel()).clear();

		// Fill information in the list boxes
		int count = 0;

		for (int i = 0; i < number_of_poes; i++) {
			if (poe_calc_info[i].is_included) {
				poe_number_array[count] = i;
				((DefaultListModel<String>) poe_list_box.getModel())
						.addElement(poe_title_array[i]);
				count++;
			}
		}
		
		poe_list_box.setSelectedIndex(0);
		CmPOETitleChosen();

		detached_check_box.setEnabled(false);
		if (is_detached)
			detached_check_box.setEnabled(true);
		else
			detached_check_box.setSelected(false);
		
		
		//organize window
		JPanel titlePane = new JPanel();
		titlePane.setBorder(BorderFactory.createTitledBorder("Title of TOF:"));		
		titlePane.setLayout(new BoxLayout(titlePane, BoxLayout.Y_AXIS));
		titlePane.add(tof_title_edit);
		titlePane.add(getLabelledPanel(detached_check_box, "Detached from Calculation"));

		
		JPanel pePane = new JPanel();
		pePane.setBorder(BorderFactory.createTitledBorder("P(E) Information:"));		
		pePane.setLayout(new BoxLayout(pePane, BoxLayout.Y_AXIS));
		pePane.add(getLabelledPanel(poe_ion_chan_static, "Number of ionization channels: "));
		pePane.add(getLabelledPanel(poe_beta_static, "Anisotropy parameter, β, for fragment angular distribution (-1 - 2): "));
		//polPane.add(getLabelledPanel(degree_polarization_edit,
				//"Degrees Polarization"));
		//polPane.add(change_polarization);
		
		
		JPanel instrPane = new JPanel();
		instrPane.setBorder(BorderFactory.createTitledBorder("Instrumental Parameters"));		
		instrPane.setLayout(new BoxLayout(instrPane, BoxLayout.Y_AXIS));
		instrPane.add(getLabelledPanel(det_scheme_static, "Detection scheme: "));
		instrPane.add(getLabelledPanel(flight_length_static, "Flight Length of Main Chamber (cm): "));
		instrPane.add(getLabelledPanel(ion_dist_static, "Effective Length of Ionizer (cm): "));
		instrPane.add(getLabelledPanel(bm_ang_static, "Angular half width of beam at interaction region: "));
		instrPane.add(getLabelledPanel(bm_ang_static, "Angular half width of detector relative to interaction region: "));

		
		JPanel calcPane = new JPanel();
		calcPane.setBorder(BorderFactory.createTitledBorder("Calculation Parameters"));		
		calcPane.setLayout(new BoxLayout(calcPane, BoxLayout.X_AXIS));
		
		//left side
		JPanel calcPaneLeft = new JPanel();
		calcPaneLeft.setLayout(new BoxLayout(calcPaneLeft, BoxLayout.Y_AXIS));
		calcPaneLeft.add(getLabelledPanel(ion_me_static, "Ion m/e for calculation (amu): "));
		calcPaneLeft.add(getLabelledPanel(bm_segs_static, "Number of beam angle segments: "));
		calcPaneLeft.add(getLabelledPanel(ion_len_static, "Number of detector angle segments: "));
		calcPaneLeft.add(getLabelledPanel(lab_ang_static, "Lab angle (degrees): "));
		JPanel cPanL1 = new JPanel();
		cPanL1.setBorder(BorderFactory.createTitledBorder("Ionization Distribution"));
		cPanL1.setLayout(new BoxLayout(cPanL1, BoxLayout.Y_AXIS));
		cPanL1.add(getLabelledPanel(ion_dist_static, "Ionization Profile: "));
		cPanL1.add(getLabelledPanel(ion_segs_static, "Number of Ionizer Segments: "));
		calcPaneLeft.add(cPanL1);
		JPanel cPanL2 = new JPanel();
		cPanL2.setBorder(BorderFactory.createTitledBorder("Polarization"));
		cPanL2.setLayout(new BoxLayout(cPanL2, BoxLayout.Y_AXIS));
		cPanL2.add(getLabelledPanel(laser_polar_static, "Polar:"));
		cPanL2.add(getLabelledPanel(polar_ang_static, "Polarization angle (degrees): "));
		cPanL2.add(getLabelledPanel(depol_static, "Depolarization (0-1): "));
		calcPaneLeft.add(cPanL2);
		calcPane.add(calcPaneLeft);

		//right side
		JPanel calcPaneRight = new JPanel();
		calcPaneRight.setLayout(new BoxLayout(calcPaneRight, BoxLayout.Y_AXIS));
		JPanel cPanR1 = new JPanel();
		cPanR1.setBorder(BorderFactory.createTitledBorder("Beam Parameters"));
		cPanR1.setLayout(new BoxLayout(cPanR1, BoxLayout.Y_AXIS));
		cPanR1.add(getLabelledPanel(alpha_static, "Beam alpha parameter (m/s): "));
		cPanR1.add(getLabelledPanel(speed_ratio_static, "Beam speed ratio parameter: "));
		cPanR1.add(getLabelledPanel(bm_vel_segs_static, "Number of beam velocity segments: "));
		calcPaneRight.add(cPanR1);
		JPanel cPanR2 = new JPanel();
		cPanR2.setBorder(BorderFactory.createTitledBorder("Lab Velocity Parameters"));	
		cPanR2.setLayout(new BoxLayout(cPanR2, BoxLayout.Y_AXIS));
		cPanR2.add(getLabelledPanel(lab_vel_min_static, "Minimum (m/s): "));
		cPanR2.add(getLabelledPanel(lab_vel_max_static, "Maximum (m/s): "));
		cPanR2.add(getLabelledPanel(lab_vel_segs_static, "Number of lab velocity segments: "));
		calcPaneRight.add(cPanR2);
		JPanel cPanR3 = new JPanel();
		cPanR3.setBorder(BorderFactory.createTitledBorder("Time Range Information"));	
		cPanR3.setLayout(new BoxLayout(cPanR3, BoxLayout.Y_AXIS));
		cPanR3.add(getLabelledPanel(start_time_static, "TOF starting time (μs): "));
		cPanR3.add(getLabelledPanel(end_time_static, "TOF ending time (μs): "));
		cPanR3.add(getLabelledPanel(num_time_points_static, "Number of points: "));
		calcPaneRight.add(cPanR3);
		calcPane.add(calcPaneRight);
		
		JPanel finPane = new JPanel();
		finPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
		finPane.setLayout(new BoxLayout(finPane, BoxLayout.X_AXIS));
		finPane.add(ok);
		finPane.add(cancel);

		
		
		
		this.getContentPane().add(titlePane, BorderLayout.NORTH);
		this.getContentPane().add(pePane, BorderLayout.CENTER);
		this.getContentPane().add(calcPane, BorderLayout.SOUTH);
		this.getContentPane().add(finPane, BorderLayout.SOUTH);
		
		//ok.addActionListener(this);
		//cancel.addActionListener(this);
		
		
	}
	
	private JPanel getLabelledPanel(Component c, String s) {
		JPanel tempPan = new JPanel();
		tempPan.setLayout(new BoxLayout(tempPan, BoxLayout.X_AXIS));
		tempPan.add(new JLabel(s));
		tempPan.add(c);
		return tempPan;
	}

	public void Execute(){
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		SetupWindow();
		this.pack();
		this.setResizable(true);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setVisible(true);
	}
	
	private void CmOk() {
		tof_title = tof_title_edit.getText();
		ID = true;
		this.dispose();
	}

	private void CmCancel() {
		ID = false;
		this.dispose();
	}

	private void CmPOETitleChosen() {

		int chosen_index, chosen_poe_num, i, number_ionization_channels;

		String channel_name_string;
		String temp_string;

		chosen_index = poe_list_box.getSelectedIndex();
		if (current_chosen_poe_index != chosen_index) {
			current_chosen_poe_index = chosen_index;
			temp_string = "";
			chosen_poe_num = poe_number_array[chosen_index];
			number_ionization_channels = poe_calc_info[chosen_poe_num].num_channels;

			poe_ion_chan_static.setText("" + number_ionization_channels);
			poe_beta_static.setText(""
					+ poe_calc_info[chosen_poe_num].beta_param);

			// Add strings to channel list box
			((DefaultListModel<String>) chan_list_box.getModel()).clear();
			for (i = 0; i < number_ionization_channels; i++) {
				channel_name_string = "Channel #";
				channel_name_string += (i + 1);

				((DefaultListModel<String>) chan_list_box.getModel())
						.addElement(channel_name_string);
			}
			chan_list_box.setSelectedIndex(0);
			CmChannelChosen();
		}
	}

	private void CmChannelChosen() {
		int chosen_index, chosen_poe_num;
		String temp_string = "";

		chosen_index = chan_list_box.getSelectedIndex();
		chosen_poe_num = poe_number_array[current_chosen_poe_index];

		ion_chan_mass1_static.setText(""
				+ poe_calc_info[chosen_poe_num].mass_1[chosen_index]);

		ion_chan_mass2_static.setText(""
				+ poe_calc_info[chosen_poe_num].mass_2[chosen_index]);

		ion_chan_weight_static.setText(""
				+ poe_calc_info[chosen_poe_num].rel_weight[chosen_index]);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(ok)) {
			// pass info back to brains and move on
			CmOk();
		} else if (e.getSource().equals(cancel)) {
			CmCancel();
		}
		
	}
	
	

}
