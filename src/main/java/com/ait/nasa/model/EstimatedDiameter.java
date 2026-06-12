package com.ait.nasa.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EstimatedDiameter(@JsonProperty("kilometers") DiameterRange kilometers, @JsonProperty("meters") DiameterRange meters) {
    public record DiameterRange(
            @JsonProperty("estimated_diameter_min") double min,
            @JsonProperty("estimated_diameter_max") double max) {
    }
}
