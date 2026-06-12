package com.ait.nasa.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ait.nasa.dto.response.AsteroidResponse;
import com.ait.nasa.exception.InvalidDateRangeException;
import com.ait.nasa.service.NeoService;

@RestController
@RequestMapping(value = "/asteroids")
public class AsteroidController {
    private static final long MAX_RANGE_DAYS = 7;

    @Autowired
    private NeoService neoService;

    @GetMapping("/asteroids")
    public ResponseEntity<List<AsteroidResponse>> getClosestAsteroids(
            @RequestParam("startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        validateDateRange(startDate, endDate);

        List<AsteroidResponse> result = neoService.getClosestApproaches(startDate, endDate);
        return ResponseEntity.ok(result);
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("startDate harus <= endDate");
        }
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        if (daysBetween > MAX_RANGE_DAYS) {
            throw new InvalidDateRangeException(
                    "Rentang tanggal maksimal " + MAX_RANGE_DAYS + " hari, diterima " + daysBetween + " hari");
        }
    }
}
