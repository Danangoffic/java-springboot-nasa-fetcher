package com.ait.nasa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ait.nasa.client.NasaNeoClient;
import com.ait.nasa.dto.nasa.CloseApproachData;
import com.ait.nasa.dto.nasa.EstimatedDiameter;
import com.ait.nasa.dto.nasa.NeoObject;
import com.ait.nasa.dto.response.AsteroidResponse;
import com.ait.nasa.dto.response.NeoFeedResponse;
import com.ait.nasa.exception.InvalidDateRangeException;
import com.ait.nasa.service.NeoService;
import com.ait.nasa.service.impl.NeoServiceImpl;

public class NeoServiceImplTest {
    private final NasaNeoClient client = mock(NasaNeoClient.class);
    private final NeoService service = new NeoServiceImpl(client);

    @Test
    void flattensSortsAndLimitsToTop10() {
        // 12 asteroid lintas 2 tanggal, miss distance acak
        var day1 = List.of(neo("A", 5000), neo("B", 100), neo("C", 9000),
                neo("D", 300), neo("E", 7000), neo("F", 50));
        var day2 = List.of(neo("G", 8000), neo("H", 20), neo("I", 6000),
                neo("J", 400), neo("K", 1000), neo("L", 2000));

        when(client.getFeed(LocalDate.parse("2026-06-01").toString(), LocalDate.parse("2026-06-02").toString()))
                .thenReturn(new NeoFeedResponse(12, Map.of("2026-06-01", day1, "2026-06-02", day2)));

        List<AsteroidResponse> result =
                service.getClosestApproaches(LocalDate.parse("2026-06-01"), LocalDate.parse("2026-06-02"));

        assertThat(result).hasSize(10);     // limit top 10
        assertThat(result.get(0).name()).isEqualTo("H");    // 20 km, terdekat
        assertThat(result.get(1).name()).isEqualTo("F");    // 50 km
        assertThat(result).extracting(AsteroidResponse::missDistanceKm).isSorted(); // ascending = terdekat duluan
    }

    @Test
    void returnsEmptyWhenNoData() {
        when(client.getFeed(LocalDate.parse("2026-06-01").toString(), LocalDate.parse("2026-06-01").toString()))
                .thenReturn(new NeoFeedResponse(0, Map.of()));
        assertThat(service.getClosestApproaches(
                LocalDate.parse("2026-06-01"), LocalDate.parse("2026-06-01"))).isEmpty();
    }

    private NeoObject neo(String name, double missKm) {
        var diameter = new EstimatedDiameter(new EstimatedDiameter.Kilometers(0.1, 0.3));
        var approach = new CloseApproachData(
                "2026-06-01",
                new CloseApproachData.RelativeVelocity("40000"),
                new CloseApproachData.MissDistance(String.valueOf(missKm)));
        return new NeoObject(name, name, diameter, false, List.of(approach));
    }

    // ---------- validasi tanggal ----------

    @Test
    void exactlySevenDays_isValid() {
        LocalDate start = LocalDate.parse("2026-06-01");
        LocalDate end = LocalDate.parse("2026-06-08"); // 7 hari penuh
        when(client.getFeed(start.toString(), end.toString()))
                .thenReturn(new NeoFeedResponse(0, Map.of()));

        assertThatCode(() -> service.getClosestApproaches(start, end))
                .doesNotThrowAnyException();
    }

    @Test
    void moreThanSevenDays_throws() {
        LocalDate start = LocalDate.parse("2026-06-01");
        LocalDate end = LocalDate.parse("2026-06-09"); // 8 hari -> invalid

        assertThatThrownBy(() -> service.getClosestApproaches(start, end))
                .isInstanceOf(InvalidDateRangeException.class);
    }

    @Test
    void startAfterEnd_throws() {
        LocalDate start = LocalDate.parse("2026-06-10");
        LocalDate end = LocalDate.parse("2026-06-01");

        assertThatThrownBy(() -> service.getClosestApproaches(start, end))
                .isInstanceOf(InvalidDateRangeException.class);
    }

    @Test
    void sameDay_isValid() {
        LocalDate day = LocalDate.parse("2026-06-01");
        when(client.getFeed(day.toString(), day.toString()))
                .thenReturn(new NeoFeedResponse(0, Map.of()));

        assertThatCode(() -> service.getClosestApproaches(day, day))
                .doesNotThrowAnyException();
    }

    @Test
    void invalidFormat_failsAtParse() {
        assertThatThrownBy(() -> LocalDate.parse("01-06-2026"))
                .isInstanceOf(DateTimeParseException.class);
        assertThatThrownBy(() -> LocalDate.parse("2026/06/01"))
                .isInstanceOf(DateTimeParseException.class);
        assertThatThrownBy(() -> LocalDate.parse("bukan-tanggal"))
                .isInstanceOf(DateTimeParseException.class);
    }
}
