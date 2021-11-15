import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class ScreenshotService implements Runnable {
	private HashMap<String,ArrayList<Integer>> emails = new HashMap<String, ArrayList<Integer>>();
	private ArrayList<String> urls = new ArrayList<String>();

	public void run() {
		System.setProperty("webdriver.chrome.driver", "chromedriver");
		WebDriver driver = new ChromeDriver();
		driver.manage().window().maximize();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		ArrayList<String> urls = Main.getAllSites();
		for (String url : urls) {
			driver.get("https://"+ url);
			try {
				Robot robot = new Robot(); 

				// if directory for the site doesnt exist, create it
				File directory = new File(Constants.CAPTURES_DIR + url);
				if (!directory.exists()) {
					directory.mkdir();
				}

				//get the dimensions/position of the browser window
				//we don't want to capture the entire screen
				Dimension d = driver.manage().window().getSize();
				Point p = driver.manage().window().getPosition();
				Rectangle rectangle = new Rectangle(p.x, p.y, d.width, d.height);
				//take the screenshot and save it
				BufferedImage bufferedImage = robot.createScreenCapture(rectangle);
				File file = new File(Constants.CAPTURES_DIR + url + "/" + timestamp.getTime() +".png");
				boolean status;
				status = ImageIO.write(bufferedImage, "png", file);

				//TODO: set up SMTP server and configure emailing and stuff
				/*String from = "evan.campbell.t@gmail.com";
				String host = "localhost:6067";
				Properties properties = System.getProperties();
				properties.setProperty("mail.smtp.host", host);
				Session session = Session.getDefaultInstance(properties);
			
				try {
					// Create a default MimeMessage object.
					MimeMessage message = new MimeMessage(session);
					message.setFrom(new InternetAddress(from));
					message.addRecipient(Message.RecipientType.TO, new InternetAddress("evan.campbell.t@gmail.com"));
					message.setSubject("Screenshot taken");
					message.setText("Hello. This is your screenshot.");
		   
					// Send message
					Transport.send(message);
				 } catch (MessagingException mex) {
					mex.printStackTrace();
				 }*/
				 
			} catch (IOException e) {
				e.printStackTrace();
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
		driver.quit();
	}

	
	
}