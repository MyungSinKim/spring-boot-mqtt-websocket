package jp.pigumer.web.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

@Component
@ServerEndpoint(value="/sample")
public class SampleServer {
    
    private static final Logger LOGGER = 
            Logger.getLogger(SampleServer.class.getName());
    
    static Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
    
    public SampleServer() {
        LOGGER.log(Level.INFO, "{0}", this.toString());
    }
    
    @OnOpen
    public void onOpen(Session session) throws Exception {
        LOGGER.info("open: " + session.getId());
        sessions.add(session);
    }
    
    @OnClose
    public void onClose(Session session) {
        LOGGER.info("close: " + session.getId());
        sessions.remove(session);
    }

}
