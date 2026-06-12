package com.ait.nasa.dto.response;

import java.util.List;
import java.util.Map;

import com.ait.nasa.model.NeoObject;
import com.fasterxml.jackson.annotation.JsonProperty;

public record NeoFeedResponse(@JsonProperty("element_count") int elementCount,
        @JsonProperty("near_earth_objects") Map<String, List<NeoObject>> nearEarthObjects) {
    
}
