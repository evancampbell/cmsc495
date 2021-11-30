package main.config;

import main.CustomAuthenticationSuccessHandler;
import main.service.SSUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@ComponentScan("main")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    CustomAuthenticationSuccessHandler authSuccessHandler;

    @Bean
    public UserDetailsService mongoUserDetails() {
        return new SSUserDetailsService();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        UserDetailsService userDetailsService = mongoUserDetails();
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                //.antMatchers("/").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/").hasAuthority("user")
                .antMatchers("/add").hasAuthority("user")
                .antMatchers("/viewhistory").hasAuthority("user")
                .antMatchers("/register").permitAll()
                .antMatchers("/admin/**").hasAuthority("admin")
                .anyRequest().authenticated()
                .and().csrf().disable().formLogin().successHandler(authSuccessHandler)
                .loginPage("/login").failureUrl("/login?error=true")
                .usernameParameter("email")
                .passwordParameter("password")
                .and().logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login").and().exceptionHandling();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/resources/**");
    }
}
