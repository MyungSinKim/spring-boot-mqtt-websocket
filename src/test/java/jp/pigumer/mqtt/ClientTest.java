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
package jp.pigumer.mqtt;

import java.io.InputStream;
import java.security.KeyStore;
import java.util.Properties;
import javax.net.ssl.TrustManagerFactory;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

public class ClientTest {

    Client sut;
    
    @Before
    public void setUp() throws Exception {
        DefaultResourceLoader loader = new DefaultResourceLoader();
        Resource resource = loader.getResource("file:/var/lib/spring-boot-sample/mqtt.properties");
        Properties properties = new Properties();
        try (InputStream is = resource.getInputStream()) {
           properties.load(is);
        }
        String url = properties.getProperty("url", "ssl://localhost:8883");
        String caFile = properties.getProperty("caFile", "file:/var/lib/spring-boot-sample/ca.crt");
        String userName = properties.getProperty("userName", "username");
        String password = properties.getProperty("password", "password");
        sut = new Client(url, caFile, userName, password);
    }
    
    @Test
    public void loadKeyStoreTest() throws Exception {
       KeyStore keyStore = sut.loadKeyStore();
       assertThat(keyStore, is(not(nullValue())));
    }
    
    @Test
    public void initTrustManagerFactoryTest() throws Exception {
        TrustManagerFactory tmf = sut.initTrustManagerFactory();
        assertThat(tmf, is(not(nullValue())));
    }
    
    @Test
    public void createMqttConnectOptionsTest() throws Exception {
        sut.createMqttConnectOptions();
        assertThat(sut.options, is(not(nullValue())));
        
        sut.createMqttClient();
        try {
            sut.subscribe();
            sut.client.publish("topic/test", new MqttMessage("message".getBytes("UTF-8")));
            Thread.sleep(5000);
        } finally {
            sut.destroy();
        }
    }

}
