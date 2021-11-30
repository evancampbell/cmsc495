package main.controller;

import main.ScreenshotRunnable;
import main.db.Site;
import main.service.EmailService;
import main.service.SSUserDetailsService;
import main.db.User;
import main.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import javax.validation.Valid;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MINUTES;

@Controller
public class LoginController {

    @Autowired
    private SSUserDetailsService userService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private EmailService emailService;

    ScheduledFuture<?> scheduledFuture;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        /*if (scheduledFuture.getDelay(TimeUnit.MILLISECONDS) <= 0) {

        }*/
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView register() {
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("register");
        return modelAndView;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());
        if (userExists != null) {
            bindingResult.rejectValue("email", "error.user", "There is already a user with that email.");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("register");
        } else {
            userService.saveUser(user,"user");
            modelAndView.addObject("successMessage", "New user registered");
            modelAndView.addObject("user", new User());
            modelAndView.setViewName("login");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public ModelAndView admin() {
        ThreadPoolTaskScheduler t = new ThreadPoolTaskScheduler();
        t.setPoolSize(1);
        t.setThreadNamePrefix("ThreadPoolTaskScheduler");
        t.initialize();

        ScreenshotRunnable runnable = new ScreenshotRunnable(siteService, emailService);

        //schedules the screenshotting every 1 minute
        scheduledFuture = t.schedule(runnable, new PeriodicTrigger(1, MINUTES));

        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("currentUser", user);
        modelAndView.setViewName("admin");
        return modelAndView;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView home(Authentication auth) {
        ModelAndView modelAndView = new ModelAndView();
        User user = userService.findUserByEmail(auth.getName());
        Set<Site> sites = user.getSites();
        modelAndView.addObject("sites", sites);
        modelAndView.setViewName("home");
        return modelAndView;
    }

}
