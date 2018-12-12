package com.cit.services.notification;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@PropertySource("classpath:application.properties")
public class NotifierService implements INotifierService{

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private String mqttBroker;
    private String mqttTopic;
    private MqttPublish publisher;
    private List<MqttPublish> list = new ArrayList<>();


    /**
     * Constructor for MQTT notification service
     * @param mqttBroker  Url for the MQTT broker
     * @param mqttTopic   The topic name, which we should push the message to
     */
    @Autowired
    public NotifierService(String mqttBroker, String mqttTopic){
         this.mqttTopic=mqttTopic;
         this.mqttBroker=mqttBroker;
    }

    /**
     * This method sends an alert to a MQTT message broker
     * @param alert This is String message to send as an alert
     */
    public void publish(String alert){
        if (findAvailablepublisher()){
            publisher.publish(mqttTopic,alert);
        }else if (list.size()<20){
            publisher = MqttPublish.createInstance();
            list.add(publisher);
            publisher.process(mqttBroker,mqttTopic,alert);
        }else{
            findSmallestList();
            publisher.addToList(alert);
            //sort the list by number of messages and add to the one with least messages
        }
    }

    /**
     * finds the publisher with the least amount of work
     */
    private void findSmallestList(){
        Collections.sort(list);
        publisher = list.get(0);
    }


    /**
     * Check if there is a publisher available to send alert
     * @return true if a publisher is available
     */
    private boolean findAvailablepublisher(){
        boolean found = false;
        log.info(String.valueOf(list.size()));
        for (MqttPublish mqttPublish:list) {
            if (mqttPublish.isPublishAvailable()){
                if (log.isDebugEnabled()){
                    log.debug("found a publisher");
                }
                publisher = mqttPublish;
                found = true;
                break;
            }
        }
        return found;
    }

    /**
     *  Clear the list of publishers and if they are not connected
     */
    private void cleanList(){
        for (MqttPublish mqttPublish:list){
            if (!mqttPublish.isConnected()){
                mqttPublish = null;
                list.remove(mqttPublish);
            }
        }
    }
}