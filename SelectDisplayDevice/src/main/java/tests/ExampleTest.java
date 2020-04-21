package tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.Test;

import ui.SelectDisplay;
import utils.DisplayUtils;

public class ExampleTest {

	/**
	 * Test that shows how the SelectDisplay UI can be created 
	 * and how the WebDriver can be moved to a different display device
	 */
	@Test
	public void selectDisplayAndStartScript() {
		SelectDisplay selectDisplay = new SelectDisplay();
		WebDriver wd = new FirefoxDriver();
		DisplayUtils.moveWebDriverToDisplayDevice(selectDisplay.getSelectedOption(), wd);
		wd.get("http://newtours.demoaut.com/mercurywelcome.php");
	}
}
