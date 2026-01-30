package edu.wut.thesis.smart_energy_community_abm.domain.util;

public final class MetricNameHelper {
    public static final String BATTERY_EMPTY = "community_battery_empty";
    public static final String BATTERY_FULL = "community_battery_full";

    private MetricNameHelper() {}

    public static final String BATTERY_CHARGE = "community_battery_charge";
    public static final String BATTERY_DISCHARGE_AMOUNT = "community_battery_discharge_amount";
    public static final String BATTERY_CHARGE_AMOUNT = "community_battery_charge_amount";
    public static final String BATTERY_CURTAILED = "community_battery_curtailed_energy";

    public static String COMMUNITY_GREEN_CONSUMPTION = "consumption:green:%";

    public static String COMMUNITY_GRID_CONSUMPTION = "consumption:grid:%";

    public static String COMMUNITY_TOTAL_CONSUMPTION = "consumption:%";

    public static String COMMUNITY_PRODUCTION = "production:%";

    public static String householdGreenConsumption(String householdName) {
        return String.format("consumption:green:%s:%%", householdName);
    }

    public static String householdGridConsumption(String householdName) {
        return String.format("consumption:grid:%s:%%", householdName);
    }

    public static String applianceGreenConsumption(String householdName, String applianceName) {
        return String.format("consumption:green:%s:%s", householdName, applianceName);
    }

    public static String applianceGridConsumption(String householdName, String applianceName) {
        return String.format("consumption:grid:%s:%s", householdName, applianceName);
    }

    public static String sourceProduction(String sourceName) {
        return String.format("production:%s", sourceName);
    }

    public static String applianceTaskRequested(String householdName, String applianceName) {
        return String.format("task:requested:%s:%s", householdName, applianceName);
    }

    public static String TOTAL_REQUESTED = "task:requested:%";

    public static String applianceTaskRefused(String householdName, String applianceName) {
        return String.format("task:refused:%s:%s", householdName, applianceName);
    }

    public static String TOTAL_REFUSED = "task:refused:%";

    public static String applianceTaskAccepted(String householdName, String applianceName) {
        return String.format("task:accepted:%s:%s", householdName, applianceName);
    }

    public static String TOTAL_ACCEPTED = "task:accepted:%";

    public static String applianceTaskPostponed(String householdName, String applianceName) {
        return String.format("task:postponed:%s:%s", householdName, applianceName);
    }

    public static String TOTAL_POSTPONED = "task:postponed:%";

    public static String applianceTaskFinished(String householdName, String applianceName) {
        return String.format("task:finished:%s:%s", householdName, applianceName);
    }

    public static String TOTAL_FINISHED = "task:finished:%";
}