package com.cit.services.notification;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageActionListener implements IMqttActionListener {
    /**
     * Member Vars
     */
    private final String messageText;
    private final String topic;
    private final String userContext;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Constructor
     */
    MessageActionListener(String topic, String messageText, String userContext) {
        this.topic = topic;
        this.messageText = messageText;
        this.userContext = userContext;
    }

    /**
     * OnSuccess runs if once message is verified delivered
     *
     * @param asyncActionToken Token generated by sending, used for response validation
     */
    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        if ((asyncActionToken != null) && asyncActionToken.getUserContext().equals(userContext))
        {
            log.info( "Message '{}' published to topic '{}'", messageText, topic);
        }
    }

    /**
     * onFailure runs if message cannot be delivered
     *
     * @param asyncActionToken Token generated by sending, used for response validation
     */
    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        log.error("Threw an Exception in MessageActionListener::onFailure, full stack trace follows:",exception);
    }
}