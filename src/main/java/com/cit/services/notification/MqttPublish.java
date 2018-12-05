package com.cit.services.notification;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.Async;

import java.io.UnsupportedEncodingException;

@Async
@Scope(value="prototype", proxyMode= ScopedProxyMode.TARGET_CLASS)
public class MqttPublish implements IMqttPublish {

    /**
     * Member Vars
     */
    private String topic = null;
    private static final String ENCODING = "UTF-8";
    private String name;
    private String clientId = null;
    private MqttAsyncClient client;
    private IMqttToken connectToken;
    private String message =null;
    private String userContext = "default";
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private boolean available;
    /**
     * Getters/setters
     */
    public String getUserContext() {
        return userContext;
    }

    public void setUserContext(String userContext) {
        this.userContext = userContext;
    }

    public String getClientId() { return clientId; }

    public void setClientId(String clientId) { this.clientId = clientId; }

    /**
     * Constructors
     */
    MqttPublish() { }
    public MqttPublish(String name) { this.name = name; }

    /**
     * Factory method - create an instance of a publisher.
     *
     * @return - new instance.
     */
    public static MqttPublish createInstance()
    {
        return new MqttPublish();
    }

    /**
     * Initialize a connection with the given MQTT Broker
     */
    @Override
    public void connect(String broker)
    {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            MemoryPersistence memoryPersistence = new MemoryPersistence();
            if (this.clientId == null){
                this.clientId = MqttAsyncClient.generateClientId();
                if (log.isDebugEnabled()){
                    log.debug("Had to set clientID using generateClient");
                }
            }
            if (log.isDebugEnabled()) {
                log.debug(this.clientId);
            }
            client = new MqttAsyncClient(broker, this.clientId, memoryPersistence);
            client.setCallback(this);
            connectToken = client.connect(options, null, this);
        } catch (MqttException e) {
            log.error("Threw an Exception in MqttPublish::connect, full stack trace follows:", e);
        }
    }

    @Override
    public boolean isConnected() {
        return (client != null) && (client.isConnected());
    }


    public void process(String mqttBroker, String mqttTopic, String mqttMessage){
        connect(mqttBroker);
        this.topic = mqttTopic;
        this.message = mqttMessage;
    }

    @Override
    public void connectionLost(Throwable cause) {
        // The MQTT client lost the connection
        log.error("Threw an Exception in MqttPublish::connectionLost, full stack trace follows:",cause);
    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        if (asyncActionToken.equals(connectToken)) {


            if ((this.topic != null) && (this.message != null)){
                publish(this.topic,this.message);
            }


            if (log.isInfoEnabled()) {
                log.info("Connection made");
            }

        }
    }

    public boolean isPublishAvailable(){
        return isConnected() && available;
    }

    /**
     * Publish a message on the given topic to the MQTT Broker
     *
     * @param strMqttTopic MQTT Topic to publish to
     * @param strMessage Message to publish
     */
    @Override
    public MessageActionListener publish(final String strMqttTopic, final String strMessage)
    {
        this.topic = strMqttTopic;
        byte[] bytesStrMessage;
        available = false;
        try {
            bytesStrMessage = strMessage.getBytes(ENCODING);
            MessageActionListener actionListener = new MessageActionListener(strMqttTopic, strMessage, userContext);
            client.publish(strMqttTopic, new MqttMessage(bytesStrMessage), userContext,	actionListener);
            return actionListener;
        } catch (UnsupportedEncodingException e) {
            log.error("Threw an UnsupportedEncodingException in MqttPublish::publish, full stack trace follows:",e);
            return null;
        } catch (MqttException e) {
            log.error(String.format("Client %s Threw an MqttException in MqttPublish::publish, full stack trace follows:",clientId),e);
            return null;
        }
    }

    /**
     * Terminate the MQTT client connection.
     */
    @Override
    public void terminate()
    {
        try {
            this.client.disconnect();
        }
        catch (MqttException e) {
            log.error("Threw an MqttException in MqttPublish::terminate, full stack trace follows:",e);
        }
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception)
    {
        log.error("Threw an Exception in MqttPublish::onFailure, full stack trace follows:",exception);
    }

    /**
     * MessageArrived handle broker communicaions
     *
     * @param topic the topic for the MQTT message
     * @param message the message that will be sent
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception
    {
        if (!topic.equals(this.topic)) {
            return;
        }
        String messageText = new String(message.getPayload(), ENCODING);

        log.debug("{} received {}: {}", name, topic, messageText);

    }

    /**
     * Called if delivery is verified
     *
     * @param token deliverytoken, verification from broker
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

        log.debug("delivery complete");

        available = true;
    }

}
