package com.maximde.passengerapi.listeners;

import com.maximde.passengerapi.PassengerAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;

public record EntityUnMountListener(PassengerAPI passengerAPI) implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(VehicleExitEvent event) {
       if(!event.isCancelled() && passengerAPI.getPassengerConfig().isVehicleExitEvent()) passengerAPI.getPassengerManager().removePassenger(false, event.getVehicle().getEntityId(), event.getExited().getEntityId(), false);
    }
}
