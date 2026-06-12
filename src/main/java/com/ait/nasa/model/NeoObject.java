package com.ait.nasa.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NeoObject(String id,
        @JsonProperty("neo_reference_id") String neoReferenceId,
        String name,
        @JsonProperty("nasa_jpl_url") String nasaJplUrl,
        @JsonProperty("absolute_magnitude_h") double absoluteMagnitude,
        @JsonProperty("estimated_diameter") EstimatedDiameter estimatedDiameter,
        @JsonProperty("is_potentially_hazardous_asteroid") boolean potentiallyHazardous,
        @JsonProperty("close_approach_data") List<CloseApproachData> closeApproachData) {
    
}
