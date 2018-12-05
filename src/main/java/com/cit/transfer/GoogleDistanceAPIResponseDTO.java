package com.cit.transfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoogleDistanceAPIResponseDTO {

    @JsonProperty("destination_addresses")
    private List<String> destinationAddresses;
    @JsonProperty("origin_addresses")
    private List<String> originAddresses;
    private List<Rows> rows;
    private String status;


    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Rows {
        private List<Elements> elements;

        @lombok.Data
        @lombok.AllArgsConstructor
        @lombok.NoArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Elements {
            private Distance distance;
            private Duration duration;
            private String status;


            @lombok.Data
            @lombok.AllArgsConstructor
            @lombok.NoArgsConstructor
            @JsonInclude(JsonInclude.Include.NON_NULL)
            public static class Distance {
                private String text;
                private int value;
            }

            @lombok.Data
            @lombok.AllArgsConstructor
            @lombok.NoArgsConstructor
            @JsonInclude(JsonInclude.Include.NON_NULL)
            public static class Duration {
                private String text;
                private int value;
            }
        }
    }
}
