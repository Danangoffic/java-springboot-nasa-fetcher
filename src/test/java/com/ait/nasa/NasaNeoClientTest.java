package com.ait.nasa;

import com.ait.nasa.client.NasaNeoClient;
import com.ait.nasa.config.NasaApiProperties;
import com.ait.nasa.dto.nasa.NeoObject;
import com.ait.nasa.dto.response.NeoFeedResponse;
import com.ait.nasa.exception.NasaApiException;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@DisplayName("NasaNeoClient")
class NasaNeoClientTest {

    private static final String BASE_URL = "https://api.nasa.gov";
    private static final String API_KEY = "TEST_KEY";
    private static final Matcher<String> FEED_URI = startsWith(BASE_URL + "/neo/rest/v1/feed");

    private MockRestServiceServer server;
    private NasaNeoClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        server = MockRestServiceServer.bindTo(builder).build();
        
        NasaApiProperties props = new NasaApiProperties(BASE_URL, API_KEY);
        client = new NasaNeoClient(builder.build(), props);
    }

    private String fixture(String name) throws Exception {
        var res = new ClassPathResource("fixtures/" + name);
        return new String(res.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("happy path: deserialisasi element_count + near_earth_objects")
    void getFeed_success_deserializesResponse() throws Exception {
        server.expect(requestTo(FEED_URI))
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("start_date", "2026-06-01"))
                .andExpect(queryParam("end_date", "2026-06-02"))
                .andExpect(queryParam("api_key", API_KEY))
                .andRespond(withSuccess(fixture("feed-response.json"), MediaType.APPLICATION_JSON));

        NeoFeedResponse res = client.getFeed("2026-06-01", "2026-06-02");

        assertThat(res).isNotNull();
        assertThat(res.elementCount()).isEqualTo(2);
        assertThat(res.nearEarthObjects()).containsKey("2026-06-01");
        assertThat(res.nearEarthObjects().get("2026-06-01"))
                .hasSize(1)
                .first()
                .satisfies(n -> {
                    assertThat(n.id()).isEqualTo("2021277");
                    assertThat(n.name()).isEqualTo("277810 (2006 FV35)");
                    assertThat(n.isPotentiallyHazardousAsteroid()).isFalse();
                });

        server.verify();
    }

    @Test
    @DisplayName("mengirim URL + query param yang benar")
    void getFeed_sendsCorrectRequest() throws Exception {
        Matcher<String> uri = containsString("/neo/rest/v1/feed");
        server.expect(requestTo(uri))
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("start_date", "2026-06-01"))
                .andExpect(queryParam("end_date", "2026-06-07"))
                .andExpect(queryParam("api_key", API_KEY))
                .andRespond(withSuccess(fixture("feed-response.json"), MediaType.APPLICATION_JSON));

        client.getFeed("2026-06-01", "2026-06-07");

        server.verify();
    }

    @Test
    @DisplayName("getFeed 429 -> NasaApiException(429)")
    void getFeed_rateLimited_throwsNasaApiException() {
        server.expect(requestTo(FEED_URI))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS)
                        .body("{\"error\":{\"code\":\"OVER_RATE_LIMIT\"}}")
                        .contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.getFeed("2026-06-01", "2026-06-02"))
                .isInstanceOf(NasaApiException.class)
                .satisfies(e -> assertThat(((NasaApiException) e).getStatusCode()).isEqualTo(429));

        server.verify();
    }

    @Test
    @DisplayName("getFeed 4xx -> NasaApiException(400)")
    void getFeed_clientError_throwsNasaApiException() {
        server.expect(requestTo(FEED_URI))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .body("{\"error\":{\"code\":\"BAD_REQUEST\"}}")
                        .contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.getFeed("bad", "input"))
                .isInstanceOf(NasaApiException.class)
                .satisfies(e -> assertThat(((NasaApiException) e).getStatusCode()).isEqualTo(400));

        server.verify();
    }

    @Test
    @DisplayName("getFeed 5xx -> NasaApiException(500)")
    void getFeed_serverError_throwsNasaApiException() {
        server.expect(requestTo(FEED_URI))
                .andRespond(withServerError());

        assertThatThrownBy(() -> client.getFeed("2026-06-01", "2026-06-02"))
                .isInstanceOf(NasaApiException.class)
                .satisfies(e -> assertThat(((NasaApiException) e).getStatusCode()).isEqualTo(500));

        server.verify();
    }

    // --- lookup test ---

    @Test
    @DisplayName("lookup happy path: response ter-map ke NeoObject")
    void lookup_success_mapsToNeoObject() throws Exception {
        Matcher<String> uri = containsString("/neo/rest/v1/neo/3542519");
        server.expect(requestTo(uri))
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("api_key", API_KEY))
                .andRespond(withSuccess(fixture("lookup-response.json"), MediaType.APPLICATION_JSON));

        NeoObject neo = client.lookup("3542519");

        assertThat(neo).isNotNull();
        assertThat(neo.id()).isEqualTo("3542519");
        assertThat(neo.name()).isEqualTo("(2010 PK9)");
        assertThat(neo.isPotentiallyHazardousAsteroid()).isTrue();

        server.verify();
    }

    @Test
    @DisplayName("lookup 429 -> NasaApiException(429)")
    void lookup_rateLimited_throwsNasaApiException() {
        Matcher<String> uri = containsString("/neo/rest/v1/neo/3542519");
        server.expect(requestTo(uri))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));

        assertThatThrownBy(() -> client.lookup("3542519"))
                .isInstanceOf(NasaApiException.class)
                .satisfies(e -> assertThat(((NasaApiException) e).getStatusCode()).isEqualTo(429));

        server.verify();
    }

    @Test
    @DisplayName("lookup 404 -> NasaApiException(404)")
    void lookup_notFound_throwsNasaApiException() {
        Matcher<String> uri = containsString("/neo/rest/v1/neo/0000");
        server.expect(requestTo(uri))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> client.lookup("0000"))
                .isInstanceOf(NasaApiException.class)
                .satisfies(e -> assertThat(((NasaApiException) e).getStatusCode()).isEqualTo(404));

        server.verify();
    }

    @Test
    @DisplayName("lookup 5xx -> NasaApiException(500)")
    void lookup_serverError_throwsNasaApiException() {
        Matcher<String> uri = containsString("/neo/rest/v1/neo/3542519");
        server.expect(requestTo(uri))
                .andRespond(withServerError());

        assertThatThrownBy(() -> client.lookup("3542519"))
                .isInstanceOf(NasaApiException.class)
                .satisfies(e -> assertThat(((NasaApiException) e).getStatusCode()).isEqualTo(500));

        server.verify();
    }
}