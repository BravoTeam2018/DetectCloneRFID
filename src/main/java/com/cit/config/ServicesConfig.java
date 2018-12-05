package com.cit.config;

import com.cit.controllers.ValidationController;
import com.cit.services.locator.LocatorService;
import com.cit.services.notification.INotifierService;
import com.cit.services.notification.NotifierService;
import com.cit.services.validation.rules.EventValidationRuleBook;
import com.cit.services.distance.*;
import com.cit.services.eventstore.EventStoreService;
import com.cit.services.eventstore.IEventStoreService;
import com.cit.services.validation.ValidationService;
import com.cit.services.validation.ValidationRulesResult;
import com.deliveredtechnologies.rulebook.lang.RuleBookBuilder;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.client.RestTemplate;


@PropertySource({ "classpath:application.properties" })
@Configuration
public class ServicesConfig {

    @Value("${google.api-key}")
    private String apiKey;

    @Value("${uri.location.service.panels:http://localhost:8080/api/locations}")
    private String uriLocations = "http://localhost:8080/api/locations";

    @Value("${mqtt.broker.host}")
    public static final String MQTT_BROKER = "tcp://13.82.192.85:8883";

    @Value("${mqtt.topic.name}")
    public static final String MQTT_TOPIC = "validation.alerts.bravo";


    /**
     * This is the main validation services configuration which is used to validate current and previous scan events
     * @return validationService
     */
    @Bean
    @Primary
    ValidationService validationService() {
        return new ValidationService(eventRuleBook(),distanceService());
    }


    /**
     * This is configuration for the rulebook used by the validation service to confiure ho the validation rules work
     * @return RuleBook
     */
    @Bean
    RuleBook  eventRuleBook() {

        return RuleBookBuilder.create(EventValidationRuleBook.class).withResultType(ValidationRulesResult.class)
                .withDefaultResult(ValidationRulesResult.builder().reason("Possible time-distance event").validEvent(true).build())
                .build();
    }



    /**
     * Configuration bean used to configure the a facade distance service, which encapulates all distance services
     * in to one and then caches the distances in one place so they can be queried
     * @return IDistanceService
     */
    @Bean
    IDistanceService distanceService() {return new DistanceFacadeService(googleDistanceService(),localDistanceService(), flyAndDriveDistanceService());}

    /**
     * Configuration bean used to configure the flyAndDrive distance service
     * @return FlyAndDriveDistanceService
     */
    @Bean
    FlyAndDriveDistanceService flyAndDriveDistanceService() { return new FlyAndDriveDistanceService();}

    /**
     * Configuration bean used to configure the local distance service
     * @return LocalDistanceService
     */
    @Bean
    LocalDistanceService localDistanceService() { return new LocalDistanceService(); }

    /**
     * Configuration bean to configure the google distance service
     * @return GoogleDistanceService
     */
    @Bean
    GoogleDistanceService googleDistanceService() {
        return new GoogleDistanceService(restTemplate(),apiKey);
    }


    /**
     * This is the configuration for the event store service to cache the most recent panel scan events
     * @return EventStoreService
     */
    @Bean
    EventStoreService eventStoreService() {
        return new EventStoreService();
    }
    @Bean

    /**
     * This is the configuration for the event store service to cache the most recent panel scan events
     * @return IEventStoreService
     */
    @Primary
    IEventStoreService eventService() {
        return new EventStoreService();
    }

    /**
     * Configuration for validation controller
     * @return ValidationController
     */
    @Bean
    public ValidationController validationController() {
        return new ValidationController(locatorService(),eventStoreService(),validationService(),notifierService());
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    /**
     * This is the configuration for the MQTT Notification service
     * @return INotifierService
     */
    @Bean
    INotifierService notifierService() {
        return new NotifierService(MQTT_BROKER, MQTT_TOPIC);
    }


    /**
     * This is the spring configuration for the panel locator service, which returns detailed panel information given and panel UUID
     * @return LocatorService
     */
    @Bean
    @Primary
    public LocatorService locatorService() {
        return new LocatorService(restTemplate(),uriLocations);
    }

    /**
     * This is used by both GoogleAPI and locator service to pull back rest response in to a serilised java object
     * @return RestTemplate
     */
    @Bean
    RestTemplate restTemplate() { return new RestTemplate(); }


}
