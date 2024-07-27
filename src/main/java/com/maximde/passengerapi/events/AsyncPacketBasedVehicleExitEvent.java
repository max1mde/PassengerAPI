package com.maximde.passengerapi.events;

import lombok.Getter;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class AsyncPacketBasedVehicleExitEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    @Getter
    private final int vehicleEntityID;

    @Getter
    private Player player;

    public AsyncPacketBasedVehicleExitEvent(boolean async, int vehicleEntityID, Player player) {
        super(async);
        this.vehicleEntityID = vehicleEntityID;
        this.player = player;
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

