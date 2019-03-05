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

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.text.NumberFormatter;

import sbeam2.CalcData;
import sbeam2.SBApp;

public class CalculateMainDialog extends JDialog implements ActionListener {

	private static final AbstractButton num_bm_vel_segs_edit = null;
	protected MainFrame parent;
	protected boolean IsOpen;

	protected JFormattedTextField title_edit, ion_m_e_edit, bm_ang_segs_edit,
			det_ang_segs_edit;
	protected JFormattedTextField ionizer_segs_edit, alpha_edit,
			speed_ratio_edit;
	protected JFormattedTextField bm_vel_edit, lab_vel_min_edit,
			lab_vel_max_edit, lab_vel_segs_edit;

	protected JLabel num_poes_static, num_tofs_static;

	protected JButton inst_params_button, contrib_poes_button;
	protected JButton ionization_dist_button, bm_vel_dist_button, tof_button;
	protected JButton ok, cancel;

	protected JCheckBox gaussian_dist_checkbox;
	protected SBApp application;
	protected CalcData calculation;

	protected float[] ionization_array;

	protected int calculation_number, num_contrib_poes;
	protected boolean ionization_is_gaussian, detachTOFs, InstParamsSet;

	public boolean ID;
	
	public CalculateMainDialog(MainFrame p, SBApp app) {
		// TODO Auto-generated constructor stub
		super(p);
		parent = p;

		IsOpen = false;
		this.setTitle("Forward Convolution Information:");

		application = app;

		NumberFormat format = NumberFormat.getNumberInstance();
		format.setGroupingUsed(false);
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Float.class);
		// If you want the value to be committed on each keystroke instead of
		// focus lost
		formatter.setCommitsOnValidEdit(true);
		title_edit = new JFormattedTextField();
		ion_m_e_edit = new JFormattedTextField(formatter);
		bm_ang_segs_edit = new JFormattedTextField(formatter);
		det_ang_segs_edit = new JFormattedTextField(formatter);
		ionizer_segs_edit = new JFormattedTextField(formatter);
		alpha_edit = new JFormattedTextField(formatter);
		speed_ratio_edit = new JFormattedTextField(formatter);
		bm_vel_edit = new JFormattedTextField(formatter);
		lab_vel_min_edit = new JFormattedTextField(formatter);
		lab_vel_max_edit = new JFormattedTextField(formatter);
		lab_vel_segs_edit = new JFormattedTextField(formatter);

		num_poes_static = new JLabel("Number of Contributing P(E)'s: "
				+ num_contrib_poes);
		num_tofs_static = new JLabel("Number of TOF's to Calculate: ");

		inst_params_button = new JButton("View/Change Instrumental Parameters");
		inst_params_button.addActionListener(this);
		contrib_poes_button = new JButton("View/Select Contributing P(E)'s");
		contrib_poes_button.addActionListener(this);
		ionization_dist_button = new JButton(
				"View/Change Ionization Distribution");
		ionization_dist_button.addActionListener(this);
		bm_vel_dist_button = new JButton(
				"View/Change Beam Velocity Distribution");
		bm_vel_dist_button.addActionListener(this);
		tof_button = new JButton("View/Change TOF Information");
		tof_button.addActionListener(this);
		ok = new JButton("CALCULATE!");
		ok.addActionListener(this);
		cancel = new JButton("cancel");
		cancel.addActionListener(this);

		gaussian_dist_checkbox = new JCheckBox();
		ionization_array = null;

		IsOpen = true;

		title_edit.setText("");

		num_poes_static.setText("Number of contributing P(E)'s: "
				+ num_contrib_poes);
		num_tofs_static.setText("");

