package sbeam2.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import com.apple.eawt.Application;

import sbeam2.SBApp;





public class MainFrame extends JFrame implements ActionListener, FocusListener{

	protected JMenuBar menubar;
	protected JLabel xposLabel, yPosLabel;
	protected JLabel SumSquare_text_gadget;
	public SBApp brains;
	private JMenu tof, poe, ang;
	private JMenuItem[] tempItems;
	private JMenu tempMenu;
	protected JDesktopPane pane;
	protected JInternalFrame focused;
	protected Logger logMe;
	ArrayList<JInternalFrame> internalFrames = new ArrayList<JInternalFrame>();
	ArrayList<JDialog> dialogs = new ArrayList<JDialog>();
	
	public MainFrame() throws HeadlessException {
		// TODO Auto-generated constructor stub
	}

	public MainFrame(GraphicsConfiguration gc) {
		super(gc);
		// TODO Auto-generated constructor stub
	}

	public MainFrame(String title) throws HeadlessException {
		super(title);
		// TODO Auto-generated constructor stub
	}
	
	public MainFrame(String title, SBApp sb) throws HeadlessException {
		super(title);
		brains = sb;
		logMe = Logger.getLogger(SBApp.class.getName());
		
		logMe.log(Level.INFO, "Sbeam starting");
	}

	public MainFrame(String title, GraphicsConfiguration gc) {
		super(title, gc);
		// TODO Auto-generated constructor stub
	}

	public void initStuff() {
		this.setSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize());
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		menubar = new JMenuBar();
		
		JMenu menu = new JMenu("File");
		JMenuItem item = new JMenuItem("New");
		item.addActionListener(this);
		menu.add(item);
		item = new JMenuItem("Open");
		item.addActionListener(this);
		menu.add(item);
		item = new JMenuItem("Save");
		item.addActionListener(this);
		menu.add(item);
		item = new JMenuItem("Save As");
		item.addActionListener(this);
		menu.add(item);
		item = new JMenuItem("Save In Compatibility Mode");
		item.addActionListener(this);
		menu.add(item);
		menubar.add(menu);
		
		menu = new JMenu("Instr. Param.");
		item = new JMenuItem("Set Instrumental Parameters");
		item.addActionListener(this);
		menu.add(item);
		item = new JMenuItem("Output Parameters to File");
		item.addActionListener(this);
		menu.add(item);
		menubar.add(menu);
		
		menu = new JMenu("TOF");
		item = new JMenuItem("Display New TOF");
		item.addActionListener(this);
		menu.add(item);
		item = new JMenuItem("Display Loaded TOF");
		item.addActionListener(this);
		menu.add(item);
		item = new JMenuItem("Delete Loaded TOFs");
		item.addActionListener(this);
		menu.add(item);
		menu.addSeparator();
		item = new JMenuItem("Output TOF for Graphing");
		item.addActionListener(this);
		menu.add(item);
		menu.addSeparator();
		item = new JMenuItem("Invert TOF to P(E)");
		item.addActionListener(this);
		menu.add(item);
		item = new JMenuItem("Perform TOF Subtraction");
		item.addActionListener(this);
		menu.add(item);
		item = new JMenuItem("Perform TOF Addition");
		item.addActionListener(this);
		menu.add(item);
		item = new JMenuItem("Smooth TOF");
		item.addActionListener(this);
		menu.add(item);
		item = new JMenuItem("Remove TOF Background");
		item.addActionListener(this);
		menu.add(item);
		item = new JMenuItem("Restore Original TOF");
		item.addActionListener(this);
		menu.add(item);
		menubar.add(menu);
		tof = menu;

		menu = new JMenu("P(E)");
		JMenu sub = new JMenu("Create New P(E)");
		menu.add(sub);
		item = new JMenuItem("Graphically");
		item.addActionListener(this);
		sub.add(item);
		item = new JMenuItem("Output P(E) to File");
		item.addActionListener(this);
		menu.add(item);
		item = new JMenuItem("Open & Display *.poe File");
		item.addActionListener(this);
		menu.add(item);
		item = new JMenuItem("Set Energy Units");
		item.addActionListener(this);
		menu.add(item);
		menu.addSeparator();
		item = new JMenuItem("Display Stored P(E)");
		item.addActionListener(this);
		menu.add(item);
		item = new JMenuItem("Delete Stored P(E)");
		item.addActionListener(this);
		menu.add(item);
		menubar.add(menu);
		poe = menu;
		
		menu = new JMenu("Ang. Distr.");
		item = new JMenuItem("Load Angular Data");
		item.addActionListener(this);
		menu.add(item);
		item = new JMenuItem("Show New Angular Distribution");
		item.addActionListener(this);
		menu.add(item);
		item = new JMenuItem("Display Loaded Angular Distribution");
		item.addActionListener(this);
		menu.add(item);
		item = new JMenuItem("Delete Angular Distribution");
		item.addActionListener(this);
		menu.add(item);
		menu.addSeparator();
		item = new JMenuItem("Output Angular Distribution");
		item.addActionListener(this);
		menu.add(item);
		menubar.add(menu);
		ang = menu;
		
		menu = new JMenu("Convolution");
		item = new JMenuItem("Calculate TOFs for a given m/e");
		item.addActionListener(this);
		menu.add(item);
		item = new JMenuItem("Change Calculation Parameters");
		item.addActionListener(this);
		menu.add(item);
		item = new JMenuItem("Show Residual");
		item.addActionListener(this);
		menu.add(item);
		menubar.add(menu);
		
