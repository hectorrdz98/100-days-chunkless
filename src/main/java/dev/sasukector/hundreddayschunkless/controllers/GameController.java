package dev.sasukector.hundreddayschunkless.controllers;

import dev.sasukector.hundreddayschunkless.HundredDaysChunkLess;
import dev.sasukector.hundreddayschunkless.helpers.ServerUtilities;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class GameController {

    private static GameController instance = null;
    private @Getter long lastDay = 0;
    private @Getter int schedulerID = -1;

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    public GameController() {
        World overworld = ServerUtilities.getOverworld();
        if (overworld != null) {
            this.lastDay = (overworld.getFullTime() / 24000);
        }

        this.startChuckScheduler();
    }

    public void startChuckScheduler() {
        this.schedulerID = new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getLogger().info("Toca revisión de chunks");
                ChunksController.getInstance().saveDeletedChunksToFile();
                World overworld = ServerUtilities.getOverworld();
                if (overworld != null) {
                    long currentDay = overworld.getFullTime() / 24000;
                    if (currentDay != lastDay) {
                        lastDay = currentDay;
                        ChunksController.getInstance().deleteChunks();
                    }
                }
            }
        }.runTaskTimer(HundredDaysChunkLess.getInstance(), 0L, 30 * 20L).getTaskId();
    }

}
