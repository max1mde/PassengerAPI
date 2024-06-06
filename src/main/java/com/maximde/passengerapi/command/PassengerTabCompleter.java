package com.maximde.passengerapi.command;

import com.maximde.passengerapi.PassengerAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PassengerTabCompleter implements TabCompleter {

    private final PassengerAPI passengerAPI;

    public PassengerTabCompleter(PassengerAPI passengerAPI) {
        this.passengerAPI = passengerAPI;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender instanceof Player player) {
            if(!player.hasPermission("passengerapi.commands")) {
                return new ArrayList<>();
            }
        }

        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            commands.addAll(Arrays.asList("debug", "reload"));
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }

        return completions;
    }
}
