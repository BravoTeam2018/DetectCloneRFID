# DetectCloneRFID System
- This is a system, which fulfills the CIT Assignment brief. 
- In short a RFID clone access card detection REST service with backing services, addational trend analysis and security guard client application.

# POC Demo
Link to running Rest service in [Cloudfoundary](http://detectclonerfid.cfapps.io)

#### Modules included
- [DetectCloneRFID](https://github.com/BravoTeam2018/DetectCloneRFID)    
    - A Spring boot RFID clone access card detection REST Controller and backing services.  
    - Deployed to Cloud foundary [http://detectclonerfid.cfapps.io](http://detectclonerfid.cfapps.io)
    - [Source repository](https://github.com/BravoTeam2018/DetectCloneRFID) 
     
- [TrendAnalysis](https://github.com/BravoTeam2018/trendanalysis)  
    - Trend analysis tool used by Security Teams to detect trends and take action
    - A Springboot micro service + Elastic Stack deployed on Azure 
    - [Source repository](https://github.com/BravoTeam2018/trendanalysis)
- [Subscriber](https://github.com/BravoTeam2018/Subscriber) 
    - Real-time Alert client used by Security Guards
    - Deployed locally at each security guard station  
    - [Source repository](https://github.com/BravoTeam2018/Subscriber)

#### Architecture Context Diagram 
![Context Diagram](https://github.com/BravoTeam2018/DetectCloneRFID/docs/ContextDiagram.png)


## DetectCloneRFID Rest conttroller 

### Building from source

### Prerequisites 
1. Install [Maven 3]( https://maven.apache.org/)
2. Install [java 8]( http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
3. Update application.properties and update google API Key
4. Update onfiguration Panel locator service to point to correct remote URL

###  Google API Key
```
Open src\main\resources\application proerties
You will see the following key
google.api-key=${GOOGLE_KEY}
This key is used to configure google distanceResult api
The ${GOOGLE_KEY} means it gets the value from the windows envionment variable
To set the GOOGLE_KEY add an environment variable called GOOGLE_KEY and assign your  API KEY
In windows 10
1. win+ S
2. search for environment
3. select edit system environment variables
4. click on button "Envionment Variables" 
5. add a new Variable name called GOOGLE_KEY and in the value secion add your key

```

### Update onfiguration Panel locator service to point to correct remote URL
```
Open src\main\resources\application proerties
uri.location.service.panels=http://uuidlookup.cfapps.io/api/locations
```

### Building Code
```
mvnw package
```

### Running tests
```
mvnw test
```

## Running the webservice on your local machine
```
mvnw spring-boot:run
```

## Running Sonarqube Analysis 
```
mvnw sonar:sonar
```

### Documentation
  - API Swagger documentation can be accessed typing http://localhost:8081 once the application is running

### Cucumber Integration Acceptance tests
- Gherkin acceptance criteria [cloneValidationCheck.feature](https://github.com/eamonfoy-cit/rfidclone/blob/master/src/test/resources/cucumber/cloneValidationCheck.feature)


  - Want to know more about cucumber and Gherkin visit these links below
    - [Writing better user stories with Gherkin and Cucumber](https://medium.com/@mvwi/story-writing-with-gherkin-and-cucumber-1878124c284c)

    - [Cucumber BDD (Part 2): Creating a Sample Java Project with Cucumber, TestNG, and Maven](https://medium.com/agile-vision/cucumber-bdd-part-2-creating-a-sample-java-project-with-cucumber-testng-and-maven-127a1053c180)


## Remaining todo

### Optional
- [ ] Blacklist feature
