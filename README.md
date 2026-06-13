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
gradle clean && gradle build && ./gradlew bootRun
```

App starts on `http://localhost:8080`

To build a jar and run it:

```bash
gradle build
java -jar build/libs/*.jar
```

### Get closest asteroids

**GET** `/asteroids?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD`

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
        "name": "(2018 GE)",
        "estimatedDiameterMinKm": 0.008405334,
        "estimatedDiameterMaxKm": 0.0187948982,
        "missDistanceKm": 6267506.031059661,
        "relativeVelocityKmh": 11140.0153655584,
        "closeApproachDate": "2026-06-07",
        "potentiallyHazardous": false
    },
    {
        "name": "(2014 KG39)",
        "estimatedDiameterMinKm": 0.02658,
        "estimatedDiameterMaxKm": 0.0594346868,
        "missDistanceKm": 1.3311906293686686E7,
        "relativeVelocityKmh": 28779.6023328323,
        "closeApproachDate": "2026-06-05",
        "potentiallyHazardous": false
    },
    {
        "name": "(2005 WR2)",
        "estimatedDiameterMinKm": 0.3315545381,
        "estimatedDiameterMaxKm": 0.7413784854,
        "missDistanceKm": 1.4925563315762656E7,
        "relativeVelocityKmh": 52110.6439547264,
        "closeApproachDate": "2026-06-06",
        "potentiallyHazardous": false
    },
    {
        "name": "662203 (2005 WR2)",
        "estimatedDiameterMinKm": 0.3392774354,
        "estimatedDiameterMaxKm": 0.7586474087,
        "missDistanceKm": 1.4925578245630082E7,
        "relativeVelocityKmh": 52110.622956137,
        "closeApproachDate": "2026-06-06",
        "potentiallyHazardous": false
    },
    {
        "name": "(2006 MB)",
        "estimatedDiameterMinKm": 0.0806408277,
        "estimatedDiameterMaxKm": 0.1803183724,
        "missDistanceKm": 1.5101007673515953E7,
        "relativeVelocityKmh": 26399.9550453693,
        "closeApproachDate": "2026-06-07",
        "potentiallyHazardous": false
    },
    {
        "name": "(2013 MR)",
        "estimatedDiameterMinKm": 0.1260535197,
        "estimatedDiameterMaxKm": 0.2818642388,
        "missDistanceKm": 1.6795040175432127E7,
        "relativeVelocityKmh": 62424.7705816326,
        "closeApproachDate": "2026-06-06",
        "potentiallyHazardous": false
    },
    {
        "name": "(2008 QV11)",
        "estimatedDiameterMinKm": 0.1434019235,
        "estimatedDiameterMaxKm": 0.320656449,
        "missDistanceKm": 1.8818979962472077E7,
        "relativeVelocityKmh": 54758.0351628068,
        "closeApproachDate": "2026-06-05",
        "potentiallyHazardous": false
    },
    {
        "name": "(2014 MG6)",
        "estimatedDiameterMinKm": 0.0152951935,
        "estimatedDiameterMaxKm": 0.0342010925,
        "missDistanceKm": 2.066898311389153E7,
        "relativeVelocityKmh": 30192.1313284423,
        "closeApproachDate": "2026-06-05",
        "potentiallyHazardous": false
    },
    {
        "name": "(2019 NX5)",
        "estimatedDiameterMinKm": 0.0040230458,
        "estimatedDiameterMaxKm": 0.0089958039,
        "missDistanceKm": 2.115472974646809E7,
        "relativeVelocityKmh": 22409.4496228421,
        "closeApproachDate": "2026-06-05",
        "potentiallyHazardous": false
    },
    {
        "name": "(2008 SY150)",
        "estimatedDiameterMinKm": 0.0305179233,
        "estimatedDiameterMaxKm": 0.0682401509,
        "missDistanceKm": 2.1740610476278286E7,
        "relativeVelocityKmh": 37913.2235314312,
        "closeApproachDate": "2026-06-02",
        "potentiallyHazardous": false
    }
]
```

#### Sample Response — 400 Bad Request

##### Returned for invalid date format, startDate after endDate, or a range over 7 days.

```json
{
    "timestamp": "2026-06-13T15:18:10.458063Z",
    "status": 400,
    "error": "Bad Request",
    "message": "Rentang tanggal maksimal 7 hari, diterima 12 hari"
}
```
