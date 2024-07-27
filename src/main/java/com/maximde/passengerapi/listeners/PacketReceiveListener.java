package com.maximde.passengerapi.listeners;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.*;
import com.maximde.passengerapi.PassengerAPI;
import com.maximde.passengerapi.events.AsyncPacketBasedVehicleExitEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class PacketReceiveListener implements PacketListener {

    private final PassengerAPI passengerAPI;

    public PacketReceiveListener(PassengerAPI passengerAPI) {
        this.passengerAPI = passengerAPI;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if(event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
            WrapperPlayClientSteerVehicle packet = new WrapperPlayClientSteerVehicle(event);
            //when player tries to leave any vehicle (also ones which don't exist on the server)
            if(packet.getFlags() == 2) {
                Player player = Bukkit.getPlayer(event.getUser().getName());
                if(player == null || !passengerAPI.getVehicles().containsKey(player)) return;
                int vehicle = passengerAPI.getVehicles().get(player);
                AsyncPacketBasedVehicleExitEvent vehicleExitEvent = new AsyncPacketBasedVehicleExitEvent(true, vehicle, player);
                passengerAPI.getServer().getPluginManager().callEvent(vehicleExitEvent);
                if(vehicleExitEvent.isCancelled()) return;
                passengerAPI.getServer().getScheduler().runTask(passengerAPI, t -> passengerAPI.getPassengerManager().removePassenger(vehicle, player.getEntityId(), true));
            }
        }
    }
}
