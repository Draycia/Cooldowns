package net.draycia.cooldowns.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class CooldownStartEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private UUID uuid;
    private String id;

    public CooldownStartEvent(UUID uuid, String id) {
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

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getId() {
        return id;
    }
}
