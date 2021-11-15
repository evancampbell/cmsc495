
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import com.mongodb.MongoClient;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.sun.net.httpserver.*;

import org.bson.Document;

public class Main {

	public static HashMap<String,ArrayList<Integer>> emails = new HashMap<String, ArrayList<Integer>>();
	public static ArrayList<String> urls = new ArrayList<String>();
	public static MongoClient mongoClient;
	public static MongoDatabase database;
	public static MongoCollection<Document> usersAndSites;

	public static void main(String[] args) {
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 6067), 0);
			server.createContext("/", new WebHandler());
			server.createContext("/res", new ResourceHandler());
			ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);
			server.setExecutor(threadPoolExecutor);
			server.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mongoClient = new MongoClient();
		database = mongoClient.getDatabase("ScreenshotDatabase");
		usersAndSites = database.getCollection("UsersAndSites");

		ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
		ScreenshotService ss = new ScreenshotService();

		//schedules the screenshotting every 1 minute
		ScheduledFuture<?> scheduledFuture = ses.scheduleAtFixedRate(ss, 0, 1, TimeUnit.MINUTES);
		
	}

	// subscribe the given user to the given url
	// TODO: this should be changed to a database insert
	public static void addSite(String url, String email) {
		Document site = new Document();
		site.append("url",url);
		site.append("email",email);
		usersAndSites.insertOne(site);
	}

	public static ArrayList<String> getAllSites() {
		ArrayList<String> sites = new ArrayList<String>();
		DistinctIterable<String> docs = usersAndSites.distinct("url", String.class);
		MongoCursor<String> results = docs.iterator();
		while (results.hasNext()) {
			sites.add(results.next());
		}

		return sites;
	}

	

	public static ArrayList<String> getSubscribedUsers(String site) {
		//TODO: this
		return new ArrayList<String>();
	}

	//for a given email address, gets all urls he's subscribed to
	//TODO: this shoudl be changed to a database retrieval
	public static ArrayList<String> getSubscribedSites(String user) {
		user = URLDecoder.decode(user);
		ArrayList<String> sites = new ArrayList<String>();
		DistinctIterable<String> docs = usersAndSites.distinct("url", Filters.eq("email", user), String.class);
		MongoCursor<String> results = docs.iterator();
		while (results.hasNext()) {
			sites.add(results.next());
		}
		
		return sites;
	}
        
		
	
		
}


	
	



