package com.cit.services.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Base64;

class GenerateId {

    private static final Logger log = LoggerFactory.getLogger(GenerateId.class);

    private GenerateId() {
        throw new IllegalStateException("Utility class : call static methods only");
    }


    static String generateClientId(){
        String generatedString = generateSafeToken();
        if (log.isDebugEnabled()) {
            log.debug("new client ID is: {}", generatedString);
        }
        return generatedString;
    }

    private static String generateSafeToken() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[15];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }
}