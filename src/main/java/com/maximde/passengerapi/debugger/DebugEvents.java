package com.maximde.passengerapi.debugger;

import com.maximde.passengerapi.PassengerAPI;
import com.maximde.passengerapi.PassengerManager;
import com.maximde.passengerapi.events.AddPassengerEvent;
import com.maximde.passengerapi.events.PassengerPacketEvent;
import com.maximde.passengerapi.events.RemovePassengerEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;

public class DebugEvents implements Listener {
    private final Set<Player> debugPlayers = new HashSet<>();
    private final PassengerAPI passengerAPI;

    public DebugEvents(PassengerAPI passengerAPI) {
        this.passengerAPI = passengerAPI;
        startActionBarTask();
    }

    public void toggleDebugMode(Player player) {
        if (debugPlayers.contains(player)) {
            debugPlayers.remove(player);
            player.sendMessage(ChatColor.GREEN + "Debug mode disabled.");
            return;
        }
        debugPlayers.add(player);
        player.sendMessage(ChatColor.GREEN + "Debug mode enabled.");
    }

    private void startActionBarTask() {
        passengerAPI.getServer().getScheduler().runTaskTimer(this.passengerAPI, this::updateActionBar, 0, 50);
    }

    private void updateActionBar() {
        if (debugPlayers.isEmpty()) return;

        PassengerManager passengerManager = passengerAPI.getPassengerManager();
        StringBuilder actionBarText = new StringBuilder();

        actionBarText.append(ChatColor.DARK_GREEN + "Plugins Setting Passengers: " + ChatColor.WHITE);
        actionBarText.append(passengerManager.getPassengersHashmap().keySet().size());
        actionBarText.append(ChatColor.DARK_GREEN + " Total Passengers: " + ChatColor.WHITE);
        actionBarText.append(passengerManager.getTotalPassengersCount());
        actionBarText.append(ChatColor.DARK_GREEN + " Total Target Entities: " + ChatColor.WHITE);
        actionBarText.append(passengerManager.getTotalTargetEntitiesCount());


        for (Player player : debugPlayers) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionBarText.toString()));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        debugPlayers.remove(event.getPlayer());
    }

    @EventHandler
    public void onAddPassengerEvent(AddPassengerEvent event) {
        for (Player player : debugPlayers) {
            player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.YELLOW + "Debug" + ChatColor.DARK_GREEN + "] " + ChatColor.WHITE + "AddPassengerEvent: Plugin=" + event.getPluginName() +
                    ", TargetEntity=" + event.getTargetEntityID() + ", Passengers=" + event.getPassengerList().size());
        }
    }

    @EventHandler
    public void onRemovePassengerEvent(RemovePassengerEvent event) {
        for (Player player : debugPlayers) {
            player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.YELLOW + "Debug" + ChatColor.DARK_GREEN + "] " + ChatColor.WHITE + "RemovePassengerEvent: Plugin=" + event.getPluginName() +
                    ", TargetEntity=" + event.getTargetEntityID() + ", Passengers=" + event.getPassengerList().size());
        }
    }

    @EventHandler
    public void onPassengerPacketEvent(PassengerPacketEvent event) {
        for (Player player : debugPlayers) {
            player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.YELLOW + "Debug" + ChatColor.DARK_GREEN + "] " + ChatColor.WHITE + "PassengerPacketEvent: TargetEntity=" + event.getTargetEntityID() +
                    ", Passengers=" + event.getPassengerList().size() + ", PacketReceivers=" + event.getPacketReceivers().size());
        }
    }

}