		menu = new JMenu("Window");
		item = new JMenuItem("Cascade");
		item.addActionListener(this);
		menu.add(item);
		item = new JMenuItem("Tile Vertically");
		item.addActionListener(this);
		menu.add(item);
		item = new JMenuItem("Tile Horizontally");
		item.addActionListener(this);
		menu.add(item);
		item = new JMenuItem("Arrange Icons");
		item.addActionListener(this);
		menu.add(item);
		item = new JMenuItem("Close All");
		item.addActionListener(this);
		menu.add(item);
		menubar.add(menu);
		
		pane = new JDesktopPane();
		pane.setBackground(getForeground());
		
		xposLabel = new JLabel(""+0);
		yPosLabel = new JLabel(""+0);
		xposLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		yPosLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		JPanel sb = new JPanel();
		sb.setLayout(new BoxLayout(sb, BoxLayout.X_AXIS));
		sb.setBorder(new BevelBorder(BevelBorder.LOWERED));
		sb.setPreferredSize(new Dimension(this.getWidth(), 20));
		sb.add(new JLabel("X Pos:\t"));
		sb.add(xposLabel);
		sb.add(new JLabel("\tY Pos:\t"));
		sb.add(yPosLabel);

		this.add(sb, BorderLayout.SOUTH);
		this.setJMenuBar(menubar);
		this.add(pane);
	}
	
	public void addFrame(JInternalFrame fr){
		this.internalFrames.add(fr);
		pane.add(fr);
	}
	
	public void addDialog(JDialog d){
		this.dialogs.add(d);
	}
	

	public void Execute(){
		this.initStuff();
		this.pack();
		this.setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		if(e.getSource() instanceof JMenuItem){
			JMenuItem i = (JMenuItem)e.getSource();
			try{
				if(tempItems != null && Arrays.asList(tempItems).contains(((JMenuItem)e.getSource()))){
					Method m = focused.getClass().getDeclaredMethod(i.getText().replaceAll("[^\\w]", "").replaceAll("/", ""));
					logMe.log(Level.INFO, "Calling: " + i.getText());
					m.invoke(focused);
				}else{
					Method m = brains.getClass().getDeclaredMethod(i.getText().replaceAll("[^\\w]", "").replaceAll("/", ""));
					logMe.log(Level.INFO, "Calling: " + i.getText());
					m.invoke(brains);
				}
			}catch(NoSuchMethodException ex){
				System.out.println("no method " + i.getText().replaceAll("[^\\w]", ""));
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1){
				System.out.println("This error:\t" + e1.getMessage());
				logMe.log(Level.SEVERE, "Error executing: " + i.getText(), e1.getMessage());
			}
		}else{
			System.out.println("Not a menu item");
		}
	}

	public static void main(String[] args){
		MainFrame mainWindow = new MainFrame("Single Beam Convolution Program");
		mainWindow.initStuff();
		mainWindow.pack();
		mainWindow.setVisible(true);

	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		if(tempItems != null){
			System.out.println("Focus lost: " + focused.getTitle());
			focused = null;
			
			for(int loopMe = 0; loopMe < tempItems.length; loopMe++){
				tempMenu.remove(tempItems[loopMe]);
			}
			tempMenu.remove(tempMenu.getMenuComponent(tempMenu.getItemCount()-1));
			tempMenu = null;
			tempItems = null;
		}
		
		
		String[] names;
		focused = (JInternalFrame)e.getSource();
		System.out.println("Focus gained: " + focused.getTitle());
		if(e.getSource() instanceof TOFView){
			tempMenu = tof;
			names = new String[]{"Axis Range", "Set Colors" , "TOF Scaling Information" , "Append New TOF", "Append Loaded TOF", "Display Residual", "Remove TOF From Display", "Edit/View TOF Parameters"};
		}else if(e.getSource() instanceof POEView){
			tempMenu = poe;
			names = new String[]{"Axis Range", "Set Colors", "Append Stored P(E)", "Freeze P(E)'s", "Unfreeze P(E)'s", "Remove P(E) From Display"};
		}else if(e.getSource() instanceof AngView){
			tempMenu = ang;
			names = new String[]{"Axis Range", "Set Colors", "Append New Angular Distribution", "Append Loaded Angular Distribution", "Remove Angular Distribution From Display"};
		}else{
			return;
		}
		
		tempMenu.addSeparator();
		tempItems = new JMenuItem[names.length];
		for(int loopMe = 0; loopMe < names.length; loopMe++){
			JMenuItem item = new JMenuItem(names[loopMe]);
			item.addActionListener(this);
			tempItems[loopMe] = item;
			tempMenu.add(item);
			
		}
		this.pack();
	}

	public void internalClosed(JInternalFrame f){
		if (f.equals(focused)) {
			if (tempItems != null) {
				System.out.println("Focus lost: " + focused.getTitle());
				focused = null;

				for (int loopMe = 0; loopMe < tempItems.length; loopMe++) {
					tempMenu.remove(tempItems[loopMe]);
				}
				tempMenu.remove(tempMenu.getMenuComponent(tempMenu
						.getItemCount() - 1));
				tempMenu = null;
				tempItems = null;
			}
		}
	}
	
	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

}