		if (ionization_is_gaussian)
			gaussian_dist_checkbox.setSelected(true);
		else
			gaussian_dist_checkbox.setSelected(false);
		;
	}

	public void SetDialogData(CalcData calc) {
		calculation = calc;
		num_contrib_poes = calculation.num_poes;
		ionization_is_gaussian = calculation.is_ionizer_gaussian;
		calculation_number = calculation.CalcNumber;
		
		
		ion_m_e_edit.setText("" + calculation.ion_m_e);
		bm_ang_segs_edit.setText("" + calculation.num_beam_ang_segs);
		det_ang_segs_edit.setText("" + calculation.num_det_ang_segs);
		ionizer_segs_edit.setText("" + calculation.num_ionizer_segs);
		alpha_edit.setText("" + calculation.alpha);
		speed_ratio_edit.setText("" + calculation.speed_ratio);
		bm_vel_edit.setText("" + calculation.num_beam_vel_segs);
		lab_vel_min_edit.setText("" + calculation.min_lab_velocity);
		lab_vel_max_edit.setText("" + calculation.max_lab_velocity);
		lab_vel_segs_edit.setText("" + calculation.num_lab_vel_segs);
		num_poes_static.setText("Number of contributing P(E)'s: "+ num_contrib_poes);
		title_edit.setText(calculation.title);
		num_tofs_static.setText("" + calculation.num_tofs);


	}

	public void DetachOldTOFs(boolean should_detach) {
		detachTOFs = should_detach;
	}

	public void SetAreInstParamsSet(boolean are_set) {
		InstParamsSet = are_set;
	}

	protected void SetupWindow() {
		this.getContentPane().setLayout(
				new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		JPanel pan1 = new JPanel(new BorderLayout());
		pan1.setBorder(BorderFactory
				.createTitledBorder("Title of Calculation:"));
		pan1.add(title_edit);

		JPanel pan2 = new JPanel();
		JPanel pan2left = new JPanel(new BorderLayout());
		pan2left.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		pan2left.add(
				getLabelledPanel(ion_m_e_edit,
						"Ion m/e ratio for calculation(amu): "),
				BorderLayout.NORTH);
		pan2left.add(inst_params_button, BorderLayout.SOUTH);
		pan2.add(pan2left);
		JPanel pan2right = new JPanel(new BorderLayout());
		pan2right.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		pan2right.add(num_poes_static, BorderLayout.NORTH);
		pan2right.add(contrib_poes_button, BorderLayout.SOUTH);
		pan2.add(pan2right);

		JPanel pan3 = new JPanel();
		pan3.setBorder(BorderFactory
				.createTitledBorder("Number of segments into which the following are divided for calculation purposes:"));
		pan3.add(getLabelledPanel(bm_ang_segs_edit,
				"Beam Angle at Interaction Region: "));
		pan3.add(getLabelledPanel(det_ang_segs_edit,
				"Detector Angle at Interaction Region: "));

		JPanel pan4 = new JPanel(new BorderLayout());
		pan4.setBorder(BorderFactory.createTitledBorder("Distribution of Ionization Source Probablilities: "));
		pan4.add(getLabelledPanel(gaussian_dist_checkbox,"Gaussian Ionization Distribution"), BorderLayout.NORTH);
		pan4.add(
				getLabelledPanel(ionizer_segs_edit,
						"Number of Ionizer Segments: "), BorderLayout.CENTER);
		pan4.add(ionization_dist_button, BorderLayout.EAST);

		JPanel pan5 = new JPanel();
		pan5.setBorder(BorderFactory
				.createTitledBorder("Parameters for Molecular Beam Velocity Distribution: "));
		JPanel pan5left = new JPanel(new BorderLayout());
		pan5left.add(getLabelledPanel(alpha_edit, "Alpha(m/s): "),
				BorderLayout.NORTH);
		pan5left.add(
				getLabelledPanel(bm_vel_edit,
						"Number of Molecular Beam Velocity Segments: "),
				BorderLayout.SOUTH);
		pan5.add(pan5left);
		JPanel pan5right = new JPanel(new BorderLayout());
		pan5right.add(getLabelledPanel(speed_ratio_edit, "Speed Ratio: "),
				BorderLayout.NORTH);
		pan5right.add(bm_vel_dist_button, BorderLayout.SOUTH);
		pan5.add(pan5right);

		JPanel pan6 = new JPanel();
		pan6.setBorder(BorderFactory
				.createTitledBorder("Lab velocities to consider in calculation: "));
		JPanel pan6left = new JPanel(new BorderLayout());
		pan6left.add(
				getLabelledPanel(lab_vel_min_edit,
						"Minimum Lab Velocity(m/s): "), BorderLayout.NORTH);
		pan6left.add(
				getLabelledPanel(lab_vel_segs_edit,
						"Number of Lab Velocity Segments: "),
				BorderLayout.SOUTH);
		pan6.add(pan6left);
		pan6.add(getLabelledPanel(lab_vel_max_edit,
				"Maximum Lab Velocity(m/s): "));

		JPanel pan7 = new JPanel();
		pan7.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		pan7.add(num_tofs_static);
		pan7.add(tof_button);

		JPanel pan8 = new JPanel();
		pan8.setLayout(new BoxLayout(pan8, BoxLayout.X_AXIS));
		pan8.add(cancel);
		pan8.add(ok);

		this.add(pan1);
		this.add(pan2);
		this.add(pan3);
		this.add(pan4);
		this.add(pan5);
		this.add(pan6);
		this.add(pan7);
		this.add(pan8);
	}

	private JPanel getLabelledPanel(Component c, String s) {
		JPanel tempPan = new JPanel();
		tempPan.setLayout(new BoxLayout(tempPan, BoxLayout.X_AXIS));
		tempPan.add(new JLabel(s));
		tempPan.add(c);
		return tempPan;
	}

	public void Execute() {
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		SetupWindow();
		this.pack();
		this.setResizable(false);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setVisible(true);
	}

	protected void ok() {
		int num_ion_segs;
		float ionizer_length;

		if (!application.InstParamsSet) {
			JOptionPane
					.showMessageDialog(this,
							"Instrumental parameters must be set for proper calculation.");
			application.setupInstParam();
		}
		IsOpen = false;

		if (detachTOFs) {
			//document.StoreDetachedTOFData(calculation);
			detachTOFs = false;
		}

		if (num_contrib_poes == 0) {
			cancel(); // Calls CmCancel to stop any changes to the information
			return;
		}

		this.dispose();
		calculation.ReplacePOECalcData(true);

		calculation.title = title_edit.getText();
		calculation.ion_m_e = Float.parseFloat(ion_m_e_edit.getText());

		calculation.SetNumDetAngleSegs(Integer.parseInt(det_ang_segs_edit
				.getText()));
		calculation.SetNumBeamAngleSegs(Integer.parseInt(bm_ang_segs_edit
				.getText()));

		// Put something here regarding whether a non-Boltzmann distribution is
		// requested
		calculation.SetBmVelDistrib(Integer.parseInt(bm_vel_edit.getText()),
				Float.parseFloat(alpha_edit.getText()),
				Float.parseFloat(speed_ratio_edit.getText()), null, null);

		calculation.SetLabVelocityArrays(
				Float.parseFloat(lab_vel_min_edit.getText()),
				Float.parseFloat(lab_vel_max_edit.getText()),
				Integer.parseInt(lab_vel_segs_edit.getText()));

		num_ion_segs = Integer.parseInt(ionizer_segs_edit.getText());
		ionizer_length = application.instrParam.ionizerLen;

		if (gaussian_dist_checkbox.isSelected()) {
			calculation.SetIonizerDist(num_ion_segs, ionizer_length, null);
			// No array sent since Gaussian distribution
		} else {
			calculation.SetIonizerDist(num_ion_segs, ionizer_length,
					ionization_array);
		}
		
		calculation.num_poes = num_contrib_poes;
		application.CalculateTOFs(calculation);
	}

	protected void cancel() {
		if (IsOpen) {
			calculation.ReplacePOECalcData(false);
		} else {
			calculation.ReplacePOECalcData(true);
		}
		detachTOFs = false;
		this.dispose();
	}


	protected void ContribPOE() {
		ContribPOEsDialog contributing_poes;
		contributing_poes = new ContribPOEsDialog(parent, application,
				calculation);

		contributing_poes.SetCalculationTitle(title_edit.getText());
		contributing_poes.SetIon_m_e(ion_m_e_edit.getText());

		contributing_poes.Execute();
		// check
		num_contrib_poes = calculation.num_poes;

		num_poes_static.setText("Number of contributing P(E)'s:  "+ num_contrib_poes);

	}

	protected void CmIonizationDist() {
		// nothing yet
	}

	protected void CmBeamVelDist() {
		// nothing yet
	}

	protected void TOFButton() {
		Calc_TOF_Dialog tofs_to_calculate;
		tofs_to_calculate = new Calc_TOF_Dialog(parent, application,
				calculation);

		tofs_to_calculate.SetCalculationTitle(title_edit.getText());

		tofs_to_calculate.Execute();
		// check

		num_tofs_static.setText("" + calculation.num_tofs);

	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(ok)) {
			// pass info back to brains and move on
			ID = true;
			ok();
		} else if (e.getSource().equals(cancel)) {
			ID = false;
			cancel();
		} else if (e.getSource().equals(inst_params_button)) {
			application.setupInstParam();
		} else if (e.getSource().equals(contrib_poes_button)) {
			ContribPOE();
		} else if (e.getSource().equals(ionization_dist_button)) {

		} else if (e.getSource().equals(bm_vel_dist_button)) {

		} else if (e.getSource().equals(tof_button)) {
			TOFButton();
		}

	}

}
