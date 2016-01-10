package jp.pigumer.mqtt;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class Client implements MqttCallback {

    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    
    @Inject
    SimpMessagingTemplate template;
    
    MqttClient client;

    MqttConnectOptions options;
    
    void subscribe() throws Exception {
        client.connect(options);
        client.subscribe("topic/test");
    }

    KeyStore loadKeyStore(String file, char[] password) throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
        DefaultResourceLoader loader = new DefaultResourceLoader();
        Resource resource = loader.getResource(file);
        try (InputStream is = resource.getInputStream()) {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(is, password);
            return keyStore;
        }
    }

    TrustManagerFactory initTrustManagerFactory(KeyStore keyStore) throws KeyStoreException, NoSuchAlgorithmException {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);
        return tmf;
    }
    
    void createMqttConnectOptions(TrustManagerFactory tmf, String username, char[] password) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext context = SSLContext.getInstance("TLSv1.2");
        context.init(null, tmf.getTrustManagers(), new SecureRandom());
        
        options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password);
        options.setSocketFactory(context.getSocketFactory());
        
    }

    void createMqttClient(String url, String clientId) throws MqttException {
        client = new MqttClient(url, clientId);
        client.setCallback(this);
    }
    
    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        String mqttHost = System.getenv("MQTTHOST");
        String mqttPort = System.getenv("MQTTPORT");
        String username = System.getenv("MQTTUSER");
        char[] password = System.getenv("MQTTPASS").toCharArray();
        String keystoreFile = System.getenv("MQTTKEYSTORE");
        char[] keystorePassword = System.getenv("MQTTKEYSTOREPASSWORD").toCharArray();
        
        mqttPort = null != mqttPort ? mqttPort : "8883";
        String url = "ssl://" + mqttHost + ":" + mqttPort;
        String clientId = UUID.randomUUID().toString();

        KeyStore keyStore = loadKeyStore(keystoreFile, keystorePassword);
        TrustManagerFactory tmf = initTrustManagerFactory(keyStore);
        
        createMqttConnectOptions(tmf, username, password);

        createMqttClient(url, clientId);
        
        subscribe();
    }

    @PreDestroy
    public void destroy() throws Exception {
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
        template.convertAndSendToUser("testuser", "/queue/" + topic, message.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        LOGGER.log(Level.INFO, token.toString());
    }
    
}
