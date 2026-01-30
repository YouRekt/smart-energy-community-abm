package edu.wut.thesis.smart_energy_community_abm.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jade.core.AID;

import java.io.IOException;

public record EnergyRequest(
        @JsonSerialize(using = AIDSerializer.class)
        @JsonDeserialize(using = AIDDeserializer.class)
        AID applianceAID,
        long startTick,
        int duration,
        double energyPerTick,
        int taskId
) {
    public boolean isActive(long tick) {
        return tick >= startTick && tick < (startTick + duration);
    }

    public long endTick() {
        return startTick + duration - 1;
    }

    public static class AIDSerializer extends JsonSerializer<AID> {
        @Override
        public void serialize(AID value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.getName());
        }
    }

    public static class AIDDeserializer extends JsonDeserializer<AID> {
        @Override
        public AID deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new AID(p.getValueAsString(), AID.ISGUID);
        }
    }
}
