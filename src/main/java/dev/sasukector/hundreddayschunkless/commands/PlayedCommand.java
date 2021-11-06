package dev.sasukector.hundreddayschunkless.commands;

import dev.sasukector.hundreddayschunkless.helpers.ServerUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PlayedCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length > 0) {
                String playerName = args[0];
                Player searchedPlayer = Bukkit.getPlayer(playerName);
                if (searchedPlayer != null && validOptions().contains(playerName)) {
                    double hours = searchedPlayer.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20.0/ 60.0 / 60.0;
                    player.playSound(player.getLocation(), "minecraft:block.note_block.bell", 1, 1);
                    ServerUtilities.sendServerMessage(player, ServerUtilities.getMiniMessage()
                            .parse("<bold><color:#0091AD>" + searchedPlayer.getName() +
                                    "</color></bold> ha jugado <bold><color:#B7094C>" + String.format("%.2f", hours) +
                                    " h</color></bold>"));
                } else {
                    ServerUtilities.sendServerMessage(player, "§cJugador no válido");
                }
            } else {
                ServerUtilities.sendServerMessage(player, "§cIndica el nombre del jugador");
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if(sender instanceof Player) {
            if (args.length == 1) {
                String partialItem = args[0];
                StringUtil.copyPartialMatches(partialItem, validOptions(), completions);
            }
        }

        Collections.sort(completions);

        return completions;
    }

    public List<String> validOptions() {
        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
    }

}
