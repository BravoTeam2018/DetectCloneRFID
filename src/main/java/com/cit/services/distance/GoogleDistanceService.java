package com.cit.services.distance;

import com.cit.models.DistanceResult;
import com.cit.transfer.GoogleDistanceAPIResponseDTO;
import com.cit.models.Location;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;

import static com.cit.utility.JsonUtility.toJsonString;

@Configuration
@Service
@Slf4j
public class GoogleDistanceService implements IDistanceService {

    private String apiKey;
    private RestTemplate restTemplate;

    @Autowired
    public GoogleDistanceService(RestTemplate restTemplate, String apiKey) {
        this.restTemplate=restTemplate;
        this.apiKey=apiKey;
    }

    public String getRequestURL(Location current, Location previous, Mode mode) {
        return getRequestURL(apiKey, current, previous, mode);
    }

    public String getRequestURL(String key, Location current, Location previous, Mode mode) {
        String baseUri = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";
        String origin = String.format("%s,%s",current.getCoordinates().getLatitude(),current.getCoordinates().getLongitude());
        String destination = String.format("%s,%s",previous.getCoordinates().getLatitude(),previous.getCoordinates().getLongitude());
        return String.format("%s&origins=%s&destinations=%s&key=%s&mode=%s", baseUri,origin,destination, key, mode.toString().toLowerCase());
    }


    public DistanceResult execute(Location current, Location previous, Mode mode ) {

        DistanceResult distanceResult = DistanceResult.builder()
                .distance(0)
                .duration(0)
                .mode(mode.toString())
                .status("ERROR")
                .build();

        if(apiKey==null)
            throw new IllegalArgumentException("GoogleDistanceService apiKey not set");


        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        ResponseEntity<GoogleDistanceAPIResponseDTO> result = restTemplate.exchange(getRequestURL(apiKey,current,previous,mode), HttpMethod.GET, entity, GoogleDistanceAPIResponseDTO.class);

        if(log.isDebugEnabled()) {
            log.debug("Google result = {}", toJsonString(result));
        }

        if( result.getStatusCode().is2xxSuccessful() ) {

            GoogleDistanceAPIResponseDTO googleResult =  result.getBody();

            assert googleResult != null;

            String status = googleResult.getStatus();
            if(status.equals("REQUEST_DENIED"))
                throw new IllegalArgumentException("GoogleDistanceService REQUEST_DENIED : " + result.getBody() );

            distanceResult = DistanceResult.builder()
                    .distance(-1)
                    .duration(-1)
                    .mode(mode.toString())
                    .status("ZERO_RESULTS")
                    .build();


            if(status.equals("OK") && (googleResult.getRows().listIterator().hasNext())) {

                boolean results = ! googleResult.getRows().get(0).getElements().get(0).getStatus().equals("ZERO_RESULTS");

                if(results) {

                    int dis = googleResult.getRows().get(0).getElements().get(0).getDistance().getValue();
                    int dur = googleResult.getRows().get(0).getElements().get(0).getDuration().getValue();

                    distanceResult = DistanceResult.builder()
                            .distance(dis)
                            .duration(dur)
                            .mode(mode.toString())
                            .status("OK")
                            .build();

                }
            }

        }

        return distanceResult;

    }

}



