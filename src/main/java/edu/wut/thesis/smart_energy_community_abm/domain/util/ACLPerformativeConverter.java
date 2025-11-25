package edu.wut.thesis.smart_energy_community_abm.domain.util;

import jade.lang.acl.ACLMessage;

public final class ACLPerformativeConverter {
    public static String ConvertACLPerformativeToString(int performative) {
        return switch (performative) {
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
}
