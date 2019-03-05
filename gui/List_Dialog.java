package sbeam2.gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;

public class List_Dialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String[] text_of_list;
	protected int chosen_index;

	public JList<String> list_box;
	protected JButton ok, cancel;

	protected String dialog_caption;

	protected boolean ListBoxNewedInternally;
	protected MultiListSelectionModel model;
	protected int selections;
	
	public boolean ID;
	

	public List_Dialog(MainFrame par, String[] ls, int numSelections) {
		super(par);
		ListBoxNewedInternally = false;
		selections = numSelections;
		model = new MultiListSelectionModel();
		if (selections == 0)
			model.setCheck(false);
		model.setLimit(numSelections);
		list_box = new JList<String>();
		list_box.setSelectionModel(model);

		ok = new JButton("ok");
		cancel = new JButton("cancel");

		text_of_list = ls;
	}

	public void SetCaption(String input_text) {
		dialog_caption = input_text;
	}

	void SetListBoxList(String[] list_text) {
		text_of_list = list_text;
	}

	public int[] GetChosenIndex() {
		return list_box.getSelectedIndices();
	}

	protected void CmOk() {
		chosen_index = list_box.getSelectedIndex();
		this.dispose();
	}

	protected void CmCancel() {
		this.dispose();
	}

	protected void SetupWindow() {
		// This must be new-ed here since other classes are daughter classes
		// of this which use a different definition of list_box. Thus, this
		// cannot be new-ed in the constructor nor deleted in the destructor
		ListBoxNewedInternally = true;
		list_box.setListData(text_of_list);
		JScrollPane scr = new JScrollPane(list_box);

		ok.addActionListener(this);
		cancel.addActionListener(this);
		JPanel pan = new JPanel();
		pan.setLayout(new BoxLayout(pan, BoxLayout.X_AXIS));
		pan.add(cancel);
		pan.add(ok);
		pan.setBorder(new BevelBorder(BevelBorder.LOWERED));

		this.setTitle(dialog_caption);
		this.getContentPane().add(scr, BorderLayout.NORTH);
		this.getContentPane().add(pan, BorderLayout.SOUTH);
	}

	public static void main(String[] args) {
		String[] ls = { "test1", "test2", "test3", "test4", "test5", "test1",
				"test2", "test3", "test4", "test5", "test1", "test2", "test3",
				"test4", "test5" };
		List_Dialog d = new List_Dialog(null, ls, 0);
	}

	public void Execute() {
		this.setPreferredSize(new Dimension(400, 200));
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

			ID = true;
			model.clearSels();
			this.dispose();
		} else if (e.getSource().equals(cancel)) {
			ID = false;
			model.clearSels();
			this.dispose();
		}

	}
}
