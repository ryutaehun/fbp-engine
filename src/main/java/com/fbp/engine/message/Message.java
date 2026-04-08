package com.fbp.engine.message;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.*;

@Getter
@EqualsAndHashCode
public class Message {
    private final UUID id;
    private final Map<String, Object> payload;
    private final long timeStamp;

    public Message(Map<String, Object> payload){
        this.id = UUID.randomUUID();
        this.payload = Map.copyOf(payload);
        this.timeStamp = System.currentTimeMillis();
    }

    @SuppressWarnings("unchecked")
    public<T> T get(String key){
        return (T) payload.get(key);
    }

    @Override
    public String toString() {
        return payload.toString();
    }

    public Message withEntry(String key, Object value) {
        Map<String, Object> newPayload = new HashMap<>(this.payload);
        newPayload.put(key, value);

        return new Message(newPayload);
    }

    public boolean hasKey(String key) {
        return this.payload.containsKey(key);
    }

    public Message withoutKey(String key) {
        Map<String, Object> newPayload = new HashMap<>(this.payload);
        newPayload.remove(key);

        return new Message(newPayload);
    }
}
