/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.pigumer.web;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class SubscribeListener implements ApplicationListener<SessionSubscribeEvent> {

    private static final Logger LOGGER = Logger.getLogger(SubscribeListener.class.getName());
    
    @Override
    public void onApplicationEvent(SessionSubscribeEvent e) {
        LOGGER.log(Level.INFO, e.toString());
    }
    
    
}
