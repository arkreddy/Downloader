import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class NetworkDump {
	
	public static void main(String[] args) throws Exception {
System.out.println("Hello World!");
		
		System.setProperty("webdriver.chrome.driver",
                "/Users/rarumalla/chromedriver");    
		
		
		DesiredCapabilities caps = DesiredCapabilities.chrome();
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.PERFORMANCE, Level.INFO);
		caps.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
		WebDriver driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), caps);
		
		//getMovieLinksfromIdleBrain(driver, "http://www.idlebrain.com/movie/archive/index.html");
		
		driver.get("https://codoid.com/about-codoid/");
		 
		List<LogEntry> entries = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
		System.out.println(entries.size() + " " + LogType.PERFORMANCE + " log entries found");
		for (LogEntry entry : entries) {
		          System.out.println(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
		}
		
		
	}

}
