package edu.wut.thesis.smart_energy_community_abm.domain.constants;

public final class DataStoreKey {
    private DataStoreKey() {}

    public static final class Discovery {
        public static final String TICK_REPLY_BY = "tick-reply-by";
        public static final String HEALTH_REPLY_BY = "health-reply-by";
        public static final String TICK_MSG = "tick-msg";
    }

    public static final class Metering {
        public static final String ALLOWED_GREEN_ENERGY = "allowed-green-energy";
        public static final String PANIC_CFP = "panic-cfp";
        public static final String AVAILABLE_ENERGY = "available-energy";
        public static final String SHORTFALL = "shortfall";
        public static final String CURRENT_CHARGE = "current-charge";
        public static final String POWER_PRODUCED = "power-produced";
        public static final String GREEN_ENERGY_USED = "green-energy-used";
        public static final String EXTERNAL_ENERGY_USED = "external-energy-used";
        public static final String REQUEST_REPLY_BY = "request-reply-by";
        public static final String ENERGY_USAGE_REQUEST_MSG = "energy-usage-request-msg";

        public static final class Panic {
            public static final String ACCEPTED_PROPOSAL = "accepted-proposal";
            public static final String POSTPONE_RESPONSES = "postpone-responses";
            public static final String CFP_REPLY_BY = "cfp-reply-by";
            public static final String ACCEPT_PROPOSAL_REPLY_BY = "accept-proposal-reply-by";
            public static final String POSTPONE_AGREEMENTS = "postpone-agreements";
            public static final String POSTPONE_REPLY_BY = "postpone-reply-by";
            public static final String POSTPONE_REPLY_COUNT = "postpone-reply-count";
            public static final String ACCEPT_PROPOSAL_MSG_COUNT = "accept-proposal-msg-count";
            public static final String TASK_TO_POSTPONE = "task-to-postpone";
        }
    }

    public static final class Negotiation {
        public static final String HOUSEHOLD_RESPONSE = "household-response";
        public static final String AGENT_LIST = "agent-list";
        public static final String OVERLOADED_TICKS = "overloaded-ticks";
        public static final String ALLOCATION_REQUEST = "allocation-request";
        public static final String REQUESTED_ALLOCATIONS = "requested-allocations";
        public static final String REQUEST_REPLY_BY = "request-reply-by";
        public static final String REQUEST_REPLY_COUNT = "request-reply-count";
        public static final String ALLOCATION_REQUEST_MSG = "allocation-request-msg";
        public static final String ALLOCATION_RESPONSE_MSG = "allocation-response-msg";
        public static final String SEND_ALLOCATION_REQUEST = "send-allocation-request";
        public static final String COLLECT_ALLOCATION_RESPONSE = "collect-allocation-response";
        public static final String PROCESS_ALLOCATION_RESPONSE = "process-allocation-response";
        public static final String APPLIANCE_CONFIRM_MESSAGES = "appliance-confirm-messages";
        public static final String CONFIRM_REPLY_BY_DELAY = "confirm-reply-by-delay";
    }
}
