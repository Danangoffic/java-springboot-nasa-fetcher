package com.ait.nasa.dto.response;

public record AsteroidResponse(
            String name,
            double estimatedDiameterMinKm,
            double estimatedDiameterMaxKm,
            double missDistanceKm,
            double relativeVelocityKmh,
            String closeApproachDate,
            boolean potentiallyHazardous) {
}
