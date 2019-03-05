package sbeam2.gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

public class TOF_Subtract_Dialog extends JDialog implements ActionListener{

	protected MainFrame parent;

	public JLabel title1;

	protected JLabel title2;
	protected JButton switch_button, ok, cancel;
	public boolean ID;
	protected boolean sameOrder;

	
	public TOF_Subtract_Dialog(MainFrame m, String s1, String s2) {
		// TODO Auto-generated constructor stub
		super(m);
		parent = m;

		title1 = new JLabel(s1);
		title2 = new JLabel(s2);
		
		switch_button = new JButton("switch");
		ok = new JButton("ok");
		cancel = new JButton("cancel");
		
		switch_button.addActionListener(this);
		ok.addActionListener(this);
		cancel.addActionListener(this);
	}

	protected void SetupWindow() {
		this.setTitle("Select Subtraction Order:");
		
		JPanel cont = new JPanel();
		cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
		cont.add(title1);
		cont.add(new JLabel("subtracted by"));
		cont.add(title2);
		cont.add(switch_button);

		JPanel pan = new JPanel();
		pan.setLayout(new BoxLayout(pan, BoxLayout.X_AXIS));
		pan.add(cancel);
		pan.add(ok);
		pan.setBorder(new BevelBorder(BevelBorder.LOWERED));
		
		this.getContentPane().add(cont, BorderLayout.NORTH);
		this.getContentPane().add(pan, BorderLayout.SOUTH);
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
		if (e.getSource().equals(switch_button)) {
			String temp = title1.getText();
			title1.setText(title2.getText());
			title2.setText(temp);
		} else if (e.getSource().equals(ok)) {
			// pass info back to brains and move on
			ID = true;
			this.dispose();

		} else if (e.getSource().equals(cancel)) {
			ID = false;
			this.dispose();
		}
	}

	public static void main(String[] args){
		TOF_Subtract_Dialog t = new TOF_Subtract_Dialog(null, "TOF 1", "TOF 2");
		t.Execute();
	}

}
