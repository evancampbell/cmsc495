import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.io.File;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.net.httpserver.*;

public class WebHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String requestParamValue = "";
        if ("POST".equals(httpExchange.getRequestMethod())) {
            StringBuilder sb = new StringBuilder();
            System.out.println(httpExchange.getRequestBody());
            int i;
            while ((i = httpExchange.getRequestBody().read()) != -1) {
                sb.append((char) i);
            }
            String post = sb.toString();
            String[] parts = post.split("&");
            String user = "";
            String url = "";
            for (String part : parts) {
                String pair[] = part.split("=");
                if (pair[0].equals("email")) {
                    user = pair[1];
                } else if (pair[0].equals("url")) {
                    url = pair[1];
                } else if (pair[0].equals("submit")) {
                    if (pair[1].equals("Subscribe")) {
                        //remove the https:// from the url if present
                        /*String pattern = "(http[s]?://)(.+)";
                        if (Pattern.matches(pattern, url)) {
                            Pattern r = Pattern.compile(pattern);
                            Matcher m = r.matcher(url);
                            url = m.group(1);
                        }*/
                        Main.addSite(url, user);
                    } else if (pair[1].equals("Take+screenshot")) {

                    } else if (pair[1].equals("View+previous+screenshots")) {
                        
                        StringBuilder sb2 = new StringBuilder();
                        //get the all the file paths of the screen captures for that site
                        //TODO: show only the screenshots taken since user subscribed. this just shows ALL screenshots taken of a site
                        File dir = new File(Constants.CAPTURES_DIR + url); 
                        String[] filenames = dir.list();
                        for (String f : filenames) {
                            System.out.println("a");
                            // parse the date the capture was taken from its filename(unix timestamp)
                            long unix_time = Long.parseLong(f.split("\\.")[0]);
                            Date date = new Date ();
                            date.setTime(unix_time);
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss"); 
                            String d = dateFormat.format(date);
                            
                            //format an html img tag pointing to the screen capture, to insert into the html
                            sb2.append(
                                "<br/><br/><br/>" + d + "<a href='" + Constants.CAPTURES_DIR + "/" + url + "/" + f +"'><img src='"
                                    + Constants.CAPTURES_DIR + "/" + url + "/" + f + "' width=500 /></a>\n" );
                                    System.out.println("b");
                        }
                            
                        
                        requestParamValue = sb2.toString();
                    }
                }
                
            }

        }
        handleResponse(httpExchange, requestParamValue);
    }

    private String handleGetRequest(HttpExchange httpExchange) {
        return httpExchange.getRequestURI()
                .toString()
                .split("\\?")[1]
                .split("=")[1];
    }

    private void handleResponse(HttpExchange httpExchange, String requestParamValue) throws IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        String bytes = Files.readString(Paths.get("web/index.html"));
        // insert the list of screenshot img tags into the html
        String[] html = bytes.split("<span id='insert-images'></span>");
        String output = html[0] + requestParamValue + html[1];
        httpExchange.sendResponseHeaders(200, output.length());
        outputStream.write(output.getBytes());
        outputStream.flush();
        outputStream.close();
        
        
    }

    
}
