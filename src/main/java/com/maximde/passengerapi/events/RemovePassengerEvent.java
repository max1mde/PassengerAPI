package com.maximde.passengerapi.events;

import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class RemovePassengerEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    @Getter
    private final int targetEntityID;

    /**
     * Passengers which are getting removed
     */
    @Getter
    private final List<Integer> passengerList;
    @Getter
    private final String pluginName;

    public RemovePassengerEvent(int targetEntityID, List<Integer> passengerList, String pluginName) {
        this.targetEntityID = targetEntityID;
        this.passengerList = passengerList;
        this.pluginName = pluginName;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}