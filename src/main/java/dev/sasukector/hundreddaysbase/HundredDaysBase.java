package dev.sasukector.hundreddaysbase;

import dev.sasukector.hundreddaysbase.commands.ToggleDaysCommand;
import dev.sasukector.hundreddaysbase.commands.PlayedCommand;
import dev.sasukector.hundreddaysbase.controllers.BoardController;
import dev.sasukector.hundreddaysbase.events.SpawnEvents;
import dev.sasukector.hundreddaysbase.helpers.ServerUtilities;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class HundredDaysBase extends JavaPlugin {

    private static @Getter HundredDaysBase instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info(ChatColor.DARK_PURPLE + "HundredDaysBase startup!");
        instance = this;

        // Register events
        this.getServer().getPluginManager().registerEvents(new SpawnEvents(), this);
        Bukkit.getOnlinePlayers().forEach(player -> BoardController.getInstance().newPlayerBoard(player));

        // Register commands
        Objects.requireNonNull(HundredDaysBase.getInstance().getCommand("played")).setExecutor(new PlayedCommand());
        Objects.requireNonNull(HundredDaysBase.getInstance().getCommand("toggleDays")).setExecutor(new ToggleDaysCommand());

        World overworld = ServerUtilities.getOverworld();
        if (overworld != null) {
            overworld.getWorldBorder().setSize(2000);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info(ChatColor.DARK_PURPLE + "HundredDaysBase shutdown!");
    }
}
