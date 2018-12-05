package com.cit.services.notification;



import java.io.File;
import java.util.Properties;
import static io.moquette.BrokerConstants.DEFAULT_MOQUETTE_STORE_H2_DB_FILENAME;
import static io.moquette.BrokerConstants.PERSISTENT_STORE_PROPERTY_NAME;
import static io.moquette.BrokerConstants.PORT_PROPERTY_NAME;
import static org.junit.Assert.assertFalse;


public class MQTTIntegrationTestUtils {


    static String localH2MvStoreDBPath() {
        String currentDir = System.getProperty("user.dir");
        return currentDir + File.separator + "build" + File.separator + DEFAULT_MOQUETTE_STORE_H2_DB_FILENAME;
    }

    public static Properties prepareTestProperties() {
        Properties testProperties = new Properties();
        testProperties.put(PERSISTENT_STORE_PROPERTY_NAME, MQTTIntegrationTestUtils.localH2MvStoreDBPath());
        testProperties.put(PORT_PROPERTY_NAME, "1883");
        return testProperties;
    }

    private MQTTIntegrationTestUtils() {
    }

    public static void clearTestStorage() {
        String dbPath = localH2MvStoreDBPath();
        File dbFile = new File(dbPath);
        if (dbFile.exists()) {
            dbFile.delete();
        }
        assertFalse(dbFile.exists());
    }
}
