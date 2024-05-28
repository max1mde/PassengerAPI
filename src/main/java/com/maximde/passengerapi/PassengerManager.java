package com.maximde.passengerapi;

import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import com.maximde.passengerapi.events.AddPassengerEvent;
import com.maximde.passengerapi.events.PassengerPacketEvent;
import com.maximde.passengerapi.events.RemovePassengerEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PassengerManager {
    /**
     * String -> Plugin name
     * Integer -> Passenger entity ID
     * List<Integer> -> The passenger ID's for this entity
     */
    private final Map<String, Map<Integer, Set<Integer>>> passengersHashmap = new ConcurrentHashMap<>();
    private final PlayerManager playerManager;

    public PassengerManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public PassengerActions initActions(JavaPlugin plugin) {
        String pluginName = plugin.getDescription().getName();
        return new PassengerActionsImpl(pluginName);
    }
    private class PassengerActionsImpl implements PassengerActions {
        private final String pluginName;

        PassengerActionsImpl(String pluginName) {
            this.pluginName = pluginName;
        }

        @Override
        public void addPassenger(int targetEntity, int passengerEntity) {
            AddPassengerEvent addPassengerEvent = new AddPassengerEvent(targetEntity, Set.of(passengerEntity), pluginName);
            if (addPassengerEvent.isCancelled()) return;
            passengersHashmap.computeIfAbsent(pluginName, k -> new HashMap<>())
                    .computeIfAbsent(targetEntity, k -> new HashSet<>())
                    .add(passengerEntity);
            sendPassengerPacket(targetEntity);
        }

        @Override
        public void addPassengers(int targetEntity, @NotNull Set<Integer> passengerIDs) {
            AddPassengerEvent addPassengerEvent = new AddPassengerEvent(targetEntity, passengerIDs, pluginName);
            if (addPassengerEvent.isCancelled()) return;
            passengersHashmap.computeIfAbsent(pluginName, k -> new HashMap<>())
                    .computeIfAbsent(targetEntity, k -> new HashSet<>())
                    .addAll(passengerIDs);
            sendPassengerPacket(targetEntity);
        }

        @Override
        public void removePassenger(int targetEntity, int passengerID) {
            RemovePassengerEvent removePassengerEvent = new RemovePassengerEvent(targetEntity, Set.of(passengerID), pluginName);
            if (removePassengerEvent.isCancelled()) return;
            Map<Integer, Set<Integer>> pluginPassengers = passengersHashmap.get(pluginName);
            if (pluginPassengers == null) return;
            Set<Integer> passengers = pluginPassengers.get(targetEntity);
            if (passengers == null) return;
            passengers.remove(passengerID);
            sendPassengerPacket(targetEntity);
        }

        @Override
        public void removePassengers(int targetEntity, @NotNull Set<Integer> passengerIDs) {
            RemovePassengerEvent removePassengerEvent = new RemovePassengerEvent(targetEntity, passengerIDs, pluginName);
            if (removePassengerEvent.isCancelled()) return;
            Map<Integer, Set<Integer>> pluginPassengers = passengersHashmap.get(pluginName);
            if (pluginPassengers == null) return;
            Set<Integer> passengers = pluginPassengers.get(targetEntity);
            if (passengers == null) return;
            passengers.removeAll(passengerIDs);
            sendPassengerPacket(targetEntity);
        }

        @Override
        public void removeAllPassengers(int targetEntity) {
            Map<Integer, Set<Integer>> pluginPassengers = passengersHashmap.get(pluginName);
            if (pluginPassengers == null) return;
            RemovePassengerEvent removePassengerEvent = new RemovePassengerEvent(targetEntity, pluginPassengers.get(targetEntity), pluginName);
            if (removePassengerEvent.isCancelled()) return;
            pluginPassengers.remove(targetEntity);
            sendPassengerPacket(targetEntity);
        }

        @Override
        public Set<Integer> getPassengers(int targetEntity) {
            Map<Integer, Set<Integer>> pluginPassengers = passengersHashmap.get(pluginName);
            if (pluginPassengers == null) return Set.of();
            Set<Integer> passengers = pluginPassengers.get(targetEntity);
            return passengers != null ? new HashSet<>(passengers) : Set.of();
        }


        @Override
        public void removeGlobalPassengers(int targetEntity, @NotNull Set<Integer> passengerIDs) {
            passengersHashmap.values().forEach(map -> {
                Set<Integer> passengers = map.get(targetEntity);
                if (passengers != null) {
                    passengers.removeAll(passengerIDs);
                }
            });
            sendPassengerPacket(targetEntity);
        }

        @Override
        public void removeAllGlobalPassengers(int targetEntity) {
            passengersHashmap.values().forEach(map -> map.remove(targetEntity));
            sendPassengerPacket(targetEntity);
        }


        @Override
        public Set<Integer> getGlobalPassengers(int targetEntity) {
            Set<Integer> allPassengers = new HashSet<>();
            passengersHashmap.values().forEach(map -> {
                Set<Integer> passengers = map.get(targetEntity);
                if (passengers != null) {
                    allPassengers.addAll(passengers);
                }
            });
            return allPassengers;
        }
    }

    private void sendPassengerPacket(int targetEntity) {
        Set<Integer> allPassengersList = passengersHashmap.values().stream()
                .flatMap(map -> map.get(targetEntity).stream().filter(Objects::nonNull))
                .collect(Collectors.toSet());

        int[] allPassengersArray = allPassengersList.stream().mapToInt(Integer::intValue).toArray();

        List<Player> receivers = new ArrayList<>(Bukkit.getOnlinePlayers());
        PassengerPacketEvent passengerPacketEvent = new PassengerPacketEvent(targetEntity, allPassengersList, receivers);
        WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(targetEntity, allPassengersArray);
        Bukkit.getPluginManager().callEvent(passengerPacketEvent);
        passengerPacketEvent.getPacketReceivers().forEach(player -> this.playerManager.sendPacket(player, packet));
    }
}
