package jp.pigumer.web;

import java.security.Principal;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

public class UserHandshakeHandler extends DefaultHandshakeHandler {

    private static final Logger LOGGER = Logger.getLogger(UserHandshakeHandler.class.getName());
    
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        LOGGER.log(Level.INFO, "determineUser");
        return () -> "testuser";
    }
    
    
}
