package com.ait.nasa.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.ait.nasa.config.NasaApiProperties;
import com.ait.nasa.dto.response.NeoFeedResponse;

@Component
public class NasaNeoClient {
    private final RestClient restClient;
    private final NasaApiProperties props;

    public NasaNeoClient(RestClient nasaRestClient, NasaApiProperties props) {
        this.restClient = nasaRestClient;
        this.props = props;
    }    

    public NeoFeedResponse getFeed(String startDate, String endDate) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/neo/rest/v1/feed")
                        .queryParam("start_date", startDate)
                        .queryParam("end_date", endDate)
                        .queryParam("api_key", props.key())
                        .build())
                .retrieve()
                .body(NeoFeedResponse.class);
    }
}
