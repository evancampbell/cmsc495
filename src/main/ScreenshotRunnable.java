package main;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.imageio.ImageIO;


import io.github.bonigarcia.wdm.WebDriverManager;
import main.db.Site;
import main.db.User;
import main.service.EmailService;
import main.service.SiteService;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import org.springframework.stereotype.Component;

import static io.github.bonigarcia.wdm.config.DriverManagerType.CHROME;

@Component
public class ScreenshotRunnable implements Runnable {
	private SiteService siteService;
	private EmailService emailService;
	private String capturesDir = "src/main/resources/captures/";

	public ScreenshotRunnable(SiteService siteService, EmailService emailService) {
		super();
		this.siteService = siteService;
		this.emailService = emailService;
	}

	public void run() {
		WebDriverManager.getInstance(CHROME).setup();
		WebDriver driver = new ChromeDriver();
		driver.manage().window().maximize();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		List<Site> sites = siteService.findAll();

		System.setProperty("java.awt.headless", "false");

		for (Site site : sites) {
			String url = site.getUrl();
			driver.get("https://" + url);
			try {
				Robot robot = new Robot(); 

				// if directory for the site doesnt exist, create it
				File directory = new File(capturesDir + url);
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
				File file = new File(capturesDir + url + "/" + timestamp.getTime() +".png");
				boolean status;
				status = ImageIO.write(bufferedImage, "png", file);

                Set<User> users = site.getUsers();
                for (User user : users) {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                    String time = dateFormat.format(timestamp);
                    emailService.sendMail(user.getEmail(), time, url, capturesDir + url + "/" + timestamp.getTime() +".png");
                }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
		driver.quit();
	}

	
	
}