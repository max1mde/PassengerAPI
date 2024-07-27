package com.maximde.passengerapi;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.manager.protocol.ProtocolManager;
import com.maximde.passengerapi.command.PassengerCommand;
import com.maximde.passengerapi.command.PassengerTabCompleter;
import com.maximde.passengerapi.debugger.DebugEvents;
import com.maximde.passengerapi.listeners.*;
import com.maximde.passengerapi.utils.Config;
import com.maximde.passengerapi.utils.Metrics;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


public final class PassengerAPI extends JavaPlugin {

    @Getter
    private PlayerManager playerManager;

    private ProtocolManager protocolManager;
    private static PassengerAPI instance;
    @Getter
    private DebugEvents debugEvents;
    @Getter
    private PassengerManager passengerManager;
    @Getter
    private Config passengerConfig;

    @Getter
    private final ConcurrentHashMap<Player, Integer> vehicles = new ConcurrentHashMap<>();


    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().reEncodeByDefault(false)
                .checkForUpdates(false)
                .bStats(true);
        this.playerManager = PacketEvents.getAPI().getPlayerManager();
        this.protocolManager = PacketEvents.getAPI().getProtocolManager();
        PacketEvents.getAPI().getEventManager().registerListener(new PacketSendListener(this),
                PacketListenerPriority.HIGHEST);
        PacketEvents.getAPI().getEventManager().registerListener(new PacketReceiveListener(this),
                PacketListenerPriority.HIGHEST);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        instance = this;
        PacketEvents.getAPI().init();
        this.passengerConfig = new Config(getDataFolder());
        this.passengerManager = new PassengerManager(playerManager, this);
        this.debugEvents = new DebugEvents(this);
        getCommand("passengerapi").setExecutor(new PassengerCommand(this));
        getCommand("passengerapi").setTabCompleter(new PassengerTabCompleter(this));
        getServer().getPluginManager().registerEvents(new EntityUnMountListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityMountListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(debugEvents, this);
        new Metrics(this, 22033);
    }

    public static PassengerActions getAPI(JavaPlugin plugin) {
        if (instance == null) {
            throw new NullPointerException("The plugin: " + plugin.getDescription().getName() +
                    " Tried to access the passenger api before it has been initialized (Use getAPI in your onEnable() not earlier)." +
                    " Either the depend PassengerAPI in the plugin.yml is missing or the plugin: " + plugin.getDescription().getName() + " " +
                    "shaded the passenger API into the own jar which should never happen!");
        }
        return instance.passengerManager.initActions(plugin);
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }
}
