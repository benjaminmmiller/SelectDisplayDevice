package utils;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;

import org.openqa.selenium.WebDriver;

public class DisplayUtils {
	
	/**
	 * This method returns the top left corner of a given display device.
	 * @param displayIndex index of the desired display device
	 * @return
	 */
	public static Point getTopLeftCorner(int displayIndex) {
		GraphicsDevice[] devices = DisplayUtils.getGraphicsDevices();
		
		int xLoc = (int) Math.round(devices[displayIndex].getDefaultConfiguration().getBounds().getX());
		int yLoc = (int) Math.round(devices[displayIndex].getDefaultConfiguration().getBounds().getY());
		
		return new Point(xLoc, yLoc);
	}
	
	/**
	 * Returns the center point needed for a new window in a given display.
	 * @param displayOrigin the origin point of the display
	 * @param displayResolution the resolution of the display device
	 * @param windowResolution the resolution of the new window
	 * @return
	 */
	public static Point getCenterPointForWindow(Point displayOrigin, Dimension displayResolution, Dimension windowResolution) {
		//Get the center point of the display
		Point displayCenter = getDisplayCenterPoint(displayOrigin, displayResolution);
		int centerX = displayCenter.x;
		int centerY = displayCenter.y;
		
		//Adjust for the resolution of the window
		//Since the anchor point of the window is its top left corner, this needs to be done.
		centerX-=(windowResolution.width/2);
		centerY-=(windowResolution.height/2);
		
		return new Point(centerX, centerY);
	}
	
	/**
	 * Returns the center point of a given display device
	 * @param displayOrigin the origin point of the display.
	 * @param displayResolution the resolution of the display device
	 * @return
	 */
	public static Point getDisplayCenterPoint(Point displayOrigin, Dimension displayResolution) {
		//Start from the screen origin. Different for each display, but {0, 0} is the default display.
		int centerX = displayOrigin.x;
		int centerY = displayOrigin.y;
		
		//Go the half the size of the screen display
		centerX+= (displayResolution.width/2);
		centerY+=(displayResolution.height/2);

		return new Point(centerX, centerY);
	}
	
	
	/**
	 * Gets a formatted name for a given graphics device (monitor)
	 * Uses the following format: "Display [Monitor Number] [Monitor Width]x[Monitor Height] ([Default Device or Orientation (Left/Right) from Default Device])"
	 * @param device
	 * @return
	 */
	public static String formatDisplayNameForGraphicsDevice(GraphicsDevice device) {
		//Create formattedDisplayName that will be returned.
		String formattedDisplayName = "Display ";
		
		//Extract the digit from the deviceID, increment it by 1, and add it to the formattedDisplayName
		String unformattedDisplayID = device.getIDstring();
		String formattedDisplayNumber = "";
		formattedDisplayNumber = unformattedDisplayID.replaceAll("\\D+", formattedDisplayNumber);
		int displayNumber = Integer.valueOf(formattedDisplayNumber);
		displayNumber++;
		formattedDisplayName+=displayNumber;
		
		//Get the width and height of the device and add it to the formattedDisplayName
		int width = device.getDisplayMode().getWidth();
		int height = device.getDisplayMode().getHeight();
		formattedDisplayName+=" "+width+"x"+height;
		
		//Determine if the device is the default display device. If it is then add "Default Device" to the formattedDisplayName
		if(device.equals(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice())) {
			formattedDisplayName+= " (Default Device)";
		}
		//If it is not the default device then determine its orientation relative to the default device (left or right)
		else {
			double xBound = device.getDefaultConfiguration().getBounds().getX();
			if(xBound>0) {
				formattedDisplayName+= " (Right from Default Display)";
			}
			else {
				formattedDisplayName+= " (Left from Default Display)";
			}
		}
		return formattedDisplayName;
	}
	
	/**
	 * Gets the index for the default display device
	 * @return
	 */
	public static int getDefaultDisplayDeviceIndex() {
		GraphicsDevice[] devices = DisplayUtils.getGraphicsDevices();
		GraphicsDevice defaultDevice = DisplayUtils.getDefaultDisplayDevice();
		int defaultSelectedIndex = 0;
		for(int i=0;i<devices.length;i++) {
			if(devices[i].equals(defaultDevice)) {
				defaultSelectedIndex = i;
			}
		}
		return defaultSelectedIndex;
	}
	
	/**
	 * Returns the default display device
	 * @return the default GraphicsDevice found in the local GraphicsEnvironment
	 */
	public static GraphicsDevice getDefaultDisplayDevice() {
		GraphicsEnvironment graphicsE = GraphicsEnvironment.getLocalGraphicsEnvironment();
		return graphicsE.getDefaultScreenDevice();
	}
	
	/**
	 * Returns all the graphics devices found in the local graphics environment
	 * @return an array of all the GraphicsDevice found in the local GraphicsEnvironment
	 */
	public static GraphicsDevice[] getGraphicsDevices() {
		GraphicsEnvironment graphicsE = GraphicsEnvironment.getLocalGraphicsEnvironment();
		return graphicsE.getScreenDevices();
	}
	
	/**
	 * Returns all the formatted display names for all the graphics devices found in the local graphics environment.
	 * @return an array of String(s) containing the formatted display names of all the GraphicDevices in the local GraphicsEnviroment
	 */
	public static String[] getDisplayDevicesFormattedNames() {
		GraphicsDevice[] devices = DisplayUtils.getGraphicsDevices();
		String [] dropdownOptions = new String[devices.length];
		for(int i=0;i<devices.length;i++) {
			dropdownOptions[i] = DisplayUtils.formatDisplayNameForGraphicsDevice(devices[i]);
		}
		return dropdownOptions;
	}
	
	/**
	 * Moves a WebDriver instance to a particular display device.
	 * @param displayDeviceIndex the index of the DisplayDevice which the WebDriver will be moved to.
	 * @param wd the WebDriver to move.
	 */
	public static void moveWebDriverToDisplayDevice(int displayDeviceIndex, WebDriver wd) {
		Point topLeft = DisplayUtils.getTopLeftCorner(displayDeviceIndex);
		org.openqa.selenium.Point seleniumPoint =  new org.openqa.selenium.Point(topLeft.x, topLeft.y);
		wd.manage().window().setPosition(seleniumPoint);
	}
	
}
