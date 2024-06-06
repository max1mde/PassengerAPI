package com.maximde.passengerapi.listeners;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import com.maximde.passengerapi.PassengerAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.checkerframework.framework.qual.Unused;

import java.util.concurrent.atomic.AtomicReference;


public class PacketSendListener implements PacketListener {

    private final PassengerAPI passengerAPI;

    public PacketSendListener(PassengerAPI passengerAPI) {
        this.passengerAPI = passengerAPI;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if(event.getPacketType() == PacketType.Play.Server.SET_PASSENGERS && this.passengerAPI.getPassengerConfig().isListenToPassengerSet()) {
            WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(event);
            passengerAPI.getPassengerManager().addPassengers(packet.getEntityId(), packet.getPassengers(), true);
            event.setCancelled(true);
        }
        if(event.getPacketType() == PacketType.Play.Server.DESTROY_ENTITIES && this.passengerAPI.getPassengerConfig().isListenToEntityDestroy()) {
            WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(event);
            passengerAPI.getPassengerManager().removePassengers(packet.getEntityIds(), false);
        }
    }

    private void debugPacket(WrapperPlayServerSetPassengers packet) {
        AtomicReference<Entity> entity = new AtomicReference<>();
        Bukkit.getScheduler().runTask(passengerAPI, t -> {
            Bukkit.getWorld("world").getEntities().forEach(e -> {
                if(e.getEntityId() == packet.getEntityId()) entity.set(e);
            });

            StringBuilder entityIDs = new StringBuilder();
            for (int passenger : packet.getPassengers()) {
                entityIDs.append(passenger + ", ");
            }

            Bukkit.broadcastMessage(ChatColor.RED + "" + entity.get().getName() + "  -> " + entityIDs.toString());
        });
    }

    // TODO replace with something better...
    public boolean doesEntityExist(int entityId) {
        for(World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getEntityId() == entityId) {
                    return true;
                }
            }
        }
        return false;
    }

}
