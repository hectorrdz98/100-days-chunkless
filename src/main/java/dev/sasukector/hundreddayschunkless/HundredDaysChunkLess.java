package dev.sasukector.hundreddayschunkless;

import dev.sasukector.hundreddayschunkless.commands.ToggleDaysCommand;
import dev.sasukector.hundreddayschunkless.commands.PlayedCommand;
import dev.sasukector.hundreddayschunkless.controllers.BoardController;
import dev.sasukector.hundreddayschunkless.controllers.ChunksController;
import dev.sasukector.hundreddayschunkless.events.SpawnEvents;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class HundredDaysChunkLess extends JavaPlugin {

    private static @Getter HundredDaysChunkLess instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info(ChatColor.DARK_PURPLE + "HundredDaysChunkLess startup!");
        instance = this;

        // Register events
        this.getServer().getPluginManager().registerEvents(new SpawnEvents(), this);
        ChunksController.getInstance().loadDeletedChunksFromFile();
        Bukkit.getOnlinePlayers().forEach(player -> BoardController.getInstance().newPlayerBoard(player));

        // Register commands
        Objects.requireNonNull(HundredDaysChunkLess.getInstance().getCommand("played")).setExecutor(new PlayedCommand());
        Objects.requireNonNull(HundredDaysChunkLess.getInstance().getCommand("toggleDays")).setExecutor(new ToggleDaysCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info(ChatColor.DARK_PURPLE + "HundredDaysChunkLess shutdown!");
    }
}
