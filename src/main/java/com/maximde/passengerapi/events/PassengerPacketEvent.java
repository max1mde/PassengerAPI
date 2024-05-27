package com.maximde.passengerapi.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class PassengerPacketEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    @Getter
    private final int targetEntityID;

    /**
     * Passengers which are getting removed or added (Just an updated list)
     */
    @Getter
    private final List<Integer> passengerList;
    @Getter
    private final List<Player> packetReceivers;

    public PassengerPacketEvent(int targetEntityID, List<Integer> passengerList, List<Player> packetReceivers) {
        this.targetEntityID = targetEntityID;
        this.passengerList = passengerList;
        this.packetReceivers = packetReceivers;
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

