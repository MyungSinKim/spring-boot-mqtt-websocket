package jp.pigumer.mqtt;

import java.util.UUID;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class Client implements MqttCallback, InitializingBean, DisposableBean {

    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    
    @Inject
    SimpMessagingTemplate template;
    
    MqttClient client;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        String mqttHost = System.getenv("MQTTHOST");
        mqttHost = null != mqttHost ? mqttHost : "raspberrypi.local";
        String mqttPort = System.getenv("MQTTPORT");
        mqttPort = null != mqttPort ? mqttPort : "1883";
        String url = "tcp://" + mqttHost + ":" + mqttPort;
        String clientId = UUID.randomUUID().toString();
        client = new MqttClient(url, clientId);
        client.setCallback(this);
        
        client.connect();
        
        client.subscribe("test");
    }

    @Override
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
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        LOGGER.info(topic + ": " + message);
        template.convertAndSend("/topic/" + topic, message.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
    
}
