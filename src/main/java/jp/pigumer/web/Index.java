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
import jp.pigumer.mqtt.MqttProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(MqttProperties.class)
public class Index {

    @Autowired
    MqttProperties properties;

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
        String url = properties.getUrl();
        Resource caFile = properties.getCaFile();
        String userName = properties.getUsername();
        String password = properties.getPassword();
        return new Client(url, caFile, userName, password);
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Index.class, args);
    }
}
