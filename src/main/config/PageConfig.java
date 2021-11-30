package main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.GzipResourceResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class PageConfig implements WebMvcConfigurer {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/admin").setViewName("admin");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/viewhistory").setViewName("viewhistory");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                //.addResourceHandler("/**") // « /css/myStatic.css
                .addResourceHandler("/resources/**") // « /static/css/myStatic.css
                .addResourceLocations("classpath:/resources/") // Default Static Loaction
                .setCachePeriod( 3600 )
                .resourceChain(true) // 4.1
                .addResolver(new PathResourceResolver()); //4.1




    }
}
