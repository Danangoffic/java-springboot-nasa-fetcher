package com.ait.nasa.dto.nasa;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NeoObject(String id,
        String name,
        @JsonProperty("estimated_diameter") EstimatedDiameter estimatedDiameter,
        @JsonProperty("is_potentially_hazardous_asteroid") boolean isPotentiallyHazardousAsteroid,
        @JsonProperty("close_approach_data") List<CloseApproachData> closeApproachData) {
    
}
