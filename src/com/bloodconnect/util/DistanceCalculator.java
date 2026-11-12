package com.bloodconnect.util;

import com.bloodconnect.model.Location;

/**
 * =========================================================================
 * Unit 1 & Unit 2 Concept: Static Utility Methods & Mathematical Algorithms
 * Algorithmic utility class for calculating geodesic distances across the globe
 * using the Haversine Formula.
 *
 * Powers Requirement 9: Distance-based ranking between donors and hospitals.
 * =========================================================================
 */
public final class DistanceCalculator {

    // Mean Earth radius in kilometers (WGS-84 standard approximation)
    public static final double EARTH_RADIUS_KM = 6371.0;

    // Default average urban emergency dispatch speed (km/h) for transit estimates
    public static final double DEFAULT_URBAN_SPEED_KMH = 35.0;

    // Private constructor to prevent instantiation of utility class
    private DistanceCalculator() {
    }

    /**
     * Calculates the geodesic surface distance between two latitude/longitude points
     * using the Haversine formula.
     *
     * Formula:
     *   a = sin²(Δlat/2) + cos(lat1) * cos(lat2) * sin²(Δlon/2)
     *   c = 2 * atan2(√a, √(1-a))
     *   d = R * c
     *
     * @param lat1 latitude of start location in decimal degrees (-90 to +90)
     * @param lon1 longitude of start location in decimal degrees (-180 to +180)
     * @param lat2 latitude of destination in decimal degrees (-90 to +90)
     * @param lon2 longitude of destination in decimal degrees (-180 to +180)
     * @return distance in kilometers (always non-negative)
     * @throws IllegalArgumentException if any coordinate is non-finite or out of bounds
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        validateCoordinate(lat1, -90.0, 90.0, "Latitude 1");
        validateCoordinate(lon1, -180.0, 180.0, "Longitude 1");
        validateCoordinate(lat2, -90.0, 90.0, "Latitude 2");
        validateCoordinate(lon2, -180.0, 180.0, "Longitude 2");

        // Convert degree differences to radians
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);

        // Haversine intermediate step
        double a = Math.sin(dLat / 2.0) * Math.sin(dLat / 2.0)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.sin(dLon / 2.0) * Math.sin(dLon / 2.0);

        // Angular distance in radians
        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calculates the distance in kilometers between two Location objects.
     *
     * @param start origin location (e.g., donor's residence or zone)
     * @param end   destination location (e.g., hospital or emergency center)
     * @return distance in kilometers
     * @throws IllegalArgumentException if start or end location is null
     */
    public static double calculateDistance(Location start, Location end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and destination locations cannot be null.");
        }
        return calculateDistance(
                start.getLatitude(),
                start.getLongitude(),
                end.getLatitude(),
                end.getLongitude()
        );
    }

    /**
     * Formats a distance in kilometers to a clean user-friendly string (e.g., "4.2 km").
     *
     * @param distanceKm distance in kilometers
     * @return formatted distance string
     */
    public static String formatDistance(double distanceKm) {
        if (!Double.isFinite(distanceKm) || distanceKm < 0) {
            return "N/A";
        }
        if (distanceKm < 1.0) {
            int meters = (int) Math.round(distanceKm * 1000.0);
            return meters + " m";
        }
        return String.format("%.1f km", distanceKm);
    }

    /**
     * Estimates urban travel response time in minutes based on distance and average speed.
     *
     * @param distanceKm        distance in kilometers
     * @param averageSpeedKmh   estimated vehicle transit speed in km/h
     * @return estimated transit time in minutes (minimum 1 minute)
     */
    public static int estimateTransitMinutes(double distanceKm, double averageSpeedKmh) {
        if (distanceKm <= 0.0) {
            return 0;
        }
        double speed = (averageSpeedKmh > 5.0) ? averageSpeedKmh : DEFAULT_URBAN_SPEED_KMH;
        int minutes = (int) Math.round((distanceKm / speed) * 60.0);
        return Math.max(1, minutes);
    }

    /**
     * Overloaded transit time estimator using the default urban emergency speed (35 km/h).
     *
     * @param distanceKm distance in kilometers
     * @return estimated transit time in minutes
     */
    public static int estimateTransitMinutes(double distanceKm) {
        return estimateTransitMinutes(distanceKm, DEFAULT_URBAN_SPEED_KMH);
    }

    /**
     * Helper validation to ensure coordinates are finite and within geographic limits.
     */
    private static void validateCoordinate(double value, double min, double max, String name) {
        if (!Double.isFinite(value) || value < min || value > max) {
            throw new IllegalArgumentException(name + " must be a valid finite number between " +
                    min + " and " + max + ". Provided: " + value);
        }
    }
}

