package com.bloodconnect.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * =========================================================================
 * Unit 1 & Unit 2 Concept: Encapsulation, Validation & Serializable
 * Represents a geographical location (City, Zone, GPS Coordinates)
 * for a donor, recipient, or hospital.
 * =========================================================================
 */
public class Location implements Serializable {
    private static final long serialVersionUID = 1L;

    // Private fields (demonstrating encapsulation)
    private String city;
    private String zone;
    private double latitude;
    private double longitude;

    /**
     * Default constructor initializing with sample default values.
     */
    public Location() {
        this("Ludhiana", "Punjab", 30.9010, 75.8573);
    }

    /**
     * Parameterized constructor to initialize and validate all fields.
     *
     * @param city      name of the city (e.g., "Ludhiana")
     * @param zone      district, state, or zone name (e.g., "Punjab")
     * @param latitude  GPS latitude coordinate between -90.0 and 90.0
     * @param longitude GPS longitude coordinate between -180.0 and 180.0
     * @throws IllegalArgumentException if inputs fail validation checks
     */
    public Location(String city, String zone, double latitude, double longitude) {
        setCity(city);
        setZone(zone);
        setLatitude(latitude);
        setLongitude(longitude);
    }

    // =========================================================================
    // Getters and Setters with Validation
    // =========================================================================

    /**
     * Gets the city name.
     *
     * @return city name
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city name after validating that it is not null or blank.
     *
     * @param city name of the city
     * @throws IllegalArgumentException if city is null or blank
     */
    public void setCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City name cannot be null or blank.");
        }
        this.city = city.trim();
    }

    /**
     * Gets the zone/state name.
     *
     * @return zone name
     */
    public String getZone() {
        return zone;
    }

    /**
     * Sets the zone/state name after validating that it is not null or blank.
     *
     * @param zone district, state, or zone name
     * @throws IllegalArgumentException if zone is null or blank
     */
    public void setZone(String zone) {
        if (zone == null || zone.trim().isEmpty()) {
            throw new IllegalArgumentException("Zone name cannot be null or blank.");
        }
        this.zone = zone.trim();
    }

    /**
     * Gets the latitude coordinate.
     *
     * @return latitude in decimal degrees
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude after validating that it is a valid finite coordinate within [-90.0, 90.0].
     *
     * @param latitude latitude coordinate
     * @throws IllegalArgumentException if latitude is NaN, infinite, or out of range
     */
    public void setLatitude(double latitude) {
        if (!Double.isFinite(latitude) || latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("Latitude must be a valid finite number between -90.0 and 90.0 degrees. Provided: " + latitude);
        }
        this.latitude = latitude;
    }

    /**
     * Gets the longitude coordinate.
     *
     * @return longitude in decimal degrees
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude after validating that it is a valid finite coordinate within [-180.0, 180.0].
     *
     * @param longitude longitude coordinate
     * @throws IllegalArgumentException if longitude is NaN, infinite, or out of range
     */
    public void setLongitude(double longitude) {
        if (!Double.isFinite(longitude) || longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("Longitude must be a valid finite number between -180.0 and 180.0 degrees. Provided: " + longitude);
        }
        this.longitude = longitude;
    }

    // =========================================================================
    // Standard Object Methods
    // =========================================================================

    /**
     * Returns a human-readable location representation, e.g. "Ludhiana, Punjab".
     *
     * @return formatted location string
     */
    @Override
    public String toString() {
        return city + ", " + zone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return Double.compare(location.latitude, latitude) == 0 &&
                Double.compare(location.longitude, longitude) == 0 &&
                Objects.equals(city, location.city) &&
                Objects.equals(zone, location.zone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, zone, latitude, longitude);
    }
}
