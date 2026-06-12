package com.ait.nasa.service.impl;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ait.nasa.client.NasaNeoClient;
import com.ait.nasa.dto.nasa.CloseApproachData;
import com.ait.nasa.dto.nasa.NeoObject;
import com.ait.nasa.dto.response.AsteroidResponse;
import com.ait.nasa.dto.response.NeoFeedResponse;
import com.ait.nasa.service.NeoService;

@Service
public class NeoServiceImpl implements NeoService {
    private static final int TOP_LIMIT = 10;

    private final NasaNeoClient nasaNeoClient;

    public NeoServiceImpl(NasaNeoClient nasaNeoClient) {
        this.nasaNeoClient = nasaNeoClient;
    }

    @Override
    public List<AsteroidResponse> getClosestApproaches(LocalDate startDate, LocalDate endDate) {
        NeoFeedResponse feed = nasaNeoClient.getFeed(startDate.toString(), endDate.toString());

        if (feed == null || feed.nearEarthObjects() == null) {
            return List.of();
        }

        return feed.nearEarthObjects().values().stream()  // map<date, list> -> stream of lists
                .flatMap(List::stream)                     // FLATTEN: gabung semua tanggal jadi satu list
                .filter(neo -> closestApproach(neo).isPresent())
                .sorted(Comparator.comparingDouble(this::missDistanceKm))  // SORT: terdekat ke Bumi
                .limit(TOP_LIMIT)                          // LIMIT: top 10
                .map(this::toResponse)
                .toList();
    }

    private AsteroidResponse toResponse(NeoObject neo) {
        CloseApproachData approach = closestApproach(neo).orElseThrow();
        return new AsteroidResponse(
                neo.name(),
                neo.estimatedDiameter().kilometers().estimatedDiameterMin(),
                neo.estimatedDiameter().kilometers().estimatedDiameterMax(),
                Double.parseDouble(approach.missDistance().kilometers()),
                Double.parseDouble(approach.relativeVelocity().kilometersPerHour()),
                approach.closeApproachDate(),
                neo.isPotentiallyHazardousAsteroid()
        );
    }

    // sebuah asteroid bisa punya >1 close approach; ambil yang paling dekat
    private double missDistanceKm(NeoObject neo) {
        return closestApproach(neo)
                .map(a -> Double.parseDouble(a.missDistance().kilometers()))
                .orElse(Double.MAX_VALUE);
    }

    private Optional<CloseApproachData> closestApproach(NeoObject neo) {
        if (neo.closeApproachData() == null) {
            return Optional.empty();
        }
        return neo.closeApproachData().stream()
                .min(Comparator.comparingDouble(a -> Double.parseDouble(a.missDistance().kilometers())));
    }
}
