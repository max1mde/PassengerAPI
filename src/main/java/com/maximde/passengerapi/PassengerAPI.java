package com.maximde.passengerapi;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.manager.protocol.ProtocolManager;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import com.maximde.passengerapi.events.AddPassengerEvent;
import com.maximde.passengerapi.events.PassengerPacketEvent;
import com.maximde.passengerapi.events.RemovePassengerEvent;
import com.maximde.passengerapi.utils.Metrics;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public final class PassengerAPI extends JavaPlugin {

    private static final HashMap<String, PassengerActions> actions = new HashMap<>();
    @Getter
    private PlayerManager playerManager;
    @Getter
    private ProtocolManager protocolManager;
    /**
     * String -> Plugin name
     * Integer -> Passenger entity ID
     * List<Integer> -> The passenger ID's for this entity
     */
    private final HashMap<String, HashMap<Integer, List<Integer>>> passengersHashmap = new HashMap<String, HashMap<Integer, List<Integer>>>();

    private static PassengerAPI instance;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().reEncodeByDefault(false)
                .checkForUpdates(false)
                .bStats(false);
        PacketEvents.getAPI().load();
    }


    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();
        playerManager = PacketEvents.getAPI().getPlayerManager();
        protocolManager = PacketEvents.getAPI().getProtocolManager();
        new Metrics(this, 22033);
    }

    public static PassengerActions getAPI(JavaPlugin plugin) {
        return instance.initActions(plugin);
    }

    /**
     * @param plugin -> The instance of your plugin main class
     *               because the api has to know which plugins access it
     * @return PassengerActions interface with an implementation for your plugin
     */
    private PassengerActions initActions(JavaPlugin plugin) {
        String pluginName = plugin.getDescription().getName();
        if (actions.containsKey(pluginName)) {
            return actions.get(pluginName);
        }
        PassengerActions passengerActions =
                new PassengerActions() {

                    @Override
                    public void addPassenger(int targetEntity, int passengerEntity) {
                        AddPassengerEvent addPassengerEvent = new AddPassengerEvent(targetEntity, List.of(passengerEntity), pluginName);
                        if(!addPassengerEvent.isCancelled()) {
                        HashMap<Integer, List<Integer>> pluginPassengers = passengersHashmap.computeIfAbsent(pluginName, k -> new HashMap<>());
                            pluginPassengers.computeIfAbsent(targetEntity, k -> new ArrayList<>()).add(passengerEntity);
                            sendPassengerPacket(targetEntity);
                        }
                    }

                    @Override
                    public void addPassengers(int targetEntity, List<Integer> passengerIDs) {
                        AddPassengerEvent addPassengerEvent = new AddPassengerEvent(targetEntity, passengerIDs, pluginName);
                        if(!addPassengerEvent.isCancelled()) {
                            HashMap<Integer, List<Integer>> pluginPassengers = passengersHashmap.computeIfAbsent(pluginName, k -> new HashMap<>());
                            pluginPassengers.computeIfAbsent(targetEntity, k -> new ArrayList<>()).addAll(passengerIDs);
                            sendPassengerPacket(targetEntity);
                        }
                    }

                    @Override
                    public void removePassenger(int targetEntity, int passengerID) {
                        RemovePassengerEvent removePassengerEvent = new RemovePassengerEvent(targetEntity, List.of(passengerID), pluginName);
                        if(!removePassengerEvent.isCancelled()) {
                            HashMap<Integer, List<Integer>> pluginPassengers = passengersHashmap.get(pluginName);
                            if (pluginPassengers != null) {
                                List<Integer> passengers = pluginPassengers.get(targetEntity);
                                if (passengers != null) {
                                    passengers.remove(Integer.valueOf(passengerID));
                                    sendPassengerPacket(targetEntity);
                                }
                            }
                        }
                    }

                    @Override
                    public void removePassengers(int targetEntity, List<Integer> passengerIDs) {
                        RemovePassengerEvent removePassengerEvent = new RemovePassengerEvent(targetEntity, passengerIDs, pluginName);
                        if(!removePassengerEvent.isCancelled()) {
                            HashMap<Integer, List<Integer>> pluginPassengers = passengersHashmap.get(pluginName);
                            if (pluginPassengers != null) {
                                List<Integer> passengers = pluginPassengers.get(targetEntity);
                                if (passengers != null) {
                                    passengers.removeAll(passengerIDs);
                                    sendPassengerPacket(targetEntity);
                                }
                            }
                        }
                    }

                    @Override
                    public void removeAllPassengers(int targetEntity) {
                        HashMap<Integer, List<Integer>> pluginPassengers = passengersHashmap.get(pluginName);
                        if (pluginPassengers != null) {
                            RemovePassengerEvent removePassengerEvent = new RemovePassengerEvent(targetEntity, pluginPassengers.get(targetEntity), pluginName);
                            if (!removePassengerEvent.isCancelled()) {
                                pluginPassengers.remove(targetEntity);
                                sendPassengerPacket(targetEntity);
                            }
                        }
                    }

                    @Override
                    public List<Integer> getPassengers(int targetEntity) {
                        HashMap<Integer, List<Integer>> pluginPassengers = passengersHashmap.get(pluginName);
                        if (pluginPassengers != null) {
                            List<Integer> passengers = pluginPassengers.get(targetEntity);
                            return passengers != null ? new ArrayList<>(passengers) : List.of();
                        }
                        return List.of();
                    }


                    @Override
                    public void removeGlobalPassengers(int targetEntity, List<Integer> passengerIDs) {
                        passengersHashmap.values().forEach(map -> {
                            List<Integer> passengers = map.get(targetEntity);
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
                    public List<Integer> getGlobalPassengers(int targetEntity) {
                        List<Integer> allPassengers = new ArrayList<>();
                        passengersHashmap.values().forEach(map -> {
                            List<Integer> passengers = map.get(targetEntity);
                            if (passengers != null) {
                                allPassengers.addAll(passengers);
                            }
                        });
                        return allPassengers;
                    }
                };
        actions.put(pluginName, passengerActions);
        return actions.get(pluginName);
    }

    private void sendPassengerPacket(int targetEntity) {
        List<Integer> allPassengersList = passengersHashmap.values().stream()
                .flatMap(map -> map.values().stream())
                .flatMap(List::stream)
                .toList();

        int[] allPassengersArray = allPassengersList.stream().mapToInt(Integer::intValue).toArray();

        List<Player> receivers = new ArrayList<>(Bukkit.getOnlinePlayers());
        PassengerPacketEvent passengerPacketEvent = new PassengerPacketEvent(targetEntity, allPassengersList, receivers);
        WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(targetEntity, allPassengersArray);
        Bukkit.getPluginManager().callEvent(passengerPacketEvent);
        passengerPacketEvent.getPacketReceivers().forEach(p -> getPlayerManager().sendPacket(p, packet));
    }

}
