package com.cit.services.notification;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;

@Service
@PropertySource("classpath:application.properties")
public class NotifierService implements INotifierService{

    private String mqttBroker = "tcp://40.121.20.223:8883";
    private String mqttTopic  = "validation.alerts.bravo";


    @Autowired
    public NotifierService(String mqttBroker, String mqttTopic){
         this.mqttTopic=mqttTopic;
         this.mqttBroker=mqttBroker;
    }

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private MqttPublish publisher;

    private List<MqttPublish> list = new ArrayList<>();

    public void publish(String alert){
        if (publisherAvailable()){
            publisher.publish(mqttTopic,alert);
        }else{
            publisher = MqttPublish.createInstance();
            generateClientId();
            list.add(publisher);
            publisher.process(mqttBroker,mqttTopic,alert);
        }
    }

    private void generateClientId(){
        String generatedString = generateSafeToken();
        log.debug("new client ID is: {}",generatedString);
        publisher.setClientId(generatedString);
    }

    private boolean publisherAvailable(){
        boolean found = false;
        log.debug("publisher list Size : {}",list.size());
        for (MqttPublish i:list) {
            if (i.isPublishAvailable()){
                if (log.isDebugEnabled()){
                    log.debug("found a publisher");
                }
                publisher = i;
                found = true;
                break;
            }
        }
         return found;
    }

    private String generateSafeToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[15];
        random.nextBytes(bytes);
        Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }
}