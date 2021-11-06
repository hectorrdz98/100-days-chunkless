package dev.sasukector.hundreddayschunkless.commands;

import dev.sasukector.hundreddayschunkless.controllers.BoardController;
import dev.sasukector.hundreddayschunkless.helpers.ServerUtilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ToggleDaysCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player && player.isOp()) {
            player.playSound(player.getLocation(), "minecraft:block.note_block.bell", 1, 1);
            BoardController.getInstance().setHideDays(!BoardController.getInstance().isHideDays());
            ServerUtilities.sendServerMessage(player, "Se han " +
                    (BoardController.getInstance().isHideDays() ? "ocultado" : "mostrado") + " los días");
        } else if (sender instanceof Player player) {
            ServerUtilities.sendServerMessage(player, "§cPermisos insuficientes");
        }
        return true;
    }

}
