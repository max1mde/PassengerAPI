package com.maximde.passengerapi.debugger;

import com.maximde.passengerapi.PassengerAPI;
import com.maximde.passengerapi.PassengerManager;
import com.maximde.passengerapi.events.AsyncAddPassengerEvent;
import com.maximde.passengerapi.events.AsyncPassengerPacketEvent;
import com.maximde.passengerapi.events.AsyncRemovePassengerEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
        passengerAPI.getServer().getScheduler().runTaskTimer(this.passengerAPI, this::updateActionBar, 0, 20);
    }

    private void updateActionBar() {
        if (debugPlayers.isEmpty()) return;

        PassengerManager passengerManager = passengerAPI.getPassengerManager();
        StringBuilder actionBarText = new StringBuilder();

        actionBarText.append(ChatColor.DARK_GREEN + "Plugins Using PassengerAPI: " + ChatColor.WHITE)
                .append(passengerManager.getPassengersHashmap().keySet().size())
                .append(ChatColor.DARK_GREEN + " Total Passengers: " + ChatColor.WHITE)
                .append(passengerManager.getTotalPassengersCount())
                .append(ChatColor.DARK_GREEN + " Total Target Entities: " + ChatColor.WHITE)
                .append(passengerManager.getTotalTargetEntitiesCount());


        for (Player player : debugPlayers) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionBarText.toString()));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        debugPlayers.remove(event.getPlayer());
    }

    @EventHandler
    public void onAddPassengerEvent(AsyncAddPassengerEvent event) {
        for (Player player : debugPlayers) {
            if(player.getItemInHand().getType() == Material.AIR) continue;
            player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.YELLOW + "Debug" + ChatColor.DARK_GREEN + "] " + ChatColor.WHITE + "AddPassengerEvent: Plugin=" + event.getPluginName() +
                    ", TargetEntityID=" + event.getTargetEntityID() + ", PassengersAmount=" + event.getPassengerList().size());
        }
    }

    @EventHandler
    public void onRemovePassengerEvent(AsyncRemovePassengerEvent event) {
        for (Player player : debugPlayers) {
            if(player.getItemInHand().getType() == Material.AIR) continue;
            player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.YELLOW + "Debug" + ChatColor.DARK_GREEN + "] " + ChatColor.WHITE + "RemovePassengerEvent: Plugin=" + event.getPluginName() +
                    ", TargetEntityID=" + event.getTargetEntityID() + ", PassengersAmount=" + event.getPassengerList().size());
        }
    }

    @EventHandler
    public void onPassengerPacketEvent(AsyncPassengerPacketEvent event) {
        for (Player player : debugPlayers) {
            if(player.getItemInHand().getType() == Material.AIR) continue;
            player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.YELLOW + "Debug" + ChatColor.DARK_GREEN + "] " + ChatColor.LIGHT_PURPLE + "PassengerPacketEvent: TargetEntityID=" + event.getTargetEntityID() +
                    ", PassengersAmount=" + event.getPassengerList().size() + ", PacketReceiversAmount=" + event.getPacketReceivers().size());
        }
    }

}
