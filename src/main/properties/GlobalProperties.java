package main.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class GlobalProperties {

    @Value("${captures.dir}")
    private String capturesDir;

    @Value("${admin}")
    private String admin;

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Value("${password}")
    private String password;


    @Bean
    public String getCapturesDir() {
        return capturesDir;
    }

    public void setCapturesDir(String capturesDir) {
        this.capturesDir = capturesDir;
    }
}
