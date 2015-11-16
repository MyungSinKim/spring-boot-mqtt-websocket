package jp.pigumer.web;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Component
public class SubscribeHandler {
 
    private static final Logger LOGGER = Logger.getLogger(SubscribeHandler.class.getName());
    
    @SubscribeMapping("/queue/{path}")
    public void subscribe(@DestinationVariable String path) {
        LOGGER.log(Level.INFO, String.format("subscribe: %s", path));
    }

}
