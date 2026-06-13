package com.ait.nasa.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ait.nasa.dto.response.AsteroidResponse;
import com.ait.nasa.service.NeoService;

@RestController
@RequestMapping(value = "/asteroids")
public class AsteroidController {
    private final NeoService neoService;

    public AsteroidController(NeoService neoService) {
        this.neoService = neoService;
    }

    @GetMapping
    public ResponseEntity<List<AsteroidResponse>> getClosestAsteroids(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AsteroidResponse> result = neoService.getClosestApproaches(startDate, endDate);
        return ResponseEntity.ok(result);
    }
}
