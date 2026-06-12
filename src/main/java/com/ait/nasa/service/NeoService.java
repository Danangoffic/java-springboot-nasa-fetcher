package com.ait.nasa.service;

import java.time.LocalDate;
import java.util.List;

import com.ait.nasa.dto.response.AsteroidResponse;

public interface NeoService {
    List<AsteroidResponse> getClosestApproaches(LocalDate startDate, LocalDate endDate);
}
