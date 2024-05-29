package com.maximde.passengerapi;

import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import com.maximde.passengerapi.events.AddPassengerEvent;
import com.maximde.passengerapi.events.PassengerPacketEvent;
import com.maximde.passengerapi.events.RemovePassengerEvent;
import lombok.Getter;
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
    @Getter
    private final Map<String, Map<Integer, Set<Integer>>> passengersHashmap = new ConcurrentHashMap<>();
    private final PlayerManager playerManager;
    private final String PLUGIN_NAME = "PassengerAPI (Internal)";
    private final PassengerAPI passengerAPI;

    public PassengerManager(PlayerManager playerManager, PassengerAPI passengerAPI) {
        this.playerManager = playerManager;
        this.passengerAPI = passengerAPI;
    }

    public PassengerActions initActions(JavaPlugin plugin) {
        String pluginName = plugin.getDescription().getName();
        return new PassengerActionsImpl(pluginName);
    }

    public int getTotalPassengersCount() {
        return passengersHashmap.values().stream()
                .flatMap(map -> map.values().stream())
                .mapToInt(Set::size)
                .sum();
    }

    public int getTotalTargetEntitiesCount() {
        return passengersHashmap.values().stream()
                .flatMap(map -> map.keySet().stream())
                .collect(Collectors.toSet())
                .size();
    }

    /**
     * Internal method for the PassengerAPI
     * Don't even try to use it somehow in your own plugin!
     */
    public void removePassengers(int[] passengerIDs, boolean sendPackets) {
        Bukkit.getScheduler().runTask(passengerAPI, bukkitTask -> {
            Set<Integer> passengerSet = Arrays.stream(passengerIDs).boxed().collect(Collectors.toSet());
            RemovePassengerEvent removePassengerEvent = new RemovePassengerEvent(-1, passengerSet, PLUGIN_NAME);
            Bukkit.getPluginManager().callEvent(removePassengerEvent);
            if (removePassengerEvent.isCancelled()) return;

            Map<String, Map<Integer, Set<Integer>>> tempHashmap = new HashMap<>(passengersHashmap);
            for (Map.Entry<String, Map<Integer, Set<Integer>>> entry : tempHashmap.entrySet()) {
                Map<Integer, Set<Integer>> pluginMap = entry.getValue();
                Map<Integer, Set<Integer>> newPluginMap = new HashMap<>();

                for (Map.Entry<Integer, Set<Integer>> pluginEntry : pluginMap.entrySet()) {
                    int targetEntity = pluginEntry.getKey();
                    Set<Integer> passengers = new HashSet<>(pluginEntry.getValue());
                    passengers.removeAll(passengerSet);
                    if (!passengers.isEmpty()) {
                        newPluginMap.put(targetEntity, passengers);
                    }
                }

                if (!newPluginMap.isEmpty()) {
                    passengersHashmap.put(entry.getKey(), newPluginMap);
                } else {
                    passengersHashmap.remove(entry.getKey());
                }
            }

            if (sendPackets) sendPassengerPackets();
        });
    }

    /**
     * Internal method for the PassengerAPI
     * Don't even try to use it somehow in your own plugin!
     */
    public void addPassengers(int targetEntity, int[] passengerIDs, boolean sendPackets) {
        Bukkit.getScheduler().runTask(passengerAPI, bukkitTask -> {
            Set<Integer> passengerSet = Arrays.stream(passengerIDs)
                    .boxed()
                    .collect(Collectors.toSet());

            AddPassengerEvent addPassengerEvent = new AddPassengerEvent(targetEntity, passengerSet, PLUGIN_NAME);
            Bukkit.getPluginManager().callEvent(addPassengerEvent);
            if (addPassengerEvent.isCancelled()) return;
            passengersHashmap.computeIfAbsent(PLUGIN_NAME, k -> new HashMap<>())
                    .computeIfAbsent(targetEntity, k -> new HashSet<>())
                    .addAll(passengerSet);

            if (sendPackets) sendPassengerPacket(targetEntity);
        });
    }


    private class PassengerActionsImpl implements PassengerActions {
        private final String pluginName;

        PassengerActionsImpl(String pluginName) {
            this.pluginName = pluginName;
        }

        @Override
        public void addPassenger(int targetEntity, int passengerEntity) {
            AddPassengerEvent addPassengerEvent = new AddPassengerEvent(targetEntity, Set.of(passengerEntity), pluginName);
            Bukkit.getPluginManager().callEvent(addPassengerEvent);
            if (addPassengerEvent.isCancelled()) return;
            passengersHashmap.computeIfAbsent(pluginName, k -> new HashMap<>())
                    .computeIfAbsent(targetEntity, k -> new HashSet<>())
                    .add(passengerEntity);
            sendPassengerPacket(targetEntity);
        }

        @Override
        public void addPassengers(int targetEntity, @NotNull Set<Integer> passengerIDs) {
            AddPassengerEvent addPassengerEvent = new AddPassengerEvent(targetEntity, passengerIDs, pluginName);
            Bukkit.getPluginManager().callEvent(addPassengerEvent);
            if (addPassengerEvent.isCancelled()) return;
            passengersHashmap.computeIfAbsent(pluginName, k -> new HashMap<>())
                    .computeIfAbsent(targetEntity, k -> new HashSet<>())
                    .addAll(passengerIDs);
            sendPassengerPacket(targetEntity);
        }

        @Override
        public void addPassengers(int targetEntity, int[] passengerIDs) {
            Set<Integer> passengerSet = Arrays.stream(passengerIDs)
                    .boxed()
                    .collect(Collectors.toSet());

            AddPassengerEvent addPassengerEvent = new AddPassengerEvent(targetEntity, passengerSet, pluginName);
            Bukkit.getPluginManager().callEvent(addPassengerEvent);
            if (addPassengerEvent.isCancelled()) return;
            passengersHashmap.computeIfAbsent(pluginName, k -> new HashMap<>())
                    .computeIfAbsent(targetEntity, k -> new HashSet<>())
                    .addAll(passengerSet);

            sendPassengerPacket(targetEntity);
        }

        @Override
        public void removePassenger(int targetEntity, int passengerID) {
            RemovePassengerEvent removePassengerEvent = new RemovePassengerEvent(targetEntity, Set.of(passengerID), pluginName);
            Bukkit.getPluginManager().callEvent(removePassengerEvent);
            if (removePassengerEvent.isCancelled()) return;
            Map<Integer, Set<Integer>> pluginPassengers = passengersHashmap.get(pluginName);
            if (pluginPassengers == null) return;
            Set<Integer> passengers = pluginPassengers.get(targetEntity);
            if (passengers == null) return;
            passengers.remove(passengerID);
            if (passengers.isEmpty()) {
                pluginPassengers.remove(targetEntity);
            }
            sendPassengerPacket(targetEntity);
        }

        @Override
        public void removePassenger(int passengerID) {
            RemovePassengerEvent removePassengerEvent = new RemovePassengerEvent(-1, Set.of(passengerID), pluginName);
            Bukkit.getPluginManager().callEvent(removePassengerEvent);
            if (removePassengerEvent.isCancelled()) return;

            passengersHashmap.get(pluginName).values().forEach(passengers -> passengers.remove(passengerID));
            sendPassengerPackets(pluginName);
        }

        @Override
        public void removePassengers(int targetEntity, @NotNull Set<Integer> passengerIDs) {
            RemovePassengerEvent removePassengerEvent = new RemovePassengerEvent(targetEntity, passengerIDs, pluginName);
            Bukkit.getPluginManager().callEvent(removePassengerEvent);
            if (removePassengerEvent.isCancelled()) return;
            Map<Integer, Set<Integer>> pluginPassengers = passengersHashmap.get(pluginName);
            if (pluginPassengers == null) return;
            Set<Integer> passengers = pluginPassengers.get(targetEntity);
            if (passengers == null) return;
            passengers.removeAll(passengerIDs);
            if (passengers.isEmpty()) {
                pluginPassengers.remove(targetEntity);
            }
            sendPassengerPacket(targetEntity);
        }

        @Override
        public void removePassengers(int targetEntity, int[] passengerIDs) {
            Set<Integer> passengerSet = Arrays.stream(passengerIDs)
                    .boxed()
                    .collect(Collectors.toSet());

            RemovePassengerEvent removePassengerEvent = new RemovePassengerEvent(targetEntity, passengerSet, pluginName);
            Bukkit.getPluginManager().callEvent(removePassengerEvent);
            if (removePassengerEvent.isCancelled()) return;
            Map<Integer, Set<Integer>> pluginPassengers = passengersHashmap.get(pluginName);
            if (pluginPassengers == null) return;
            Set<Integer> passengers = pluginPassengers.get(targetEntity);
            if (passengers == null) return;
            passengers.removeAll(passengerSet);
            if (passengers.isEmpty()) {
                pluginPassengers.remove(targetEntity);
            }
            sendPassengerPacket(targetEntity);
        }

        @Override
        public void removePassengers(int[] passengerIDs) {
            Set<Integer> passengerSet = Arrays.stream(passengerIDs).boxed().collect(Collectors.toSet());
            removePassengers(passengerSet);
        }

        @Override
        public void removePassengers(@NotNull Set<Integer> passengerIDs) {
            RemovePassengerEvent removePassengerEvent = new RemovePassengerEvent(-1, passengerIDs, pluginName);
            Bukkit.getPluginManager().callEvent(removePassengerEvent);
            if (removePassengerEvent.isCancelled()) return;

            passengersHashmap.keySet().forEach(key -> {
                passengersHashmap.get(key).values().forEach(passengers -> passengers.removeAll(passengerIDs));
                passengersHashmap.get(key).forEach((target, passengers) -> {
                    passengers.removeAll(passengerIDs);
                    if (passengersHashmap.get(key).get(target).isEmpty()) {
                        passengersHashmap.get(key).remove(target);
                    }
                });
                if (passengersHashmap.get(key).isEmpty()) {
                    passengersHashmap.remove(key);
                }
            });

            sendPassengerPackets(pluginName);
        }

        @Override
        public void removeAllPassengers(int targetEntity) {
            Map<Integer, Set<Integer>> pluginPassengers = passengersHashmap.get(pluginName);
            if (pluginPassengers == null) return;
            RemovePassengerEvent removePassengerEvent = new RemovePassengerEvent(targetEntity, pluginPassengers.get(targetEntity), pluginName);
            Bukkit.getPluginManager().callEvent(removePassengerEvent);
            if (removePassengerEvent.isCancelled()) return;
            pluginPassengers.remove(targetEntity, null);
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

    private void sendPassengerPackets() {
        passengersHashmap.keySet().forEach(this::sendPassengerPackets);
    }

    private void sendPassengerPackets(String pluginName) {
        passengersHashmap.get(pluginName).keySet().forEach(PassengerManager.this::sendPassengerPacket);
    }

    private void sendPassengerPacket(int targetEntity) {
        Set<Integer> allPassengersList = passengersHashmap.values().stream()
                .filter(map -> map.get(targetEntity) != null)
                .flatMap(map -> map.get(targetEntity).stream().filter(Objects::nonNull))
                .collect(Collectors.toSet());

        int[] allPassengersArray = allPassengersList.stream().mapToInt(Integer::intValue).toArray();

        List<Player> receivers = new ArrayList<>(Bukkit.getOnlinePlayers());
        PassengerPacketEvent passengerPacketEvent = new PassengerPacketEvent(targetEntity, allPassengersList, receivers);
        WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(targetEntity, allPassengersArray);
        Bukkit.getPluginManager().callEvent(passengerPacketEvent);
        passengerPacketEvent.getPacketReceivers().forEach(player -> this.playerManager.sendPacketSilently(player, packet));
    }
}
