package me.softik.nerochat.models;

import java.util.UUID;

public class ChatMessageData {

    public final UUID uuid;
    public final String message;

    public ChatMessageData(UUID uuid, String message) {
        this.uuid = uuid;
        this.message = message;
    }
}
