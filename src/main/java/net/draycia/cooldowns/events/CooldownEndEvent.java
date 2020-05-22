package net.draycia.cooldowns.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class CooldownEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private UUID uuid;
    private String id;

    public CooldownEndEvent(UUID uuid, String id) {
        this.uuid = uuid;
        this.id = id;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getId() {
        return id;
    }
}
