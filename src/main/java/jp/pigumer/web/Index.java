/*
 * Copyright 2016 Pigumer Group Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.pigumer.web;

import jp.pigumer.job.IntervalJob;
import jp.pigumer.mqtt.Client;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Controller
@SpringBootApplication
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
