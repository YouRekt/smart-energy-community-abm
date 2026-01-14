package edu.wut.thesis.smart_energy_community_abm.domain.constants;

public final class TransitionKeys {
    public static final int RUNNING = 1;
    public static final int IDLE = 0;

    private TransitionKeys() {}

    public static final class Discovery {
    }

    public static final class Metering {
        public static final int NO_PANIC = 0;
        public static final int HAS_PANIC = 1;

        public static final class Panic {
        }
    }

    public static final class Negotiation {
        public static final int OVERLOADED = 1;
        public static final int NOT_OVERLOADED = 0;
    }
}
