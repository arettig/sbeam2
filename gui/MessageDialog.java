package sbeam2.gui;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class MessageDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected boolean IsOpen;
	protected JLabel message_line, percent_line;


	public MessageDialog(JFrame parent)
	{
		IsOpen = false;
		this.setTitle("Calculating TOF:");
		message_line = new JLabel(); 
		percent_line = new JLabel();
		this.add(message_line);
		this.add(percent_line);
	}


	public void SetMessage(String message)
	{
		String text_1;
		text_1 = "Calculating:  ";
		text_1 += message;
		message_line.setText(text_1);
	}

	public void SetPercent(int percent_completed)
	{
		String text_1;
		text_1 = "Portion of velocity segments completed for this calculation:  " + percent_completed + "%";
		percent_line.setText(text_1);
	}

	public void ShowWindow()
	{
		IsOpen = true;
		this.pack();
		this.setVisible(true);
	}
	public boolean GetStatus() {return IsOpen;}

	public void CloseWindow()
	{
		IsOpen = false;
		this.setVisible(false);
	}


}
