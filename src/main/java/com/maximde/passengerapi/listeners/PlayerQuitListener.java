package com.maximde.passengerapi.listeners;

import com.maximde.passengerapi.PassengerAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public record PlayerQuitListener(PassengerAPI passengerAPI) implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        passengerAPI.getVehicles().remove(event.getPlayer());
    }

}
