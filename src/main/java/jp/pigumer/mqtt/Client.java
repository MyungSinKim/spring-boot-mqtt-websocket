/*
 * Copyright 2016 Pigumer Group.
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

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements MqttCallback {

    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    
    @Autowired
    SimpMessagingTemplate template;
    
    MqttClient client;

    MqttConnectOptions options;
    
    final String url;
    
    final Resource caFile;

    final String userName;
    
    final char[] password;
    
    public Client(String url, Resource caFile, String userName, String password) {
        this.url = url;
        this.caFile = caFile;
        this.userName = userName;
        this.password = password.toCharArray();
    }
    
    void subscribe() throws Exception {
        client.connect(options);
        client.subscribe("test");
    }

    Optional<KeyStore> loadKeyStore() {
        X509Certificate cert;

        if (caFile == null) {
            return Optional.empty();
        }
        try (InputStream is = caFile.getInputStream()) {
            InputStreamReader isr = new InputStreamReader(is);
            PEMParser parser = new PEMParser(isr);
            X509CertificateHolder holder = (X509CertificateHolder) parser.readObject();
            cert = new JcaX509CertificateConverter().getCertificate(holder);
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", cert);
            return Optional.of(keyStore);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "failed load", e);
            return Optional.empty();
        }
    }

    Optional<TrustManager[]> initTrustManagers() {
        return loadKeyStore().map(keyStore -> {
            try {
                Security.addProvider(new BouncyCastleProvider());
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(keyStore);
                return tmf.getTrustManagers();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "failed load", e);
                return null;
            }
        });
    }
    
    void createMqttConnectOptions() {
        Optional<SSLContext> context = initTrustManagers().map(trustManagers -> {
            try {
                SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, trustManagers, new SecureRandom());
                return sslContext;
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "failed load", e);
                return null;
            }
        });

        options = new MqttConnectOptions();
        options.setUserName(userName);
        options.setPassword(password);
        context.ifPresent(sslContext -> options.setSocketFactory(sslContext.getSocketFactory()));
    }

    void createMqttClient() throws MqttException {
        client = new MqttClient(url, MqttClient.generateClientId(), new MemoryPersistence());
        client.setCallback(this);
    }
    
    @PostConstruct
    public void afterPropertiesSet() throws Exception {        
        createMqttConnectOptions();
        createMqttClient();
        subscribe();
    }

    @PreDestroy
    public void destroy() throws Exception {
        if (null == client) return;
        try {
            if (client.isConnected()) {
                client.disconnect();
            }
        } finally {
            client.close();
        }
    }
    
    @Override
    public void connectionLost(Throwable cause) {
        LOGGER.log(Level.INFO, "connectionLost", cause);
        try {
            subscribe();
        } catch (Exception e) {
            LOGGER.log(Level.INFO, "connectionLost", e);
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        LOGGER.log(Level.INFO, String.format("%s: %s", topic, message));
        if (null != template) {
            template.convertAndSendToUser("testuser", "/queue/" + topic, message.toString());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        LOGGER.log(Level.INFO, token.toString());
    }

    public MqttClient getClient() {
        return client;
    }
}
