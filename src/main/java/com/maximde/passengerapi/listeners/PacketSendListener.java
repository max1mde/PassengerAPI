package com.maximde.passengerapi.listeners;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import com.maximde.passengerapi.PassengerAPI;

public class PacketSendListener implements PacketListener {

    private final PassengerAPI passengerAPI;

    public PacketSendListener(PassengerAPI passengerAPI) {
        this.passengerAPI = passengerAPI;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if(event.getPacketType() == PacketType.Play.Server.SET_PASSENGERS) {
            WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(event);
            passengerAPI.getPassengerManager().addPassengers(packet.getEntityId(), packet.getPassengers(), false);
        }
        if(event.getPacketType() == PacketType.Play.Server.DESTROY_ENTITIES) {
            WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(event);
            passengerAPI.getPassengerManager().removePassengers(packet.getEntityIds(), false);
        }
    }

}
