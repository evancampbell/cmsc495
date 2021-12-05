package main.controller;

import main.db.Site;
import main.db.User;
import main.properties.GlobalProperties;
import main.service.SSUserDetailsService;
import main.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class ScreenshotController {

    @Autowired
    private SSUserDetailsService userService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private GlobalProperties properties;

    @RequestMapping(value="/add", method = RequestMethod.POST)
    public ModelAndView add(@Valid Site site, Authentication auth) {
        String url = site.getUrl();
        // if url starts with http:// and/or wwww, remove them
        Pattern pattern = Pattern.compile("(https?:\\/\\/)?(www\\.)?([-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*))");
        Matcher m = pattern.matcher(url);
        if (m.find()) {
            url = m.group(3);
        }
        site.setUrl(url);
        String currentUsername = auth.getName();
        User user = userService.findUserByEmail(currentUsername);
        siteService.saveSite(site, user);

        ModelAndView model = new ModelAndView();
        String success = String.format("Successfully subscribed to %s!",site.getUrl());
        model.addObject("successAdd", success);
        model.setViewName("home");
        Set<Site> sites = user.getSites();
        model.addObject("sites", sites);
        return model;
    }

    @RequestMapping(value="/viewhistory", method = RequestMethod.GET)
    public ModelAndView viewHistory(@RequestParam String url) {
        Pattern pattern = Pattern.compile("(https?:\\/\\/)?(www\\.)?([-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*))");
        Matcher m = pattern.matcher(url);
        if (m.find()) {
            url = m.group(3);
        }
        Site site = siteService.findSiteByUrl(url);
        ArrayList<String> files = new ArrayList<String>();
        ArrayList<String> dates = new ArrayList<String>();
        if (site != null) {
            File dir = new File( properties.getCapturesDir() + url);
            String[] filenames = dir.list();
            for (String filename : filenames) {
                files.add(filename);
                long unix_time = Long.parseLong(filename.split("\\.")[0]);
                Date date = new Date();
                date.setTime(unix_time);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                String d = dateFormat.format(date);
                dates.add(d);
            }

        }
        ModelAndView model = new ModelAndView();
        model.addObject("dates",dates);
        model.addObject("files",files);
        model.addObject("url", url);
        model.setViewName("viewhistory");
        return model;
    }

    @RequestMapping(value="/unsubscribe", method=RequestMethod.POST)
    public RedirectView unsubscribe(@RequestParam String url, Authentication auth) {
        String currentUsername = auth.getName();
        siteService.removeUser(url, userService.findUserByEmail(currentUsername));
        return new RedirectView("/");
    }

}
