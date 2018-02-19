package sbeam2.gui;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.util.Arrays;

import javafx.scene.layout.Border;
import sbeam2.CalcData;
import sbeam2.POECalcData;
import sbeam2.POEData;
import sbeam2.SBApp;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.NumberFormatter;

public class ContribPOEsDialog extends JDialog implements ActionListener,
		ListSelectionListener, ChangeListener {

	protected MainFrame parent;
	protected boolean IsOpen;

	protected JFormattedTextField beta_edit, mass1_edit, mass2_edit,
			chan_weight_edit;
	protected JLabel calc_title_static, poe_title_static, ion_m_e_static,
			num_ion_chan_static;
	protected JList<String> contrib_poe_listbox, non_contrib_poe_listbox,
			ion_chan_listbox;
	protected JButton add_poe_button, remove_poe_button, ok, cancel;
	protected JSpinner numChan;
	// protected TUpDownMouseMove *numChan;

	protected SBApp application;
	protected CalcData calculation;
	protected int calc_num;

	protected String calc_title;
	protected String poe_title;
	protected String[] ion_channel_list;

	protected int num_ion_channels, old_num_channels;

	protected POECalcData[] poe_info;

	protected int num_total_poes, num_contrib_poes, old_sel_poe_num,
			old_sel_ion_index;

	public boolean ID;
	
	
	public ContribPOEsDialog(MainFrame p, SBApp app, CalcData calc) {
		// TODO Auto-generated constructor stub
		super(p);
		parent = p;

		int i, j, ion_channels;
		POECalcData[] calc_poe_calc_data;

		IsOpen = false;

		application = app;
		calculation = calc;
		calc_poe_calc_data = calculation.temp_data_for_poes;

		calc_title = "";
		poe_title = "";

		calc_num = calculation.CalcNumber;
		num_total_poes = calculation.num_total_poes;

		NumberFormat format = NumberFormat.getNumberInstance();
		format.setGroupingUsed(false);
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Float.class);
		// If you want the value to be committed on each keystroke instead of
		// focus lost
		formatter.setCommitsOnValidEdit(true);
		beta_edit = new JFormattedTextField(formatter);
		beta_edit.setText("0");
		mass1_edit = new JFormattedTextField(formatter);
		mass1_edit.setText("0");
		mass2_edit = new JFormattedTextField(formatter);
		mass2_edit.setText("0");
		chan_weight_edit = new JFormattedTextField(formatter);
		chan_weight_edit.setText("1");

		calc_title_static = new JLabel();
		poe_title_static = new JLabel();
		ion_m_e_static = new JLabel();
		num_ion_chan_static = new JLabel();

		contrib_poe_listbox = new JList<String>(new DefaultListModel<String>());
		non_contrib_poe_listbox = new JList<String>(
				new DefaultListModel<String>());
		ion_chan_listbox = new JList<String>(new DefaultListModel<String>());
		contrib_poe_listbox.addListSelectionListener(this);
		ion_chan_listbox.addListSelectionListener(this);

		add_poe_button = new JButton("Add P(E)");
		add_poe_button.addActionListener(this);
		remove_poe_button = new JButton("Remove P(E)");
		remove_poe_button.addActionListener(this);

		numChan = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
		numChan.addChangeListener(this);

		ok = new JButton("ok");
		cancel = new JButton("cancel");
		ok.addActionListener(this);
		cancel.addActionListener(this);

		// The following are used to restrict the input field of the edit
		// windows in the dialog.

		old_sel_ion_index = 0;
		ion_channel_list = null;

		// Initialize poe_info and copy information from calculation
		// poe_calc_data
		poe_info = new POECalcData[num_total_poes];
		for (i = 0; i < num_total_poes; i++) {
			poe_info[i] = new POECalcData();
			poe_info[i].beta_param = calc_poe_calc_data[i].beta_param;
			ion_channels = calc_poe_calc_data[i].num_channels;
			poe_info[i].num_channels = ion_channels;
			poe_info[i].is_included = calc_poe_calc_data[i].is_included;

			poe_info[i].mass_ratio = null;
			if (ion_channels == 0) {
				poe_info[i].mass_1 = null;
				poe_info[i].mass_2 = null;
				poe_info[i].rel_weight = null;
				// poe_info[i].mass_ratio = null;
			} else {
				poe_info[i].mass_1 = new float[ion_channels];
				poe_info[i].mass_2 = new float[ion_channels];
				poe_info[i].rel_weight = new float[ion_channels];
				for (j = 0; j < ion_channels; j++) {
					poe_info[i].mass_1[j] = calc_poe_calc_data[i].mass_1[j];
					poe_info[i].mass_2[j] = calc_poe_calc_data[i].mass_2[j];
					poe_info[i].rel_weight[j] = calc_poe_calc_data[i].rel_weight[j];
					// poe_info[i].mass_ratio[j] =
					// calc_poe_calc_data[i].mass_ratio[j];
				} // End of loop over ion_channels
			} // End of else statement
		} // End of loop over info for all possible P(E)'s

		String m_e_static_text;

		this.setTitle("Choose contributing P(E)'s:");

		calc_title_static.setText(calc_title);

		numChan.setEnabled(false);
		beta_edit.setEnabled(false);
		ion_chan_listbox.setEnabled(false);
		mass1_edit.setEnabled(false);
		mass2_edit.setEnabled(false);
		chan_weight_edit.setEnabled(false);

		m_e_static_text = " amu";
		ion_m_e_static.setText(m_e_static_text);

		num_total_poes = application.poes.size();

		old_sel_poe_num = -1; // No P(E) number is previously selected
		FillContribListBoxes(-1);
		if (num_contrib_poes > 0) {
			contrib_poe_listbox.setSelectedIndex(0);
		}
	}

	public void SetCalculationTitle(String input_text) {
		calc_title = input_text;
	}

	public void SetIon_m_e(String input_text) {
		ion_m_e_static.setText(input_text);
	}

	protected void SetupWindow() {
		JPanel top = new JPanel();
		TitledBorder t = BorderFactory.createTitledBorder(
				BorderFactory.createEmptyBorder(), "title");
		t.setTitlePosition(TitledBorder.TOP);
		top.setBorder(t);
		top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
		top.add(new JScrollPane(contrib_poe_listbox));
		JPanel temp = new JPanel();
		temp.setLayout(new BoxLayout(temp, BoxLayout.Y_AXIS));
		temp.add(add_poe_button);
		temp.add(remove_poe_button);
		top.add(temp);
		top.add(new JScrollPane(non_contrib_poe_listbox));

		JPanel mid = new JPanel();
		mid.setBorder(BorderFactory
				.createTitledBorder("Click on a contributing P(E) to change the following information:"));
		mid.setLayout(new BorderLayout());
		mid.add(getLabelledPanel(numChan, "Number of Ionization Channels"),
				BorderLayout.WEST);
		mid.add(getLabelledPanel(beta_edit,
				"Anisotropy Parameter (β)\nbetween -1 and 2"),
				BorderLayout.EAST);

		JPanel botMid = new JPanel();
		botMid.setBorder(BorderFactory
				.createTitledBorder("Parameters for ionization channels:"));
		botMid.setLayout(new BorderLayout());
		botMid.add(new JScrollPane(ion_chan_listbox), BorderLayout.WEST);
		temp = new JPanel();
		temp.setLayout(new BorderLayout());
		temp.add(getLabelledPanel(mass1_edit, "Mass 1"), BorderLayout.NORTH);
		temp.add(getLabelledPanel(mass2_edit, "Mass 2"), BorderLayout.CENTER);
		temp.add(getLabelledPanel(chan_weight_edit, "Channel Weight"),
				BorderLayout.SOUTH);
		chan_weight_edit.setPreferredSize(new Dimension(50, chan_weight_edit.getPreferredSize().height));
		botMid.add(temp, BorderLayout.EAST);
		mid.add(botMid, BorderLayout.SOUTH);

		JPanel pan = new JPanel();
		pan.add(ok);
		pan.add(cancel);
		pan.setBorder(new BevelBorder(BevelBorder.LOWERED));

		this.add(top, BorderLayout.NORTH);
		this.add(mid, BorderLayout.CENTER);
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

	protected void addPOE() {
		if (non_contrib_poe_listbox.getModel().getSize() == 0
				|| non_contrib_poe_listbox.isSelectionEmpty()) {
			return;
		}
		int i, selected_index, chosen_poe = -1;
		int count = 0;

		// Find out which one is selected
		selected_index = non_contrib_poe_listbox.getSelectedIndex();
		((DefaultListModel<String>) contrib_poe_listbox.getModel())
				.addElement(((DefaultListModel<String>) non_contrib_poe_listbox
						.getModel()).remove(selected_index));
		if (selected_index < 0)
			return;

		i = 0;
		while (count <= selected_index) {
			if (poe_info[i].is_included == false) {
				if (count < selected_index) {
					count++;
				} else {
					poe_info[i].is_included = true;
					chosen_poe = i;
					count++;
				}
			}
			i++;
		}
		contrib_poe_listbox.setSelectedIndex(0);
		// FillContribListBoxes(chosen_poe);
	}

	/*
	 * protected void CmRemovePOEFromList(); protected void EvMouseMove(int
	 * modKeys, Point point); protected void EvLbnDblClkContrib(); protected
	 * void EvLbnDblClkNonContrib(); protected void EvLbnSelChangeContrib();
	 * protected void EvLbnSelChangeIonChan();
	 */
	protected int GetSelContribPOE() {
		int i, selected_index, chosen_poe = 0;
		int count = 0;

		selected_index = contrib_poe_listbox.getSelectedIndex();
		if (selected_index < 0)
			return selected_index;

		i = 0;
		while (count <= selected_index) {
			if (poe_info[i].is_included == true) {
				if (count < selected_index) {
					count++;
				} else {
					chosen_poe = i;
					count++;
				}
			}
			i++;
		}
		return chosen_poe;
	}

	protected void removePOE() {
		if (contrib_poe_listbox.getModel().getSize() == 0
				|| contrib_poe_listbox.isSelectionEmpty()) {
			return;
		}
		int i, selected_index, chosen_poe = 0, chosen_channel;
		int count = 0;

		// Find out which one is selected

		selected_index = contrib_poe_listbox.getSelectedIndex();

		if (selected_index < 0) // i.e. if none is selected
			return;

		((DefaultListModel<String>) non_contrib_poe_listbox.getModel())
				.addElement(((DefaultListModel<String>) contrib_poe_listbox
						.getModel()).remove(selected_index));
		i = 0;
		while (count <= selected_index) {
			if (poe_info[i].is_included == true) {
				if (count < selected_index) {
					count++;
				} else {
					poe_info[i].is_included = false;
					chosen_poe = i;
					count++;
				}
			}
			i++;
		}
		// Set data in POE_calc_data for this selected P(E)
		poe_info[chosen_poe].beta_param = parseFloat(beta_edit.getText());

		chosen_channel = ion_chan_listbox.getSelectedIndex();

		if (chosen_channel >= 0) {
			poe_info[chosen_poe].mass_1[chosen_channel] = parseFloat(mass1_edit.getText());
			poe_info[chosen_poe].mass_2[chosen_channel] = parseFloat(mass2_edit.getText());
			poe_info[chosen_poe].rel_weight[chosen_channel] = parseFloat(chan_weight_edit.getText());
		}

		// FillContribListBoxes(-1);

		// setText other boxes since no P(E) will be selected after one is
		// removed
		//ion_chan_listbox.setListData(new String[0]);
		((DefaultListModel)ion_chan_listbox.getModel()).clear();
		beta_edit.setText("");
		//ion_chan_listbox.setListData(new String[0]);
		mass1_edit.setText("");
		mass2_edit.setText("");
		chan_weight_edit.setText("");

		poe_title_static.setText("");
		numChan.setEnabled(false);
		beta_edit.setEnabled(false);
		ion_chan_listbox.setEnabled(false);
		mass1_edit.setEnabled(false);
		mass2_edit.setEnabled(false);
		chan_weight_edit.setEnabled(false);
		

		old_sel_poe_num = -1; // No P(E) is currently selected
	}

	protected void FillIonChanListBox(int poe_number) {
		int current_num_channels, i;
		current_num_channels = poe_info[poe_number].num_channels;
		((DefaultListModel<String>)ion_chan_listbox.getModel()).clear();
		if (current_num_channels > 0) {
			//ion_channel_list = new String[current_num_channels];
			//ion_chan_listbox.setListData(ion_channel_list);
			ion_chan_listbox.setEnabled(true);
			for (i = 1; i <= current_num_channels; i++) {
				//ion_channel_list[i - 1] = ;
				 ((DefaultListModel<String>) ion_chan_listbox.getModel())
				 .addElement("Channel #" + i);
			}
			/*//ion_chan_listbox.setSelectedIndex(0);
			//old_sel_ion_index = 0;

			poe_info[poe_number].mass_1[0] = parseFloat(mass1_edit
					.getText());
			//mass1_edit.setEnabled(true);
			poe_info[poe_number].mass_2[0] = parseFloat(mass2_edit
					.getText());
			//mass2_edit.setEnabled(true);
			poe_info[poe_number].rel_weight[0] = parseFloat(chan_weight_edit.getText());
			//chan_weight_edit.setEnabled(true);*/
		}
	}

	protected void FillContribListBoxes(int poe_number) { // new_poe is position
															// of newly added
															// P(E) in list box
		int i, new_poe = 0;
		int count;
		POEData poe;

		count = 0;
		for (i = 0; i < num_total_poes; i++) {
			poe = application.poes.get(i);
			if (poe_info[i].is_included == false) {
				((DefaultListModel<String>) non_contrib_poe_listbox.getModel())
						.addElement(poe.title);
			} else {
				((DefaultListModel<String>) contrib_poe_listbox.getModel())
						.addElement(poe.title);
				if (i == poe_number)
					new_poe = count;
				count++;
			}
		}

		num_contrib_poes = count;
		if (poe_number < 0) // Won't select a specific P(E) if -1 is passed to
							// this function
			return;
		else {
			contrib_poe_listbox.setSelectedIndex(new_poe);
		}// -1 means ignore poe_number
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(ok)) {
			// pass info back to brains and move on
			// Send this new data to the Calc_data object
			ID = true;
			int chosen_poe, chosen_channel/* , i */;

			// Get any data which is displayed in the dialog but may not be
			// stored yet
			// Set data in POE_calc_data for this selected P(E)
			chosen_poe = GetSelContribPOE();
			if (chosen_poe >= 0) {
				chosen_channel = ion_chan_listbox.getSelectedIndex();
				if (chosen_channel >= 0) {
					poe_info[chosen_poe].mass_1[chosen_channel] = parseFloat(mass1_edit.getText());
					poe_info[chosen_poe].mass_2[chosen_channel] = parseFloat(mass2_edit.getText());
					poe_info[chosen_poe].rel_weight[chosen_channel] = parseFloat(chan_weight_edit.getText());
				}
			}

			// Send this new data to the Calc_data object
			calculation.SetPOECalcData(poe_info);
			IsOpen = false;
			this.dispose();
		} else if (e.getSource().equals(cancel)) {
			IsOpen = false;
			ID = false;
			this.dispose();
		} else if (e.getSource().equals(add_poe_button)) {
			addPOE();
		} else if (e.getSource().equals(remove_poe_button)) {
			removePOE();
		}
	}

	public static void main(String[] args) {
		ContribPOEsDialog c = new ContribPOEsDialog(null, null, null);
		c.Execute();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub
		if(e.getValueIsAdjusting()) return;
		if (e.getSource().equals(ion_chan_listbox)) {
			int chosen_poe, chosen_channel;

			chosen_poe = GetSelContribPOE();
			if (chosen_poe < 0)
				return;
		
			poe_info[chosen_poe].mass_1[old_sel_ion_index] = parseFloat(mass1_edit.getText());
			poe_info[chosen_poe].mass_2[old_sel_ion_index] = parseFloat(mass2_edit.getText());
			poe_info[chosen_poe].rel_weight[old_sel_ion_index] = parseFloat(chan_weight_edit.getText());

			chosen_channel = ion_chan_listbox.getSelectedIndex();
			if(chosen_channel < 0){
				ion_chan_listbox.setSelectedIndex(0);
				chosen_channel = 0;
			}

			mass1_edit.setText("");
			mass2_edit.setText("");
			chan_weight_edit.setText("");

			mass1_edit.setEnabled(true);
			mass2_edit.setEnabled(true);
			chan_weight_edit.setEnabled(true);

			mass1_edit
					.setText("" + poe_info[chosen_poe].mass_1[chosen_channel]);
			mass2_edit
					.setText("" + poe_info[chosen_poe].mass_2[chosen_channel]);
			chan_weight_edit.setText(""
					+ poe_info[chosen_poe].rel_weight[chosen_channel]);

			old_sel_ion_index = chosen_channel;
		} else {
			POEData poe;
			int poe_number, chosen_channel = 0;

			if (old_sel_poe_num >= 0) {
				poe_info[old_sel_poe_num].beta_param = parseFloat(beta_edit.getText());

				chosen_channel = ion_chan_listbox.getSelectedIndex();
				if (!ion_chan_listbox.isSelectionEmpty()) {
					System.out.println("storing");
					poe_info[old_sel_poe_num].mass_1[chosen_channel] = parseFloat(mass1_edit.getText());
					poe_info[old_sel_poe_num].mass_2[chosen_channel] = parseFloat(mass2_edit.getText());
					poe_info[old_sel_poe_num].rel_weight[chosen_channel] = parseFloat(chan_weight_edit.getText());
				}
			}


			poe_number = GetSelContribPOE();
			System.out.println(old_sel_poe_num + ":" + poe_number);
			poe_title_static.setText("");
			if (contrib_poe_listbox.isSelectionEmpty()) {
				numChan.setValue(1);
				//ion_chan_listbox.setListData(new String[0]);
				beta_edit.setText("0");
				//ion_chan_listbox.setListData(new String[0]);
				mass1_edit.setText("0");
				mass2_edit.setText("0");
				chan_weight_edit.setText("1");

				poe_title_static.setText("");
				numChan.setEnabled(false);
				beta_edit.setEnabled(false);
				ion_chan_listbox.setEnabled(false);
				mass1_edit.setEnabled(false);
				mass2_edit.setEnabled(false);
				chan_weight_edit.setEnabled(false);

				old_sel_poe_num = -1;
				return;
			}

			if (poe_info[poe_number].num_channels == 0) // Disable some things
			{
				//ion_chan_listbox.setListData(new String[0]);
				mass1_edit.setText("0");
				mass2_edit.setText("0");
				chan_weight_edit.setText("1");

				ion_chan_listbox.setEnabled(false);
				mass1_edit.setEnabled(false);
				mass2_edit.setEnabled(false);
				chan_weight_edit.setEnabled(false);
			}

			poe = application.poes.get(poe_number);

			poe_title = poe.title;
			poe_title_static.setText(poe_title);
			numChan.setEnabled(true);
			numChan.setValue(poe_info[poe_number].num_channels);
			old_num_channels = -1; // Used so new # of channels will be
									// displayed
			mass1_edit.setText("" + poe_info[poe_number].mass_1[chosen_channel]);
			mass2_edit.setText("" + poe_info[poe_number].mass_2[chosen_channel]);
			chan_weight_edit.setText("" + poe_info[poe_number].rel_weight[chosen_channel]);

			beta_edit.setEnabled(true);
			beta_edit.setText("");
			beta_edit.setText("" + poe_info[poe_number].beta_param);

			//ion_chan_listbox.setListData(new String[0]);
			FillIonChanListBox(poe_number);

			// Make this current poe_number the old_poe_number for later use
			old_sel_poe_num = poe_number;
			ion_chan_listbox.setSelectedIndex(0);
		}

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Spinner");
		int i, current_num_channels, num_old_poe_chans, last_chan;
		int current_poe;
		float[] mass_1_array;
		float[] mass_2_array;
		float[] chan_weight_array;

		current_poe = GetSelContribPOE();
		if (current_poe < 0) {
			return;
		}

		// Get data for old POE_calc_data before changing # of ion_channels

		current_num_channels = (int) numChan.getValue();
		num_old_poe_chans = poe_info[current_poe].num_channels;

		if (num_old_poe_chans != current_num_channels) {
			if (mass1_edit.isEnabled()) {
				poe_info[current_poe].mass_1[old_sel_ion_index] = parseFloat(mass1_edit.getText());
				poe_info[current_poe].mass_2[old_sel_ion_index] = parseFloat(mass2_edit.getText());
				poe_info[current_poe].rel_weight[old_sel_ion_index] = parseFloat(chan_weight_edit.getText());
			}

			mass_1_array = new float[current_num_channels];
			mass_2_array = new float[current_num_channels];
			chan_weight_array = new float[current_num_channels];

			last_chan = Math.min(num_old_poe_chans, current_num_channels);
			// Copy data from old P(E) data and then delete old stuff
			for (i = 0; i < current_num_channels; i++) {
				if (i < last_chan) {
					mass_1_array[i] = poe_info[current_poe].mass_1[i];
					mass_2_array[i] = poe_info[current_poe].mass_2[i];
					chan_weight_array[i] = poe_info[current_poe].rel_weight[i];
				} else {
					mass_1_array[i] = 1.0f;
					mass_2_array[i] = 1.0f;
					chan_weight_array[i] = 1.0f;
				}
			}

			if (num_old_poe_chans > 0) {
				poe_info[current_poe].mass_1 = null;
				poe_info[current_poe].mass_2 = null;
				poe_info[current_poe].rel_weight = null;
			}

			// Set old poe_info to point to this new information
			poe_info[current_poe].mass_1 = mass_1_array;
			poe_info[current_poe].mass_2 = mass_2_array;
			poe_info[current_poe].rel_weight = chan_weight_array;

			poe_info[current_poe].num_channels = current_num_channels;

			if(current_num_channels > num_old_poe_chans){
				((DefaultListModel<String>) ion_chan_listbox.getModel()).addElement("Channel #"+(ion_chan_listbox.getModel().getSize()+1));
			}else{
				((DefaultListModel<String>) ion_chan_listbox.getModel()).remove(ion_chan_listbox.getModel().getSize()-1);

			}
			old_num_channels = (int) numChan.getValue();
		}
	}
	
	private float parseFloat(String s){
		if(s == null || s.equals("")){
			return 0;
		}
		return Float.parseFloat(s);
	}
}
