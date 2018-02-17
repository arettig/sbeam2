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
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.text.NumberFormatter;

public class Param_Dialog extends JDialog implements ActionListener {

	protected boolean IsOpen;
	protected int type;
	protected boolean HasChanged;
	protected JLabel text1, text2;
	protected JTextField edit1, edit2;

	protected String value_1;
	protected String value_2;
	protected String default_1;
	protected String default_2;

	protected String energy_text_low;
	protected String energy_text_high;

	protected JButton ok, cancel;

	public boolean ID;
	
	
	public Param_Dialog(MainFrame p, int typ) {
		// TODO Auto-generated constructor stub
		super(p);

		IsOpen = false;
		this.setTitle("Input this stuff:");
		text1 = new JLabel();
		text2 = new JLabel();
		type = typ;

		NumberFormat format = NumberFormat.getInstance();
		format.setGroupingUsed(false);
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(Integer.MIN_VALUE);
		formatter.setMaximum(Integer.MAX_VALUE);

		edit1 = new JFormattedTextField(formatter);
		edit2 = new JFormattedTextField(formatter);

		// Initialize "default" values in the edit boxes
		value_1 = "0.0";
		value_2 = "-1.0";
		default_1 = "";
		default_2 = "";
		HasChanged = false;

		ok = new JButton("ok");
		cancel = new JButton("cancel");
		ok.addActionListener(this);
		cancel.addActionListener(this);
	}

	public void SetType(int type_of_dialog) {
		type = type_of_dialog;
	}

	public void SetValue1(String input_text) {
		value_1 = input_text;
	}

	public void SetValue2(String input_text) {
		value_2 = input_text;
	}

	public void SetDefault1(String input_text) {
		default_1 = input_text;
	}

	public void SetDefault2(String input_text) {
		default_2 = input_text;
	}

	public String GetValue1() {
		String ret = this.edit1.getText();
		if(ret.equals("")){
			return "0.0f";
		}
		return ret;
	}

	public String GetValue2() {
		String ret = this.edit2.getText();
		if(ret.equals("")){
			return "0.0f";
		}
		return ret;
	}

	public String GetDefault1() {
		String ret = default_1;
		if(ret.equals("")){
			return "0.0f";
		}
		return ret;
	}

	public String GetDefault2() {
		String ret = default_2;
		if(ret.equals("")){
			return "0.0f";
		}
		return ret;
	}

	public boolean GetStatus() {
		return IsOpen;
	}

	public boolean GetHasChanged() {
		boolean temp_boolean = HasChanged;
		HasChanged = false;
		return temp_boolean;
	}

	public void ConvertEnergyValues(int unit_num, float mult_factor) {
		float temp_float;
		if (type != 2) {
			return;
		}

		switch (unit_num) {
		case 0:
			energy_text_low = "Smallest energy to display (in kcal/mol):";
			energy_text_high = "Largest energy to display (in kcal/mol):";
			break;
		case 1:
			energy_text_low = "Smallest energy to display (in kJ/mol):";
			energy_text_high = "Largest energy to display (in kJ/mol):";
			break;
		case 2:
			energy_text_low = "Smallest energy to display (in wavenumbers):";
			energy_text_high = "Largest energy to display (in wavenumbers):";
			break;
		default:
			energy_text_low = "Smallest energy to display (in eV):";
			energy_text_high = "Largest energy to display (in eV):";
		}

		temp_float = Float.parseFloat(value_1);
		temp_float *= mult_factor;
		value_1 = "" + temp_float;

		temp_float = Float.parseFloat(value_2);
		temp_float *= mult_factor;
		value_2 = "" + temp_float;

		temp_float = Float.parseFloat(default_1);
		temp_float *= mult_factor;
		default_1 = "" + temp_float;

		temp_float = Float.parseFloat(default_2);
		temp_float *= mult_factor;
		default_2 = "" + temp_float;

		if (IsOpen) {
			text1.setText(energy_text_low);
			text2.setText(energy_text_high);
			edit1.setText(value_1);
			edit2.setText(value_2);
		}
	}

	public void ShowWindow() {
		switch (type) {
		case 1:
		case 4:
			text1.setText("Earliest time to display (in microseconds):");
			text2.setText("Latest time to display (in microseconds):");
			break;
		case 2:
			text1.setText(energy_text_low);
			text2.setText(energy_text_high);
			break;
		case 3:
			text1.setText("Smallest angle to display (in degrees):");
			text2.setText("Largest angle to display (in degrees):");
			break;
		}

		edit1.setText(value_1);
		edit2.setText(value_2);
		IsOpen = true;

		JPanel cont = new JPanel();
		cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
		cont.add(getLabelledPanel(edit1, text1.getText()));
		cont.add(getLabelledPanel(edit2, text2.getText()));
		
		JPanel pan = new JPanel();
		pan.setLayout(new BoxLayout(pan, BoxLayout.X_AXIS));
		pan.add(cancel);
		pan.add(ok);
		pan.setBorder(new BevelBorder(BevelBorder.LOWERED));

		this.add(cont, BorderLayout.NORTH);
		this.add(pan, BorderLayout.SOUTH);
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
		ShowWindow();
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
			ID = true;
			this.dispose();

		} else if (e.getSource().equals(cancel)) {
			ID = false;
			this.dispose();
		}
	}
	
	public static void main(String[] args){
		Param_Dialog p = new Param_Dialog(null, 1);
	}

}
