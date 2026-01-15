package edu.wut.thesis.smart_energy_community_abm.domain.util;

import jade.lang.acl.ACLMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class ACLPerformativeConverterTest {

    @ParameterizedTest
    @CsvSource({
            "INFORM, " + ACLMessage.INFORM,
            "REQUEST, " + ACLMessage.REQUEST,
            "CFP, " + ACLMessage.CFP,
            "PROPOSE, " + ACLMessage.PROPOSE,
            "REFUSE, " + ACLMessage.REFUSE
    })
    @DisplayName("Should correctly convert String to ACL integer")
    void testStringToACL(String input, int expected) {
        assertEquals(expected, ACLPerformativeConverter.ConvertStringToACLPerformative(input));
    }

    @Test
    @DisplayName("Should handle unknown strings gracefully")
    void testUnknownString() {
        assertEquals(ACLMessage.UNKNOWN, ACLPerformativeConverter.ConvertStringToACLPerformative("INVALID_PERFORMATIVE"));
    }

    @Test
    @DisplayName("Should correctly convert ACL integer to String")
    void testACLToString() {
        assertEquals("INFORM", ACLPerformativeConverter.ConvertACLPerformativeToString(ACLMessage.INFORM, false));
    }

    @Test
    @DisplayName("Should handle unknown integers gracefully")
    void testUnknownInteger() {
        assertEquals("ERR_UNK_PERF", ACLPerformativeConverter.ConvertACLPerformativeToString(-999, false));
    }
}