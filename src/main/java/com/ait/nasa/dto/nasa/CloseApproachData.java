package com.ait.nasa.dto.nasa;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CloseApproachData(@JsonProperty("close_approach_date") String closeApproachDate,
        @JsonProperty("relative_velocity") RelativeVelocity relativeVelocity,
        @JsonProperty("miss_distance") MissDistance missDistance) {
    public record RelativeVelocity(
            @JsonProperty("kilometers_per_hour") String kilometersPerHour) {}
    public record MissDistance(String kilometers) {}
}
