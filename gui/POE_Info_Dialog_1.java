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
import javax.swing.border.BevelBorder;
import javax.swing.text.NumberFormatter;

public class POE_Info_Dialog_1 extends JDialog implements ActionListener{

	protected boolean IsOpen;

	protected JFormattedTextField title_edit, min_energy_edit, max_energy_edit,
			num_points_edit;
	protected JLabel min_poss_energy_static;

	protected JLabel energy_units1, energy_units2;
	protected JLabel min_energy_line1, min_energy_line2, min_energy_line3;
	protected JButton ok, cancel;
	
	protected String units_string;
	protected boolean ClearEnergyLines;
	protected float min_possible_energy;
	protected MainFrame parent;
	
	public boolean ID;
	

	public POE_Info_Dialog_1(MainFrame p) {
		// TODO Auto-generated constructor stub
		super(p);
		parent = p;

		IsOpen = false;
		this.setTitle("Energy Distribution Info:");

		NumberFormat format = NumberFormat.getNumberInstance();
		format.setGroupingUsed(false);
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Float.class);
		// If you want the value to be committed on each keystroke instead of
		// focus lost
		formatter.setCommitsOnValidEdit(true);
		title_edit = new JFormattedTextField();
		min_energy_edit = new JFormattedTextField(formatter);
		max_energy_edit = new JFormattedTextField(formatter);
		num_points_edit = new JFormattedTextField(formatter);

		// The following are used to restrict the input field of the edit
		// windows in the dialog.

		min_poss_energy_static = new JLabel();
		energy_units1 = new JLabel();
		energy_units2 = new JLabel();
		min_energy_line1 = new JLabel();
		min_energy_line2 = new JLabel();
		min_energy_line3 = new JLabel();
		ClearEnergyLines = false;
		
		ok = new JButton("ok");
		cancel = new JButton("cancel");
		ok.addActionListener(this);
		cancel.addActionListener(this);
	}

	public void SetMinPossibleEnergy(float min_energy) {
		min_possible_energy = min_energy;
	}

	public void SetTitle(String input_text) {
		title_edit.setText(input_text);
	}

	public void SetMinEnergy(String input_text) {
		this.min_energy_edit.setText(input_text);
	}

	public void SetMaxEnergy(String input_text) {
		this.max_energy_edit.setText(input_text);
	}

	public void SetNumPoints(String input_text) {
		this.num_points_edit.setText(input_text);
	}

	public void SetEnergyUnits(String input_text) {
		units_string = input_text;
	}

	public String GetTitle() {
		return title_edit.getText();
	}

	public String GetMinEnergy() {
		return min_energy_edit.getText();
	}

	public String GetMaxEnergy() {
		return max_energy_edit.getText();
	}

	public String GetNumPoints() {
		return num_points_edit.getText();
	}

	public boolean GetStatus() {
		return IsOpen;
	}

	public void SetClearEnergyLines() {
		ClearEnergyLines = true;
	}

	protected void SetupWindow() {
		String static_text_min;
		String static_text_add = ".  All points below this";

		String static_text_2;

		static_text_min = "" + min_possible_energy + " " + units_string
				+ static_text_add;

		static_text_2 = "(in " + units_string + "):";

		if (ClearEnergyLines) {
			min_energy_line1.setText("");
			min_energy_line2.setText("");
			min_energy_line3.setText("");
		} else {
			min_poss_energy_static.setText(static_text_min);
		}
		energy_units1.setText(static_text_2);
		energy_units2.setText(static_text_2);

		JPanel cont = new JPanel();
		cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
		cont.add(getLabelledPanel(title_edit, "Title"));
		cont.add(getLabelledPanel(min_energy_edit, "Minimum Energy"));
		cont.add(getLabelledPanel(max_energy_edit, "Maximum Energy"));
		cont.add(getLabelledPanel(num_points_edit, "Number of Points"));
		
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
			IsOpen = false;
			ID = true;
			this.dispose();
		} else if (e.getSource().equals(cancel)) {
			IsOpen = false;
			ID = false;
			this.dispose();
		}
	}
	
	public static void main(String[] args){
		POE_Info_Dialog_1 p = new POE_Info_Dialog_1(null);
		p.Execute();
	}

}
