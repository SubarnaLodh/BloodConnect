package com.bloodconnect.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * =========================================================================
 * Unit 1 & Unit 2 Concept: Date Handling, Static Methods & Formatting
 * Central date and time utility helper for BloodConnect.
 * Standardizes date formatting, parsing, and cooldown calculation intervals.
 * =========================================================================
 */
public final class DateUtil {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

    private DateUtil() {
        // Prevent instantiation
    }

    /**
     * Formats a LocalDate into standard display string (e.g., "15-Aug-2024").
     *
     * @param date LocalDate to format
     * @return formatted date string, or "N/A" if date is null
     */
    public static String formatDate(LocalDate date) {
        return (date != null) ? date.format(DATE_FORMATTER) : "N/A";
    }

    /**
     * Formats a LocalDateTime into standard display string (e.g., "15-Aug-2024 14:30").
     *
     * @param dateTime LocalDateTime to format
     * @return formatted date-time string, or "N/A" if null
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return (dateTime != null) ? dateTime.format(DATE_TIME_FORMATTER) : "N/A";
    }

    /**
     * Safely parses an ISO date ("yyyy-MM-dd") or standard display date ("dd-MMM-yyyy").
     *
     * @param dateString input date string
     * @return parsed LocalDate
     * @throws DateTimeParseException if text cannot be parsed
     */
    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        String clean = dateString.trim();
        try {
            return LocalDate.parse(clean, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            // Fallback to ISO-8601 (yyyy-MM-dd)
            return LocalDate.parse(clean);
        }
    }

    /**
     * Calculates days elapsed between two dates.
     *
     * @param startDate start date
     * @param endDate   end date
     * @return days difference
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * Checks if a date falls strictly in the future compared to today.
     *
     * @param date date to test
     * @return true if date is after LocalDate.now()
     */
    public static boolean isFutureDate(LocalDate date) {
        return date != null && date.isAfter(LocalDate.now());
    }

    /**
     * Determines whether the mandatory cooldown days have passed since the last donation.
     * If lastDonationDate is null, cooldown is considered satisfied (first-time donor).
     *
     * @param lastDonationDate date of most recent donation
     * @param cooldownDays     required minimum waiting days
     * @return true if eligible by date
     */
    public static boolean isCooldownSatisfied(LocalDate lastDonationDate, int cooldownDays) {
        if (lastDonationDate == null) {
            return true;
        }
        long daysPassed = ChronoUnit.DAYS.between(lastDonationDate, LocalDate.now());
        return daysPassed >= cooldownDays;
    }
}

