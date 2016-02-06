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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class Client implements MqttCallback {

    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    
    @Inject
    SimpMessagingTemplate template;
    
    MqttClient client;

    MqttConnectOptions options;
    
    String url;
    
    String caFile;

    String userName;
    
    char[] password;
    
    public Client(String url, String caFile, String userName, String password) {
        this.url = url;
        this.caFile = caFile;
        this.userName = userName;
        this.password = password.toCharArray();
    }
    
    void subscribe() throws Exception {
        client.connect(options);
        client.subscribe("test");
    }

    KeyStore loadKeyStore() throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
        X509Certificate cert;
        
        DefaultResourceLoader loader = new DefaultResourceLoader();
        Resource resource = loader.getResource(caFile);
        try (InputStream is = resource.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                PEMParser parser = new PEMParser(isr)) {
            X509CertificateHolder holder = (X509CertificateHolder) parser.readObject();
            cert = new JcaX509CertificateConverter().getCertificate(holder);
        }
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", cert);
        return keyStore;
    }

    TrustManagerFactory initTrustManagerFactory() throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(loadKeyStore());
        return tmf;
    }
    
    void createMqttConnectOptions() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, IOException, CertificateException {
        Security.addProvider(new BouncyCastleProvider());
        
        SSLContext context = SSLContext.getInstance("TLSv1.2");
        context.init(null, initTrustManagerFactory().getTrustManagers(), new SecureRandom());
        
        options = new MqttConnectOptions();
        options.setUserName(userName);
        options.setPassword(password);
        options.setSocketFactory(context.getSocketFactory());        
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
    
}
