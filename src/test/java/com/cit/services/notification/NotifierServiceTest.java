package com.cit.services.notification;

import com.cit.IntegrationTests;
import com.cit.UnitTests;
import com.cit.config.ServicesConfig;
import io.moquette.broker.Server;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.MemoryConfig;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import static java.nio.charset.StandardCharsets.UTF_8;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Category(IntegrationTests.class)
@ContextConfiguration(classes = ServicesConfig.class)
class NotifierServiceTest {

    private  String mqttBroker = "tcp://localhost:1883";

    private final Logger LOG = LoggerFactory.getLogger(NotifierServiceTest.class);
    private MqttClientPersistence s_dataStore;
    private MqttClientPersistence s_pubDataStore;

    private Server m_server;
    private IConfig m_config;


    @BeforeEach
    public void setup() throws IOException {
        String tmpDir = System.getProperty("java.io.tmpdir");
        s_dataStore = new MqttDefaultFilePersistence(tmpDir);
        s_pubDataStore = new MqttDefaultFilePersistence(tmpDir + File.separator + "publisher");

        m_server = new Server();
        final Properties configProps = MQTTIntegrationTestUtils.prepareTestProperties();
        m_config = new MemoryConfig(configProps);
        m_server.startServer(m_config);

    }

    @Test
    public void checkSubscribersGetCorrectNotifications() throws Exception {
        LOG.info("*** checkSubscribersGetCorrectQosNotifications ***");
        String tmpDir = System.getProperty("java.io.tmpdir");

        MqttClientPersistence dsSubscriberA = new MqttDefaultFilePersistence(tmpDir + File.separator + "subscriberA");

        MqttClient subscriberA = new MqttClient(mqttBroker, "SubscriberA", dsSubscriberA);
        MessageCollector cbSubscriberA = new MessageCollector();
        subscriberA.setCallback(cbSubscriberA);
        subscriberA.connect();
        subscriberA.subscribe("a/b", 1);

        MqttClientPersistence dsSubscriberB = new MqttDefaultFilePersistence(tmpDir + File.separator + "subscriberB");

        MqttClient subscriberB = new MqttClient(mqttBroker, "SubscriberB", dsSubscriberB);
        MessageCollector cbSubscriberB = new MessageCollector();
        subscriberB.setCallback(cbSubscriberB);
        subscriberB.connect();
        subscriberB.subscribe("a/+", 2);

        NotifierService notifier;
        notifier =  new NotifierService(mqttBroker,"a/b");
        notifier.publish("Hello world MQTT!!");

        MqttMessage messageOnA = cbSubscriberA.waitMessage(1);
        assertEquals("Hello world MQTT!!", new String(messageOnA.getPayload(), UTF_8));
        subscriberA.disconnect();

        MqttMessage messageOnB = cbSubscriberB.waitMessage(1);
        assertNotNull("MUST be a received message", messageOnB);
        assertEquals("Hello world MQTT!!", new String(messageOnB.getPayload(), UTF_8));
        subscriberB.disconnect();
    }






}