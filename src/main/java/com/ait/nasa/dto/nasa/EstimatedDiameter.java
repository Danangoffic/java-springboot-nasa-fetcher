package com.ait.nasa.dto.nasa;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EstimatedDiameter(Kilometers kilometers) {
    public record Kilometers(
            @JsonProperty("estimated_diameter_min") double estimatedDiameterMin,
            @JsonProperty("estimated_diameter_max") double estimatedDiameterMax
    ) {}
}
