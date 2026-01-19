package edu.wut.thesis.smart_energy_community_abm.domain.util;

public final class MetricNameHelper {
    private MetricNameHelper() {}

    public static final String BATTERY_CHARGE = "community_battery_charge";

    public static String communityGreenConsumption() { return "consumption:green:%"; }

    public static String communityGridConsumption() { return "consumption:grid:%"; }

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

    public static String communityTotalProduction() { return "production:%"; }

    public static String sourceProduction(String sourceName) {
        return String.format("production:%s", sourceName);
    }
}