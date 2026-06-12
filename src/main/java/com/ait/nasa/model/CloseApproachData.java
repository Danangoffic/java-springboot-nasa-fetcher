package com.ait.nasa.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CloseApproachData(@JsonProperty("close_approach_date") String closeApproachDate,
        @JsonProperty("relative_velocity") RelativeVelocity relativeVelocity,
        @JsonProperty("miss_distance") MissDistance missDistance,
        @JsonProperty("orbiting_body") String orbitingBody) {
    public record RelativeVelocity(
            @JsonProperty("kilometers_per_second") String kmPerSecond,
            @JsonProperty("kilometers_per_hour") String kmPerHour) {
    }

    public record MissDistance(
            @JsonProperty("kilometers") String kilometers,
            @JsonProperty("lunar") String lunar) {
    }
}
