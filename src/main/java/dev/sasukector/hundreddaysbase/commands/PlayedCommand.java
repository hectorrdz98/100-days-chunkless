package dev.sasukector.hundreddaysbase.commands;

import dev.sasukector.hundreddaysbase.helpers.ServerUtilities;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

import java.util.*;
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
                } else if (Arrays.asList("top", "topnt").contains(playerName)) {
                    player.playSound(player.getLocation(), "minecraft:block.note_block.bell", 1, 1);

                    Map<OfflinePlayer, Double> timeMap = new HashMap<>();
                    for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                        double hours = offlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20.0/ 60.0 / 60.0;
                        timeMap.put(offlinePlayer, hours);
                    }

                    String text;
                    Map<OfflinePlayer, Double> topTen;
                    if (playerName.equals("top")) {
                        text = "viciados";
                        topTen = timeMap.entrySet().stream()
                                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                .limit(10)
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
                    } else {
                        text = "flojos";
                        topTen = timeMap.entrySet().stream()
                                .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                                .limit(10)
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
                    }

                    StringBuilder playersKills = new StringBuilder();
                    for (Map.Entry<OfflinePlayer, Double> entry : topTen.entrySet()) {
                        playersKills.append("\n- ").append(entry.getKey().getName())
                                .append(" ").append(String.format("%.2f", entry.getValue()))
                                .append(" hrs");
                    }
                    ServerUtilities.sendServerMessage(player, ServerUtilities.getMiniMessage()
                            .parse("<bold><color:#F72585>Top " + text + "</color></bold><color:#B5179E>" +
                                    playersKills + "</color>"));
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
        List<String> valid = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
        valid.add("top");
        valid.add("topnt");
        return valid;
    }

}
