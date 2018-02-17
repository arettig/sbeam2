package sbeam2.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.text.NumberFormatter;

import sbeam2.InstrumentParameters;

public class Instr_Param_Dialog extends JDialog implements ActionListener{

	protected MainFrame parent;

	protected boolean IsOpen;

	protected JFormattedTextField beam_ang_edit, detect_ang_edit,
			ion_flight_const_edit;
	protected JFormattedTextField ionizer_len_edit, flight_len_edit;

	protected JComboBox<String> detectionType; //product_flux_radio, number_density_radio;
	protected JButton ok, cancel, load, save;

	protected boolean NumberDensityCalculation, params_have_changed,
			was_number_dens_calc;
	
	public boolean ID;

	public Instr_Param_Dialog(MainFrame p, InstrumentParameters params) {
		// TODO Auto-generated constructor stub
		super(p);

		parent = p;
		IsOpen = false;

		this.setTitle("Instrumental Parameters for this session:");

		// text field stuff
		NumberFormat format = NumberFormat.getNumberInstance();
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Double.class);
		// If you want the value to be committed on each keystroke instead of
		// focus lost
		formatter.setCommitsOnValidEdit(true);
		ion_flight_const_edit = new JFormattedTextField(formatter);
		ionizer_len_edit = new JFormattedTextField(formatter);
		flight_len_edit = new JFormattedTextField(formatter);
		beam_ang_edit = new JFormattedTextField(formatter);
		detect_ang_edit = new JFormattedTextField(formatter);
		detectionType = new JComboBox<String>(new String[]{"Number Density","Product Flux"});
		
		ion_flight_const_edit.setText("" + params.ionFlightConst);
		ionizer_len_edit.setText("" + params.ionizerLen);
		flight_len_edit.setText("" + params.flightLen);
		beam_ang_edit.setText("" + params.beamAng);
		detect_ang_edit.setText("" + params.detectAng);
		detectionType.setSelectedIndex((params.isNumDensity) ? 0:1);
		
		ok = new JButton("ok");
		cancel = new JButton("cancel");
		ok.addActionListener(this);
		cancel.addActionListener(this);
	}

	public void SetIonFlightConst(String input_text) {
		ion_flight_const_edit.setText(input_text);
	}

	public void SetIonizerLen(String input_text) {
		ionizer_len_edit.setText(input_text); 
	}

	public void SetFlightLen(String input_text) {
		flight_len_edit.setText(input_text);
	}

	public void SetBeamAng(String input_text) {
		beam_ang_edit.setText(input_text);
	}

	public void SetDetectAng(String input_text) {
		detect_ang_edit.setText(input_text);
	}

	public void SetTypeOfCalculation(boolean num_dens_calc) {
		NumberDensityCalculation = num_dens_calc;
	}

	public String GetIonFlightConst() {
		return ion_flight_const_edit.getText();
	}

	public String GetIonizerLen() {
		return ionizer_len_edit.getText();
	}

	public String GetFlightLen() {
		return flight_len_edit.getText();
	}

	public String GetBeamAng() {
		return beam_ang_edit.getText();
	}

	public String GetDetectAng() {
		return detect_ang_edit.getText();
	}

	public boolean isNumDensity() {
		return detectionType.getSelectedIndex() == 0;
	}

	public boolean GetStatus() {
		return IsOpen;
	}

	public boolean GetParamsHaveChanged() {
		return params_have_changed;
	}

	protected void SetupWindow() {
		params_have_changed = false;
		was_number_dens_calc = NumberDensityCalculation;
		
		FillEditBoxes();
		IsOpen = true;
		
		JPanel cont = new JPanel();
		cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
		cont.add(getLabelledPanel(ion_flight_const_edit, "Ion Flight Constant: "));
		cont.add(getLabelledPanel(ionizer_len_edit, "Effective Length of Ionizer(cm): "));
		cont.add(getLabelledPanel(flight_len_edit, "Flight Length of Main Chamber(cm): "));
		cont.add(getLabelledPanel(beam_ang_edit, "Angular Half Width of Beam(degrees): "));
		cont.add(getLabelledPanel(detect_ang_edit, "Angular Half Width of Detection Aperture(degrees): "));
		cont.add(getLabelledPanel(detectionType, "Detection Method: "));
		
		JPanel pan = new JPanel();
		pan.setLayout(new BoxLayout(pan, BoxLayout.X_AXIS));
		pan.add(cancel);
		pan.add(ok);
		pan.setBorder(new BevelBorder(BevelBorder.LOWERED));
		
		this.add(cont, BorderLayout.NORTH);
		this.add(pan, BorderLayout.SOUTH);
	}

	protected void FillEditBoxes() {

		IsOpen = true;
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
