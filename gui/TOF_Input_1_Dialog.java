package sbeam2.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.NumberFormatter;

import sbeam2.TOFData;
import sbeam2.scale;

public class TOF_Input_1_Dialog extends JDialog implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected boolean IsOpen;
	protected JFormattedTextField tof_title_edit, lab_angle_edit,
			detected_me_edit, polarization_angle_edit;
	protected JFormattedTextField degree_polarization_edit;

	public JFormattedTextField dwell_time_edit;

	protected JFormattedTextField trigger_offset_edit;

	protected JRadioButton laser_polarized;
	protected JButton change_polarization, change_dwell_time;
	protected JComboBox<scale> dwell_time_scale, trigger_scale;
	// protected TComboBoxData dwell_scale_data, trigger_scale_data;
	protected JButton ok, cancel;

	protected MainFrame parent;
	public boolean ID;

	public TOF_Input_1_Dialog(MainFrame par, TOFData tof) {
		super(par);

		parent = par;
		IsOpen = false;
		this.setTitle("Time of Flight Info for Real TOF:");
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setGroupingUsed(false);
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Float.class);
		// If you want the value to be committed on each keystroke instead of
		// focus lost
		formatter.setCommitsOnValidEdit(true);

		tof_title_edit = new JFormattedTextField();
		lab_angle_edit = new JFormattedTextField(formatter);
		detected_me_edit = new JFormattedTextField(formatter);
		polarization_angle_edit = new JFormattedTextField(formatter);
		degree_polarization_edit = new JFormattedTextField(formatter);
		dwell_time_edit = new JFormattedTextField(formatter);
		trigger_offset_edit = new JFormattedTextField(formatter);

		// The following are used to restrict the input field of the edit
		// windows in the dialog.

		laser_polarized = new JRadioButton();

		ok = new JButton("ok");
		cancel = new JButton("cancel");

		scale[] s = { scale.ps, scale.ns, scale.μs, scale.ms };
		dwell_time_scale = new JComboBox<scale>(s);
		// dwell_scale_data = new TComboBoxData();
		trigger_scale = new JComboBox<scale>(s);
		// trigger_scale_data = new TComboBoxData();

		change_polarization = new JButton("Change Polarization");
		change_dwell_time = new JButton("Change Dwell Time");
		
		tof_title_edit.setText(tof.title);
		lab_angle_edit.setText("" + tof.lab_angle);
		detected_me_edit.setText("" + tof.ion_m_e);
		polarization_angle_edit.setText("" + tof.polarization_angle);
		degree_polarization_edit.setText("" + tof.depolarization);
		dwell_time_edit.setText("" + tof.dwell);
		dwell_time_scale.setSelectedItem(tof.dwell_scale);
		trigger_offset_edit.setText("" + tof.offset);
		dwell_time_scale.setSelectedItem(tof.dwell_scale);
		trigger_scale.setSelectedItem(tof.offset_scale);

		
	}

	public void SetTOFTitle(String input_text) {
		System.out.println("tof title:\t" + input_text);
		 tof_title_edit.setText(input_text);
	}

	public void SetLabAngle(String input_text) {
		lab_angle_edit.setText(input_text);
	}

	public void SetDetectedme(String input_text) {
		detected_me_edit.setText(input_text);
	}

	public void SetPolarizationAngle(String input_text) {
		polarization_angle_edit.setText(input_text);
	}

	public void SetDegreePolarization(String input_text) {
		degree_polarization_edit.setText(input_text);
	}

	public void SetDwellTime(String input_text) {
		dwell_time_edit.setText(input_text);
	}

	public void SetDwellScale(scale input_text) {
		dwell_time_scale.setSelectedItem(input_text);
	}

	public void SetTriggerOffset(String input_text) {
		trigger_offset_edit.setText(input_text);
	}

	public void SetOffsetScale(scale input_text) {
		trigger_scale.setSelectedItem(input_text);
	}

	public void SetLaserPolarized(boolean is_laser_polarized) {
		laser_polarized.setSelected(is_laser_polarized);
	}

	public String GetTOFTitle() {
		return tof_title_edit.getText();
	}

	public String GetLabAngle() {
		String ret = lab_angle_edit.getText();
		if(ret.equals("")){
			return "0.0f";
		}
		return ret;
	}

	public String GetDetectedme() {
		String ret = detected_me_edit.getText();
		if(ret.equals("")){
			return "0.0f";
		}
		return ret;
	}

	public String GetPolarizationAngle() {
		String ret = polarization_angle_edit.getText();
		if(ret.equals("")){
			return "0.0f";
		}
		return ret;
	}

	public String GetDegreePolarization() {
		String ret = degree_polarization_edit.getText();
		if(ret.equals("")){
			return "0.0f";
		}
		return ret;
	}

	public String GetDwellTime() {
		String ret = dwell_time_edit.getText();
		if(ret.equals("")){
			return "0.0f";
		}
		return ret;
	}
	
	public scale GetDwellScale() {
		return (scale) dwell_time_scale.getSelectedItem();
	}

	public String GetTriggerOffset() {
		String ret = trigger_offset_edit.getText();
		if(ret.equals("")){
			return "0.0f";
		}
		return ret;
	}

	public scale GetOffsetScale() {
		return (scale) trigger_scale.getSelectedItem();
	}

	boolean GetStatus() {
		return IsOpen;
	}

	public boolean IsLaserPolarized() {
		return laser_polarized.isSelected();
	}

	/*
	 * protected scale RetrieveScale(int index); protected void CmOk();
	 * protected void CmCancel(); protected void CmChangePolarization();
	 * protected void CmChangeDwellTime(); protected void
	 * CmFillPolarizationBoxes(); protected void CmClearPolarizationBoxes();
	 */

	protected void SetupWindow() {
		/*
		 * tof_title_edit.setText(toftitlestr);
		 * lab_angle_edit.setText(labanglestr);
		 * detected_me_edit.setText(detected_mestr);
		 * polarization_angle_edit.setText("");
		 * degree_polarization_edit.setText("");
		 * dwell_time_edit.setText(dwelltimestr);
		 * trigger_offset_edit.setText(trigoffsetstr);
		 */

		// If these are changed, need to change corresponding index in CmOk()
		// function!
		// dwell_time_scale.setName(dwellscalestr);

		// trigger_scale.setName(offsetscalestr);

		// Enable window is a TWindow function; can be used to dis/enable a
		// TEdit window!!!
		dwell_time_edit.setEnabled(false);
		dwell_time_scale.setEnabled(false);
		polarization_angle_edit.setEnabled(false);
		degree_polarization_edit.setEnabled(false);

		// button setup
		change_polarization.setAction(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				polarization_angle_edit.setEnabled(!polarization_angle_edit
						.isEnabled());
				degree_polarization_edit.setEnabled(!degree_polarization_edit
						.isEnabled());
			}
		});

		change_dwell_time.setAction(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dwell_time_edit.setEnabled(!dwell_time_edit.isEnabled());
				dwell_time_scale.setEnabled(!dwell_time_scale.isEnabled());
			}
		});

		ok.addActionListener(this);
		cancel.addActionListener(this);

		change_polarization.setText("Change Polarization");
		change_dwell_time.setText("Change Dwell Time");

		JPanel genPane = new JPanel();
		genPane.setBorder(new LineBorder(Color.gray));
		genPane.setLayout(new BoxLayout(genPane, BoxLayout.Y_AXIS));
		genPane.add(getLabelledPanel(tof_title_edit, "Title"));
		genPane.add(getLabelledPanel(lab_angle_edit, "lab angle"));
		genPane.add(getLabelledPanel(detected_me_edit, "Detected m/e"));

		JPanel polPane = new JPanel();
		polPane.setBorder(new LineBorder(Color.gray));
		polPane.setLayout(new BoxLayout(polPane, BoxLayout.Y_AXIS));
		polPane.add(getLabelledPanel(laser_polarized, "Laser Polarized"));
		polPane.add(getLabelledPanel(polarization_angle_edit,
				"Polarization Angle"));
		polPane.add(getLabelledPanel(degree_polarization_edit,
				"Depolarization"));
		polPane.add(change_polarization);

		JPanel timePane = new JPanel();
		timePane.setBorder(new LineBorder(Color.gray));
		timePane.setLayout(new BoxLayout(timePane, BoxLayout.Y_AXIS));
		timePane.add(getLabelledPanel(dwell_time_edit, "Dwell Time"));
		timePane.add(getLabelledPanel(dwell_time_scale, "Scale"));
		timePane.add(change_dwell_time);
		timePane.add(getLabelledPanel(trigger_offset_edit, "Trigger Offset"));
		timePane.add(getLabelledPanel(trigger_scale, "scale"));

		JPanel finPane = new JPanel();
		finPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
		finPane.setLayout(new BoxLayout(finPane, BoxLayout.X_AXIS));
		finPane.add(ok);
		finPane.add(cancel);

		this.getContentPane().add(genPane, BorderLayout.NORTH);
		this.getContentPane().add(polPane, BorderLayout.WEST);
		this.getContentPane().add(timePane, BorderLayout.EAST);
		this.getContentPane().add(finPane, BorderLayout.SOUTH);
	}

	private JPanel getLabelledPanel(Component c, String s) {
		JPanel tempPan = new JPanel();
		tempPan.setLayout(new BoxLayout(tempPan, BoxLayout.X_AXIS));
		tempPan.add(new JLabel(s));
		tempPan.add(c);
		return tempPan;
	}
	
	public void execute(){
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

			IsOpen = false;
			ID = true;
			this.dispose();
		} else if (e.getSource().equals(cancel)) {
			IsOpen = false;
			ID = false;
			this.dispose();
		}

	}

}
