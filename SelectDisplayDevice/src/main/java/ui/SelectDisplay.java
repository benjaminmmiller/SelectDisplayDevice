package ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import utils.DisplayUtils;


/**
 * Class for creating select display device UI.
 * This allows users to select an available display device and returns the selected device
 * Unfortunately display device manufacturer information cannot be retrieved using base Java. 
 * An external library or a lower level programming language must be used. 
 * See: https://stackoverflow.com/questions/34431014/using-java-to-gather-specific-system-information-monitor-screen-details
 * This is the point of the “Identify Display” button. 
 * This button opens windows on each monitor to tell the user their display device number. 
 */
public class SelectDisplay extends JComponent{
	final JFrame optionalDialogs;
	final JFrame mainWindowFrame; 
	protected volatile boolean isRunning = true;
	private static int selectedOption; 
	final GraphicsDevice[] devices;
	String dropdownOptions[];
	final int defaultSelectedIndex;
	JPanel mainPanel;
	GridBagConstraints constraints;
	
	public SelectDisplay(){
		this.devices= DisplayUtils.getGraphicsDevices();
		this.dropdownOptions = DisplayUtils.getDisplayDevicesFormattedNames();
		this.defaultSelectedIndex = DisplayUtils.getDefaultDisplayDeviceIndex();
		this.optionalDialogs = new JFrame();
		this.mainWindowFrame = new JFrame("Set Display");
		setupSwingUI();
		waitForSelection();
	}

	/**
	 * Setup all the swing UI components. 
	 */
	private void setupSwingUI() {
		this.mainPanel = new JPanel(new GridBagLayout());
		this.constraints = new GridBagConstraints();
		
		JComboBox setupDeviceDropdown = setupDeviceDropdown();
		setupIdentifyDisplayButton();
		setupSetDisplayButton(setupDeviceDropdown);
		setupAndPackWindow();
	}
	
	/**
	 * Creates a dropdown which contains all the possible display devices (monitors)
	 * @return the JComboBox dropdown that was created
	 */
	private JComboBox setupDeviceDropdown() {
		final JComboBox dropdown = new JComboBox(this.dropdownOptions);
		dropdown.setSelectedIndex(this.defaultSelectedIndex);
		this.constraints.fill = GridBagConstraints.HORIZONTAL;
		this.constraints.gridwidth = 2;
		this.constraints.gridx = 0;
		this.constraints.gridy = 0;
		this.constraints.insets = new Insets(25,50,25,50);
		this.mainPanel.add(dropdown, this.constraints);
		return dropdown;
	}
	
	/**
	 * Creates the button for identifying display devices.
	 * Once clicked launches a window on every display device and lists that display devices number.
	 * @return the JButton that was created.
	 */
	private JButton setupIdentifyDisplayButton() {
		this.constraints.insets = new Insets(0,50,25,50);
		JButton identifyDisplay = new JButton();
		identifyDisplay.setText("Identify Displays");
		this.constraints.fill = GridBagConstraints.HORIZONTAL;
		this.constraints.weightx = 0.5;
		this.constraints.gridx = 0;
		this.constraints.gridy = 1;
		this.constraints.gridwidth = 1;
		identifyDisplay.addActionListener(
				new ActionListener()
			    {
			      public void actionPerformed(ActionEvent e)
			      {
			    	  optionalDialogs.dispose();
			    	  for(int i=0;i<devices.length;i++) {
			  			addNewNonModalIdentifyDisplayWindow(DisplayUtils.getTopLeftCorner(i), devices[i], optionalDialogs);	
			  			}
			      }
			    }
				);
		this.mainPanel.add(identifyDisplay,this.constraints);
		return identifyDisplay;
	}
	
	/**
	 * Setup the set display button.
	 * This button gets the current value of the display devices dropdown.
	 * After doing this it closes the window and sents a notify to alert that a value has been selected.
	 * @param devicesDropdown the reference to the devicesDropdown
	 * @return
	 */
	private JButton setupSetDisplayButton(final JComboBox devicesDropdown) {
		JButton setButton = new JButton();
		setButton.setText("Set");
		this.constraints.fill = GridBagConstraints.HORIZONTAL;
		this.constraints.weightx = 0.5;
		this.constraints.gridx = 1;
		this.constraints.gridy = 1;
		this.constraints.gridwidth = 1;
		setButton.addActionListener(
				new ActionListener()
			    {
			      public void actionPerformed(ActionEvent e)
			      {
			    	  selectedOption = (Integer)devicesDropdown.getSelectedIndex();
			    	  System.out.println("Dropdown has value: "+selectedOption);
			    	  Frame[] allFrames = Frame.getFrames();
			    	  for(int i=0;i<allFrames.length;i++) {
			    		  allFrames[i].dispose();
			    	  }
			    	  synchronized (getOuter()) {
			    		  isRunning = false;
			    		  getOuter().notifyAll();
			    	  }
			      }
			    }
				);
		this.mainPanel.add(setButton,this.constraints);
		return setButton;
	}
	
	private void setupAndPackWindow() {
		GraphicsDevice defaultDevice = DisplayUtils.getDefaultDisplayDevice();
		this.mainWindowFrame.getContentPane().add(this.mainPanel);
		this.mainWindowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.mainWindowFrame.pack();
		this.mainWindowFrame.setMinimumSize(new Dimension(this.mainWindowFrame.getWidth(), this.mainWindowFrame.getHeight()));
		this.mainWindowFrame.setLocation(DisplayUtils.getCenterPointForWindow(DisplayUtils.getTopLeftCorner(defaultSelectedIndex), new Dimension(defaultDevice.getDisplayMode().getWidth(), defaultDevice.getDisplayMode().getHeight())
				, new Dimension(this.mainWindowFrame.getWidth(), this.mainWindowFrame.getHeight()) ));
		this.mainWindowFrame.setVisible(true);
	}
	
	/**
	 * Creates a new non-modal window for a given display device at a given point.
	 * Since the pop-up is non-modal, interaction with the main window is still possible with this window open.
	 * The pop-up contains information about the given display device.
	 * @param point the top left corner of the device
	 * @param device the display device to create the window on
	 * @param parentComponent the parent Swing component of the new window
	 * @return the JDialog created
	 */
	private static JDialog addNewNonModalIdentifyDisplayWindow(Point point, GraphicsDevice device, Component parentComponent) {
		JOptionPane pane = new JOptionPane(new JLabel(DisplayUtils.formatDisplayNameForGraphicsDevice(device)), JOptionPane.INFORMATION_MESSAGE,JOptionPane.NO_OPTION,null, new String[]{"Close"});
		JDialog dialog = pane.createDialog(parentComponent, DisplayUtils.formatDisplayNameForGraphicsDevice(device));
		Point centerPoint = DisplayUtils.getCenterPointForWindow(point, new Dimension(device.getDisplayMode().getWidth(),device.getDisplayMode().getHeight()), new Dimension(dialog.getWidth(),dialog.getHeight()));
		dialog.setLocation(centerPoint);
		dialog.setModal(false);
		dialog.setVisible(true);
		return dialog;
	}
	
	/**
	 * Wait until the "Set" button is clicked.
	 */
	public void waitForSelection() {
		synchronized(this) {
			while(this.isRunning) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("Done waiting");
	}
	
	protected SelectDisplay getOuter() {
		return SelectDisplay.this;
	}
	
	public int getSelectedOption() {
		return selectedOption;
	}

	public static void setSelectedOption(int selectedOption) {
		SelectDisplay.selectedOption = selectedOption;
	}
}
