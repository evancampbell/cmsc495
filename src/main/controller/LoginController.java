package main.controller;

import main.ScreenshotRunnable;
import main.db.User;
import main.service.EmailService;
import main.service.SSUserDetailsService;
import main.service.SiteService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.MINUTES;

@Controller
public class LoginController {

    @Autowired
    private SSUserDetailsService userService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private Logger logger;

    private ScheduledFuture<?> scheduledFuture;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView register() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("user", new User());
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
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors ) {
                logger.error(error.getObjectName() + " - " + error.getDefaultMessage() + "\n");
            }
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

        try {
            Path path = Path.of("src/main/resources/application.log");
            modelAndView.addObject("log", Files.readString(path));
        } catch (IOException e) {
            logger.error("couldn't get log file: " + e.getMessage() + "\n");
        }
        modelAndView.addObject("currentUser", userService.findUserByEmail(auth.getName()));
        modelAndView.addObject("users", userService.findAll());
        modelAndView.setViewName("admin");
        return modelAndView;
    }

    @RequestMapping(value = "/admin", method = RequestMethod.POST)
    public RedirectView deleteUser(@RequestParam("email") String email) {
        System.out.println(email);
        userService.deleteByEmail(email);
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        try {
            Path path = Path.of("src/main/resources/application.log");
            modelAndView.addObject("log", Files.readString(path));
        } catch (IOException e) {
            logger.error("couldn't get log file: " + e.getMessage() + "\n");
        }
        /*modelAndView.addObject("currentUser", userService.findUserByEmail(auth.getName()));
        modelAndView.addObject("users", userService.findAll());
        modelAndView.setViewName("admin");*/
        return new RedirectView("/admin");
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView home(Authentication auth) {
        ModelAndView modelAndView = new ModelAndView();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("sites", user.getSites());
        modelAndView.setViewName("home");
        return modelAndView;
    }

}
