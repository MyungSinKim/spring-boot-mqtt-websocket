package jp.pigumer.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import jp.pigumer.job.IntervalJob;
import jp.pigumer.mqtt.Client;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@ComponentScan
@Controller
@EnableAutoConfiguration
@Configuration
@EnableScheduling
public class Index {
    
    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/json")
    @ResponseBody
    public Now now() {
        return new Now("2016/01/01 00:00:00");
    }
    
    @Bean
    public IntervalJob getJob() {
        return new IntervalJob();
    }
    
    @Bean
    public Client getClient() throws IOException {
        DefaultResourceLoader loader = new DefaultResourceLoader();
        Resource resource = loader.getResource("file:/var/lib/spring-boot-sample/mqtt.properties");
        Properties properties;
        try (InputStream is = resource.getInputStream()) {
            properties = new Properties();
            properties.load(is);
        }
        String url = properties.getProperty("url");
        String caFile = properties.getProperty("caFile");
        String userName = properties.getProperty("userName");
        String password = properties.getProperty("password");
        return new Client(url, caFile, userName, password);
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Index.class, args);
    }
}
