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
package jp.pigumer.job;

import jp.pigumer.mqtt.Client;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.UUID;

public class IntervalJob {

    private static Logger log = LoggerFactory.getLogger(IntervalJob.class);

    @Autowired
    Client client;

    @Scheduled(fixedDelay = 5000)
    public void job() {
        log.info("job");
        try {
            client.getClient().publish("test", new MqttMessage(UUID.randomUUID().toString().getBytes()));
        } catch (MqttException e) {
            log.warn("publish", e);
        }
    }

}
