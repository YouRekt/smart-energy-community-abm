package edu.wut.thesis.smart_energy_community_abm.domain.dto;

/**
 * A simple record to wrap API responses with a message and optional data.
 *
 * @param message The response message
 * @param runId   Optional run ID (for start simulation response)
 */
public record ApiResponse(String message, Long runId) {
    public ApiResponse(String message) {
        this(message, null);
    }
}
