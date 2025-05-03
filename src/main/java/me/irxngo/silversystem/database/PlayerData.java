package me.irxngo.silversystem.database;

import java.util.UUID;

public class PlayerData {
    private final UUID uuid;
    private final String name;
    private final int silver;

    public PlayerData(UUID uuid, String name, int silver) {
        this.uuid = uuid;
        this.name = name;
        this.silver = silver;
    }

    public String getName() {
        return name;
    }

    public int getSilver() {
        return silver;
    }
}