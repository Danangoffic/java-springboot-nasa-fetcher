# NASA Asteroids REST API

A small Spring Boot REST API that fetches Near-Earth Object data from
NASA's NEO Feed and returns the **10 closest asteroids** to Earth for a
given date range (max 7 days), trimmed down to only the fields you care about.

## Tech Stack

- Java 17+ / Spring Boot (Spring Web, Validation)
- Gradle
- NASA NeoWs (Near Earth Object Web Service) Feed API

## Prerequisites

- JDK 17 or newer
- A free NASA API key from [NASA API](https://api.nasa.gov) (or use `DEMO_KEY` for quick testing)

## Configuration

The API key is read from the `NASA_API_KEY` environment variable (never hardcoded).

## Run Project

### set your key

```bash
export NASA_API_KEY=your_api_key_here
```

### run

```bash
gradle run
```

App starts on `http://localhost:8080`

To build a jar and run it:

```bash
gradle build
java -jar build/libs/*.jar
```

### Get closest asteroids

GET /asteroids?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD
Returns the **top 10 asteroids** sorted by closest miss distance to Earth.
Date range must be **<= 7 days**.

#### Sample Request

```bash
curl "http://localhost:8080/asteroids?startDate=2026-06-01&endDate=2026-06-07"
```

#### Sample Response — `200 OK`

```json
[
  {
    "name": "(2010 RF12)",
    "estimatedDiameterMeters": 7.45,
    "missDistanceKm": 124003.21,
    "relativeVelocityKmh": 23456.78,
    "closeApproachDate": "2026-06-03",
    "potentiallyHazardous": false
  },
  {
    "name": "(2024 MK)",
    "estimatedDiameterMeters": 230.11,
    "missDistanceKm": 295112.40,
    "relativeVelocityKmh": 41020.05,
    "closeApproachDate": "2026-06-05",
    "potentiallyHazardous": true
  }
]
```

#### Sample Response — 400 Bad Request

##### Returned for invalid date format, startDate after endDate, or a range over 7 days.

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Date range must not exceed 7 days"
}
```
