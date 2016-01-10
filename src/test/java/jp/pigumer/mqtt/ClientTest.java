package jp.pigumer.mqtt;

import java.security.KeyStore;
import java.util.UUID;
import javax.net.ssl.TrustManagerFactory;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class ClientTest {

    @Test
    public void loadKeyStoreTest() throws Exception {
       Client sut = new Client();
       KeyStore keyStore = sut.loadKeyStore("classpath:/sample.ks", "sample123".toCharArray());
       assertThat(keyStore, is(not(nullValue())));
    }
    
    @Test
    public void initTrustManagerFactoryTest() throws Exception {
        Client sut = new Client();
        KeyStore keyStore = sut.loadKeyStore("classpath:/sample.ks", "sample123".toCharArray());
        assertThat(keyStore, is(not(nullValue())));
        TrustManagerFactory tmf = sut.initTrustManagerFactory(keyStore);
        assertThat(tmf, is(not(nullValue())));
    }
    
    @Test
    public void createMqttConnectOptionsTest() throws Exception {
        Client sut = new Client();
        KeyStore keyStore = sut.loadKeyStore("classpath:/sample.ks", "sample123".toCharArray());
        assertThat(keyStore, is(not(nullValue())));
        TrustManagerFactory tmf = sut.initTrustManagerFactory(keyStore);
        assertThat(tmf, is(not(nullValue())));

        sut.createMqttConnectOptions(tmf, "username", "password".toCharArray());
        assertThat(sut.options, is(not(nullValue())));
        
        sut.createMqttClient("ssl://localhost:8883", UUID.randomUUID().toString());
        
//        try {
//            sut.subscribe();
//        } finally {
//            sut.destroy();
//        }
    }

}
