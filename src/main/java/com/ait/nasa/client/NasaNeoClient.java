package com.ait.nasa.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.ait.nasa.config.NasaApiProperties;
import com.ait.nasa.dto.response.NeoFeedResponse;
import com.ait.nasa.exception.NasaApiException;
import com.ait.nasa.model.NeoObject;

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
                .onStatus(status -> status.isError(), (request, response) -> {
                    throw new NasaApiException(
                            response.getStatusCode().value(),
                            "NASA Neo-Feed request failed with status "
                                    + response.getStatusCode().value());
                })
                .body(NeoFeedResponse.class);
    }

    public NeoObject lookup(String asteroidId) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/neo/rest/v1/neo/{id}")
                        .queryParam("api_key", props.key())
                        .build(asteroidId))
                .retrieve()
                .onStatus(status -> status.isError(), (request, response) -> {
                    throw new NasaApiException(
                            response.getStatusCode().value(),
                            "NASA Neo-Lookup request failed with status "
                                    + response.getStatusCode().value());
                })
                .body(NeoObject.class);
    }
}
