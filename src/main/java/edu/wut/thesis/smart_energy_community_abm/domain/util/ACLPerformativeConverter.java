package edu.wut.thesis.smart_energy_community_abm.domain.util;

import jade.lang.acl.ACLMessage;

public final class ACLPerformativeConverter {

    public static String ConvertACLPerformativeToString(int performative, boolean shouldColorizeAndAddBrackets) {
        String performativeString = switch (performative) {
            case ACLMessage.ACCEPT_PROPOSAL -> "ACCEPT_PROPOSAL";
            case ACLMessage.AGREE -> "AGREE";
            case ACLMessage.CANCEL -> "CANCEL";
            case ACLMessage.CFP -> "CFP";
            case ACLMessage.CONFIRM -> "CONFIRM";
            case ACLMessage.DISCONFIRM -> "DISCONFIRM";
            case ACLMessage.FAILURE -> "FAILURE";
            case ACLMessage.INFORM -> "INFORM";
            case ACLMessage.INFORM_IF -> "INFORM_IF";
            case ACLMessage.INFORM_REF -> "INFORM_REF";
            case ACLMessage.NOT_UNDERSTOOD -> "NOT_UNDERSTOOD";
            case ACLMessage.PROXY -> "PROXY";
            case ACLMessage.PROPOSE -> "PROPOSE";
            case ACLMessage.PROPAGATE -> "PROPAGATE";
            case ACLMessage.QUERY_IF -> "QUERY_IF";
            case ACLMessage.QUERY_REF -> "QUERY_REF";
            case ACLMessage.REFUSE -> "REFUSE";
            case ACLMessage.REJECT_PROPOSAL -> "REJECT_PROPOSAL";
            case ACLMessage.REQUEST -> "REQUEST";
            case ACLMessage.REQUEST_WHEN -> "REQUEST_WHEN";
            case ACLMessage.REQUEST_WHENEVER -> "REQUEST_WHENEVER";
            case ACLMessage.SUBSCRIBE -> "SUBSCRIBE";
            case ACLMessage.UNKNOWN -> "UNKNOWN";
            default -> "ERR_UNK_PERF";
        };
        return shouldColorizeAndAddBrackets ?
                getColoredPerformative(performative, performativeString) :
                performativeString;
    }

    public static int ConvertStringToACLPerformative(String performative) {
        return switch (performative) {
            case "ACCEPT_PROPOSAL" -> ACLMessage.ACCEPT_PROPOSAL;
            case "AGREE" -> ACLMessage.AGREE;
            case "CANCEL" -> ACLMessage.CANCEL;
            case "CFP" -> ACLMessage.CFP;
            case "CONFIRM" -> ACLMessage.CONFIRM;
            case "DISCONFIRM" -> ACLMessage.DISCONFIRM;
            case "FAILURE" -> ACLMessage.FAILURE;
            case "INFORM" -> ACLMessage.INFORM;
            case "INFORM_IF" -> ACLMessage.INFORM_IF;
            case "INFORM_REF" -> ACLMessage.INFORM_REF;
            case "NOT_UNDERSTOOD" -> ACLMessage.NOT_UNDERSTOOD;
            case "PROXY" -> ACLMessage.PROXY;
            case "PROPOSE" -> ACLMessage.PROPOSE;
            case "PROPAGATE" -> ACLMessage.PROPAGATE;
            case "QUERY_IF" -> ACLMessage.QUERY_IF;
            case "QUERY_REF" -> ACLMessage.QUERY_REF;
            case "REFUSE" -> ACLMessage.REFUSE;
            case "REJECT_PROPOSAL" -> ACLMessage.REJECT_PROPOSAL;
            case "REQUEST" -> ACLMessage.REQUEST;
            case "REQUEST_WHEN" -> ACLMessage.REQUEST_WHEN;
            case "REQUEST_WHENEVER" -> ACLMessage.REQUEST_WHENEVER;
            case "SUBSCRIBE" -> ACLMessage.SUBSCRIBE;
            default -> ACLMessage.UNKNOWN;
        };
    }

    private static String getColoredPerformative(int aclMessageConstant, String label) {
        String colorCode;

        switch (aclMessageConstant) {
            case ACLMessage.ACCEPT_PROPOSAL -> colorCode = "\u001B[38;5;46m";  // Neon Green
            case ACLMessage.AGREE -> colorCode = "\u001B[38;5;34m";  // Forest Green
            case ACLMessage.CONFIRM -> colorCode = "\u001B[38;5;118m"; // Chartreuse
            case ACLMessage.FAILURE -> colorCode = "\u001B[38;5;196m"; // Bright Red
            case ACLMessage.REFUSE -> colorCode = "\u001B[38;5;160m"; // Crimson
            case ACLMessage.REJECT_PROPOSAL -> colorCode = "\u001B[38;5;124m"; // Dark Red
            case ACLMessage.NOT_UNDERSTOOD -> colorCode = "\u001B[38;5;88m";  // Dark Maroon
            case ACLMessage.CANCEL -> colorCode = "\u001B[38;5;208m"; // Orange
            case ACLMessage.DISCONFIRM -> colorCode = "\u001B[38;5;214m"; // Light Orange
            case ACLMessage.INFORM -> colorCode = "\u001B[38;5;255m"; // Bright White
            case ACLMessage.INFORM_IF -> colorCode = "\u001B[38;5;250m"; // Light Grey
            case ACLMessage.INFORM_REF -> colorCode = "\u001B[38;5;245m"; // Medium Grey
            case ACLMessage.REQUEST -> colorCode = "\u001B[38;5;33m";  // Dodger Blue
            case ACLMessage.REQUEST_WHEN -> colorCode = "\u001B[38;5;27m";  // Blue
            case ACLMessage.REQUEST_WHENEVER -> colorCode = "\u001B[38;5;21m";  // Deep Blue
            case ACLMessage.SUBSCRIBE -> colorCode = "\u001B[38;5;63m";  // Royal Blue
            case ACLMessage.PROPOSE -> colorCode = "\u001B[38;5;51m";  // Bright Cyan
            case ACLMessage.CFP -> colorCode = "\u001B[38;5;220m"; // Gold / Yellow
            case ACLMessage.QUERY_IF -> colorCode = "\u001B[38;5;226m"; // Bright Yellow
            case ACLMessage.QUERY_REF -> colorCode = "\u001B[38;5;184m"; // Dark Yellow
            case ACLMessage.PROXY -> colorCode = "\u001B[38;5;201m"; // Magenta
            case ACLMessage.PROPAGATE -> colorCode = "\u001B[38;5;129m"; // Purple
            case ACLMessage.UNKNOWN -> colorCode = "\u001B[38;5;240m"; // Dark Grey
            default -> colorCode = "\u001B[38;5;232m"; // Very Dark Grey
        }

        return colorCode + "[" + label + "]" + "\u001B[0m";
    }
}
