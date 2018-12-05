package com.cit.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.cit.models.Event;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.And;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

@Slf4j
public class StepDefsIntegrationTest extends SpringIntegrationTest {

    private Event currentEvent;
    private int elapsedTimeSeconds;

    @Given("^card \"(.*?)\" used at panel \"(.*?)\"$")
    public void givenCardUsedAtPanel( String cardId , String panelId ) {

        this.currentEvent =  Event.builder()
                .cardId(cardId)
                .panelId(panelId)
                .timestamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis())
                .build();
    }

    @And("^check performed$")
    public void whenCheckPerformed() throws IOException {

        long startTime = System.currentTimeMillis();

        executeGet(String.format("http://localhost:%d/api/panels/request?panelId=%s&cardId=%s&allowed=true",
                this.port,this.currentEvent.getPanelId(),this.currentEvent.getCardId()));

        long stopTime = System.currentTimeMillis();
        this.elapsedTimeSeconds = (int)((stopTime - startTime) / 1000);
    }

    @And("^no previous events found$")
    public void andNoPreviousEventFound() {
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(this.latestResponse.getBody());
        assertNull(JsonPath.read(document, "$.previousEvent"));
    }

    @And("^responds with validEvent \"([^\"]*)\"$")
    public void thenRespondsWithValidEvent(String validEvent) {
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(this.latestResponse.getBody());
        assertEquals(Boolean.parseBoolean(validEvent), JsonPath.read(document, "$.validEvent"));
    }

    @And("^responds within less than (\\d+) seconds$")
    public void andRespondsWithinLessThan(int seconds) {
        assertEquals(String.format("response time %d took longer than %d seconds",this.elapsedTimeSeconds, seconds),true, this.elapsedTimeSeconds <= seconds);
    }

    @And("^previous event found (\\d+) seconds before at panel \"([^\"]*)\"$")
    public void andPreviousEventFoundNSecondsBeforeAtPanel(int secondsBefore , String previousEventPanel) throws IOException {

        // need to create an event using previous panel secondsBefore
        long timestamp1 = this.currentEvent.getTimestamp();
        long previousTimestamp = timestamp1 - (secondsBefore*1000); // secondsBefore

        Event previousEvent = Event.builder()
                .accessAllowed(true)
                .cardId(this.currentEvent.getCardId())
                .panelId(previousEventPanel)
                .timestamp(previousTimestamp)
                .build();
        String uri = String.format("http://localhost:%d/api/panels/addprev?panelId=%s&cardId=%s&allowed=true&timeStamp=%d",
                this.port,previousEvent.getPanelId(),previousEvent.getCardId(),previousTimestamp);

        log.debug("Cucumber - andPreviousEventFoundNSecondsBeforeAtPanel( secondsBefore={}, previousEventPanel={}, uri={}", secondsBefore,previousEventPanel, uri);


        executeGet(uri);
    }

}