package sbeam2.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

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
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.NumberFormatter;

import sbeam2.CalcData;
import sbeam2.SBApp;

public class Calc_TOF_Dialog extends JDialog implements ActionListener,
		ListSelectionListener, ChangeListener {

	protected MainFrame parent;
	protected boolean IsOpen;

	protected JFormattedTextField lab_angle_edit, polar_angle_edit,
			depolar_edit;
	protected JFormattedTextField starting_time_edit, ending_time_edit,
			num_tof_points_edit;
	protected JLabel calc_title_static, num_tofs_static;
	protected JSpinner num_tofs_updown;
	protected JList<String> tof_list;
	protected JCheckBox polarized_chkbox;
	protected JButton ok, cancel;

	protected CalcData calculation;
	protected SBApp application;

	protected String[] tof_names;

	protected String calc_title;

	protected int num_tofs, old_num_tofs, old_sel_index;
	protected float[] lab_angles, polarization_angles, depolarization;
	protected boolean[] is_polarized;

	public Calc_TOF_Dialog(MainFrame p, SBApp app, CalcData calc) {
		// TODO Auto-generated constructor stub
		super(p);
		parent = p;
		application = app;

		int i;
		float[] calc_lab_angles;
		float[] calc_polar_angles;
		float[] calc_depolar;
		boolean[] calc_is_polarized;

		IsOpen = false;

		calc_title = "";

		application = app;
		calculation = calc;

		num_tofs = calculation.num_tofs;
		calc_lab_angles = calculation.tof_lab_angles;
		calc_polar_angles = calculation.tof_polarization_angles;
		calc_depolar = calculation.depolarization;
		calc_is_polarized = calculation.tof_is_polarized;
		// is_number_density_calc = calculation.GetIsNumDensityCalc();

		tof_names = null;

		// Copy this information to the arrays in this class
		if (num_tofs > 0) {
			lab_angles = new float[num_tofs];
			polarization_angles = new float[num_tofs];
			depolarization = new float[num_tofs];
			is_polarized = new boolean[num_tofs];

			for (i = 0; i < num_tofs; i++) {
				lab_angles[i] = calc_lab_angles[i];
				polarization_angles[i] = calc_polar_angles[i];
				depolarization[i] = calc_depolar[i];
				is_polarized[i] = calc_is_polarized[i];
			}
		} else {
			lab_angles = null;
			polarization_angles = null;
			depolarization = null;
			is_polarized = null;
		}

		NumberFormat format = NumberFormat.getNumberInstance();
		format.setGroupingUsed(false);
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Float.class);

		lab_angle_edit = new JFormattedTextField(formatter);
		polar_angle_edit = new JFormattedTextField(formatter);
		polar_angle_edit.setPreferredSize(new Dimension(50,20));
		depolar_edit = new JFormattedTextField(formatter);

		polarized_chkbox = new JCheckBox();
		polarized_chkbox.addActionListener(this);

		calc_title_static = new JLabel();
		num_tofs_static = new JLabel();
		num_tofs_updown = new JSpinner(new SpinnerNumberModel(num_tofs, 0, 50,
				1));
		num_tofs_updown.addChangeListener(this);
		tof_list = new JList<String>(new DefaultListModel<String>());
		tof_list.addListSelectionListener(this);

		starting_time_edit = new JFormattedTextField(formatter);
		ending_time_edit = new JFormattedTextField(formatter);
		num_tof_points_edit = new JFormattedTextField(formatter);

		ok = new JButton("ok");
		cancel = new JButton("cancel");
		ok.addActionListener(this);
		cancel.addActionListener(this);

		// num_density_radio = new TRadioButton(this, IDC_CALCTOFNUMDENSRADIO);
		// ion_flux_radio = new TRadioButton(this, IDC_CALCTOFFLUXRADIO);

		// The following are used to restrict the input field of the edit
		// windows in the dialog.

		old_sel_index = -1;
		old_num_tofs = num_tofs;

		this.setTitle("Set up TOF calculation info:");

		polarized_chkbox.setSelected(false);
		lab_angle_edit.setEnabled(false);
		polar_angle_edit.setEnabled(false);
		depolar_edit.setEnabled(false);
		polarized_chkbox.setEnabled(false);
		calc_title_static.setText(calc_title);
		num_tofs_static = new JLabel();
		// num_tofs_updown.SetBuddy(HWND(*num_tofs_static));

		starting_time_edit.setText("" + calculation.starting_time);
		ending_time_edit.setText("" + calculation.ending_time);
		num_tof_points_edit.setText("" + calculation.num_tof_points);

		/*
		 * num_tofs_updown.Attr.Style |= UDS_SETBUDDYINT;
		 * num_tofs_updown.SetRange(0, 50); num_tofs_updown.SetPos(num_tofs);
		 */

		((DefaultListModel<String>) tof_list.getModel()).clear();
		old_sel_index = -1; // No TOF was previously selected
		FillTOFListBox();

		if (num_tofs > 0) {
			tof_list.setSelectedIndex(0);
		}

		/*
		 * if(is_number_density_calc == true) num_density_radio.Check(); else
		 * ion_flux_radio.Check();
		 */

	}

	public void SetCalculationTitle(String input_text) {
		calc_title = input_text;
	}

	protected void SetupWindow() {

		JPanel top = new JPanel();
		top.setBorder(BorderFactory.createTitledBorder("TOF's to calculate:"));
		top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
		top.add(getLabelledPanel(num_tofs_updown,
				"Number of TOF's to calculate:"));
		JPanel topbot = new JPanel();
		topbot.add(new JScrollPane(tof_list));
		JPanel topbotright = new JPanel();
		topbotright.setLayout(new BoxLayout(topbotright, BoxLayout.Y_AXIS));
		topbotright
				.add(getLabelledPanel(lab_angle_edit, "Lab Angle(degrees): "));
		topbotright.add(getLabelledPanel(polarized_chkbox,
				"Laser is Polarized "));
		topbotright.add(getLabelledPanel(polar_angle_edit,
				"Polarization Angle(degrees): "));
		topbotright.add(getLabelledPanel(depolar_edit,
				"Depolarization (0 . 1): "));
		topbot.add(topbotright);
		top.add(topbot);

		JPanel bot = new JPanel();
		bot.setBorder(BorderFactory
				.createTitledBorder("Time Information for all TOF's:"));
		bot.setLayout(new BoxLayout(bot, BoxLayout.Y_AXIS));
		bot.add(getLabelledPanel(starting_time_edit, "Starting Time(µs): "));
		bot.add(getLabelledPanel(ending_time_edit, "Ending Time(µs): "));
		bot.add(getLabelledPanel(num_tof_points_edit,
				"Number of Points in TOF to calculate: "));

		JPanel pan = new JPanel();
		pan.setLayout(new BoxLayout(pan, BoxLayout.X_AXIS));
		pan.add(cancel);
		pan.add(ok);
		pan.setBorder(new BevelBorder(BevelBorder.LOWERED));

		this.add(top, BorderLayout.NORTH);
		this.add(bot, BorderLayout.CENTER);
		this.add(pan, BorderLayout.SOUTH);
	}

	private JPanel getLabelledPanel(Component c, String s) {
		JPanel tempPan = new JPanel();
		tempPan.setLayout(new BoxLayout(tempPan, BoxLayout.X_AXIS));
		tempPan.add(new JLabel(s));
		tempPan.add(c);
		return tempPan;
	}

	// protected void EvMouseMove(int modKeys, Point point);

	protected void FillTOFListBox() {
		int i;
		String temp_number = "";

		// Delete old array
		if (tof_names != null) {
			if (old_num_tofs > 0) {
				for (i = 1; i <= old_num_tofs; i++) {
					tof_names[i - 1] = null;
				}
				tof_names = null;
			}
		}

		if (num_tofs > 0) {
			tof_names = new String[num_tofs];
			((DefaultListModel<String>) tof_list.getModel()).clear();
			tof_list.setEnabled(true);
			lab_angle_edit.setEnabled(true);
			polarized_chkbox.setEnabled(true);

			for (i = 1; i <= num_tofs; i++) {
				tof_names[i - 1] = "TOF #" + i;
				((DefaultListModel<String>) tof_list.getModel())
						.addElement(tof_names[i - 1]);
			}
			tof_list.setSelectedIndex(0);
			old_sel_index = 0;

			lab_angle_edit.setText("" + lab_angles[old_sel_index]);

			polar_angle_edit.setText("");
			depolar_edit.setText("");

			if (is_polarized[old_sel_index] == true) {
				polarized_chkbox.setSelected(true);
				polar_angle_edit.setEnabled(true);
				depolar_edit.setEnabled(true);
				polar_angle_edit.setText(""
						+ polarization_angles[old_sel_index]);
				depolar_edit.setText("" + depolarization[old_sel_index]);
			}

			if (is_polarized[old_sel_index] == false) {
				polarized_chkbox.setSelected(false);
				polar_angle_edit.setEnabled(false);
				depolar_edit.setEnabled(false);
			}
		} else {
			((DefaultListModel<String>) tof_list.getModel()).clear();
			lab_angle_edit.setText("");
			polar_angle_edit.setText("");
			depolar_edit.setText("");
			tof_list.setEnabled(false);
			lab_angle_edit.setEnabled(false);
			polar_angle_edit.setEnabled(false);
			depolar_edit.setEnabled(false);
			polarized_chkbox.setEnabled(false);
			old_sel_index = -1;
		}
	}

	public void Execute() {
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		SetupWindow();
		this.pack();
		this.setResizable(false);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(ok)) {
			// pass info back to brains and move on
			tof_list.setSelectedIndex(-1);
			this.valueChanged(null);
			System.out.println("Setting calc tofs:" + lab_angles[0] + "\t" + polarization_angles[0]);
			calculation.num_tofs = (int) num_tofs_updown.getValue();
			calculation.SetTOFInfo(lab_angles, polarization_angles,
					depolarization, is_polarized);

			calculation.SetTOFTimeInfo(
					parseFloat(starting_time_edit.getText()),
					parseFloat(ending_time_edit.getText()),
					Integer.parseInt(num_tof_points_edit.getText()));

			/*
			 * if(num_density_radio.GetCheck() == BF_CHECKED)
			 * calculation.SetIsNumDensityCalc(true); else
			 * calculation.SetIsNumDensityCalc(false);
			 */
			IsOpen = false;
			this.dispose();
		} else if (e.getSource().equals(cancel)) {
			IsOpen = false;
			this.dispose();
		} else if (e.getSource().equals(polarized_chkbox)) {
			if (old_sel_index >= 0) {
				if (!polarized_chkbox.isSelected()) {
					// Store the data which is in the boxes
					is_polarized[old_sel_index] = false;
					polarization_angles[old_sel_index] = parseFloat(polar_angle_edit
							.getText());
					depolarization[old_sel_index] = parseFloat(depolar_edit
							.getText());

					polar_angle_edit.setText("");
					polar_angle_edit.setEnabled(false);
					depolar_edit.setText("");
					depolar_edit.setEnabled(false);
				} else {
					is_polarized[old_sel_index] = true;
					polar_angle_edit.setEnabled(true);
					depolar_edit.setEnabled(true);
					polar_angle_edit.setText(""
							+ polarization_angles[old_sel_index]);
					depolar_edit.setText("" + depolarization[old_sel_index]);
				}

			}
		}

	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub
		// Copy old data from TEdit to arrays
		System.out.println("VALUE CHANGED");

		if((int)num_tofs_updown.getValue() == 0){
			return;
		}
		if (old_sel_index >= 0) {
			lab_angles[old_sel_index] = parseFloat(lab_angle_edit
					.getText());

			if (polarized_chkbox.isSelected()) {
				is_polarized[old_sel_index] = true;
				polarization_angles[old_sel_index] = Float
						.parseFloat(polar_angle_edit.getText());
				depolarization[old_sel_index] = parseFloat(depolar_edit
						.getText());
			} else
				is_polarized[old_sel_index] = false;
		}

		// Put stored info into edit boxes for the TOF which is currently
		// selected
		old_sel_index = tof_list.getSelectedIndex(); // Reset old_sel_index to
														// the one now selected

		// This next if statement is necessary since this function is called by
		// CmOk
		// Thus, it is possible that nothing is selected when this function is
		// called
		if (old_sel_index >= 0) {

			lab_angle_edit.setText("");
			polar_angle_edit.setText("");
			depolar_edit.setText("");

			lab_angle_edit.setText("" + lab_angles[old_sel_index]);

			if (is_polarized[old_sel_index] == true) {
				polarized_chkbox.setSelected(true);
				polar_angle_edit.setEnabled(true);
				depolar_edit.setEnabled(true);
				polar_angle_edit.setText(""
						+ polarization_angles[old_sel_index]);
				depolar_edit.setText("" + depolarization[old_sel_index]);
			}
			if (is_polarized[old_sel_index] == false) {
				polarized_chkbox.setSelected(false);
				polar_angle_edit.setEnabled(false);
				depolar_edit.setEnabled(false);
			}
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		int i, last_tof;
		float[] temp_lab_angles;
		float[] temp_polar_angles;
		float[] temp_depolar;
		boolean[] temp_is_polar;

		System.out.println("STATE CHANGED");
		num_tofs = (int) num_tofs_updown.getValue();
		if (num_tofs != old_num_tofs) {
			if ((old_sel_index >= 0) && (old_num_tofs > 0)) // Save info already
															// in boxes
			{
				lab_angles[old_sel_index] = parseFloat(lab_angle_edit.getText());

				if (polarized_chkbox.isSelected()) {
					is_polarized[old_sel_index] = true;
					polarization_angles[old_sel_index] = parseFloat(polar_angle_edit
							.getText());
					depolarization[old_sel_index] = parseFloat(depolar_edit
							.getText());
				}
				if (polarized_chkbox.isSelected())
					is_polarized[old_sel_index] = false;
			}

			last_tof = Math.min(num_tofs, old_num_tofs);
			// Copy data from old tof arrays and then delete old stuff
			if (num_tofs == 0) {
				temp_lab_angles = null;
				temp_polar_angles = null;
				temp_depolar = null;
				temp_is_polar = null;
			} else {
				temp_lab_angles = new float[num_tofs];
				temp_polar_angles = new float[num_tofs];
				temp_depolar = new float[num_tofs];
				temp_is_polar = new boolean[num_tofs];
			}
			for (i = 0; i < num_tofs; i++) {
				if (i < last_tof) {
					temp_lab_angles[i] = lab_angles[i];
					temp_polar_angles[i] = polarization_angles[i];
					temp_depolar[i] = depolarization[i];
					temp_is_polar[i] = is_polarized[i];
				}
				// Set all new info to default values
				else {

				}
			}

			if (old_num_tofs > 0) {

			}

			lab_angles = temp_lab_angles;
			polarization_angles = temp_polar_angles;
			depolarization = temp_depolar;
			is_polarized = temp_is_polar;

			FillTOFListBox();
			old_num_tofs = num_tofs;
		}
	}

	private float parseFloat(String s) {
		if (s == null || s.equals("")) {
			return 0;
		}
		return Float.parseFloat(s);
	}
}
