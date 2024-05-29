package com.maximde.passengerapi.command;

import com.maximde.passengerapi.PassengerAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PassengerCommand implements CommandExecutor {

    private final PassengerAPI passengerAPI;

    public PassengerCommand(PassengerAPI passengerAPI) {
        this.passengerAPI = passengerAPI;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player player)) return false;

        if(!player.hasPermission("passengerapi.commands")) {
            player.sendMessage(ChatColor.RED + "Missing permissions!");
            return false;
        }

        if(args.length < 1) {
            player.sendMessage("Commands:\n" +
                    "- /passengerapi debug\n" +
                    "- /passengerapi reload");
            return false;
        }


        switch (args[0]) {
            case "debug" -> passengerAPI.getDebugEvents().toggleDebugMode(player);
            case "reload" -> {
                passengerAPI.getPassengerConfig().reload();
                player.sendMessage(ChatColor.GREEN + "Config reloaded!");
            }
            default -> player.sendMessage(ChatColor.RED + "Command not found!");
        }

        return false;
    }
}
