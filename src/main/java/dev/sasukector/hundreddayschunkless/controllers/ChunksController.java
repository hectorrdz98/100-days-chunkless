package dev.sasukector.hundreddayschunkless.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import dev.sasukector.hundreddayschunkless.HundredDaysChunkLess;
import dev.sasukector.hundreddayschunkless.helpers.ServerUtilities;
import dev.sasukector.hundreddayschunkless.helpers.TCT.BukkitTCT;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ChunksController {

    private static ChunksController instance = null;
    private final @Getter List<int[]> deletedChunks;
    private static final Random random = new Random();

    public static ChunksController getInstance() {
        if (instance == null) {
            instance = new ChunksController();
        }
        return instance;
    }

    public ChunksController() {
        this.deletedChunks = new ArrayList<>();
    }

    public boolean notDeletedChunk(int x, int z) {
        boolean already = false;
        for (int[] coords : this.deletedChunks) {
            if (coords[0] == x && coords[1] == z) {
                already = true;
                break;
            }
        }
        return !already;
    }

    public void addChunkToDeletedOnes(Chunk chunk) {
        if (this.notDeletedChunk(chunk.getX(), chunk.getZ())) {
            this.deletedChunks.add(new int[]{ chunk.getX(), chunk.getZ() });
        }
    }

    private List<int[]> convertJSONArrayToList(JsonArray deletedChunks) {
        List<int[]> deletedList = new ArrayList<>();
        deletedChunks.forEach(jsonElement -> {
            String[] preCoords = jsonElement.getAsString().split(",");
            if (preCoords.length == 2) {
                deletedList.add(new int[]{ Integer.parseInt(preCoords[0]), Integer.parseInt(preCoords[1]) });
            } else {
                Bukkit.getLogger().info("Error while loading chunk with info: " + Arrays.toString(preCoords));
            }
        });
        return deletedList;
    }

    private JsonArray convertListToJsonArray(List<int[]> deletedList) {
        JsonArray jsonArray = new JsonArray();
        for (int[] coords : deletedList) {
            jsonArray.add(coords[0] + "," + coords[1]);
        }
        return jsonArray;
    }

    private JsonArray getDeletedChunksJSONArray() {
        JsonArray deletedChunksArray = new JsonArray();
        File configFile = new File(HundredDaysChunkLess.getInstance().getDataFolder(), "chunks.json");
        if (!configFile.exists()) {
            HundredDaysChunkLess.getInstance().saveResource(configFile.getName(), false);
        }
        try {
            String baseJson = Files.readString(configFile.toPath());
            if (baseJson != null && !baseJson.isEmpty()) {
                deletedChunksArray = new Gson().fromJson(baseJson, JsonArray.class);
            }
        } catch (Exception e) {
            Bukkit.getLogger().info(ChatColor.RED + "Error while getting JSON file for chunks: " + e);
            e.printStackTrace();
        }
        return deletedChunksArray;
    }

    public void loadDeletedChunksFromFile() {
        List<int[]> loadedDeletedChunks = this.convertJSONArrayToList(this.getDeletedChunksJSONArray());
        this.deletedChunks.clear();
        this.deletedChunks.addAll(loadedDeletedChunks);
    }

    public void saveDeletedChunksToFile() {
        File configFile = new File(HundredDaysChunkLess.getInstance().getDataFolder(), "chunks.json");
        if (!configFile.exists()) {
            HundredDaysChunkLess.getInstance().saveResource(configFile.getName(), false);
        }
        try {
            FileWriter fileWriter = new FileWriter(configFile, false);
            fileWriter.write(convertListToJsonArray(this.deletedChunks).toString());
            fileWriter.close();
        } catch (Exception e) {
            Bukkit.getLogger().info(ChatColor.RED + "Error while writing JSON file for chunks: " + e);
            e.printStackTrace();
        }
    }

    public void deleteChunks() {
        World overworld = ServerUtilities.getOverworld();
        long lastDay = GameController.getInstance().getLastDay();
        double currentProb = lastDay / 100.0;
        if (overworld != null) {
            ServerUtilities.sendBroadcastMessage(ServerUtilities.getMiniMessage().parse(
                    "<color:#2E6F95>Nuevo día, borrando chunks con</color> <bold><color:#0091AD>" +
                            lastDay + "%</color></bold>"
            ));
            List<int[]> chunkPorBorrar = new ArrayList<>();
            for (int i = -65; i <= 65; ++i) {
                for (int j = -65; j <= 65; ++j) {
                    if (this.notDeletedChunk(i, j) && random.nextDouble() <= currentProb) {
                        chunkPorBorrar.add(new int[]{ i,j });
                    }
                }
            }
            BukkitTCT task = new BukkitTCT();
            Collections.shuffle(chunkPorBorrar);
            chunkPorBorrar.forEach(coords -> {
                AtomicInteger chunkX = new AtomicInteger(coords[0]);
                AtomicInteger chunkZ = new AtomicInteger(coords[1]);
                task.addWithDelay(() -> deleteChunk(overworld, chunkX.get(), chunkZ.get()), 500);
            });
            ServerUtilities.sendBroadcastMessage(ServerUtilities.getMiniMessage().parse(
                    "<color:#2E6F95>Se van a borrar</color> <bold><color:#0091AD>" +
                            chunkPorBorrar.size() + " chunks</color></bold>"
            ));
            task.execute();
        }
    }

    public void deleteChunk(World world, int chunkX, int chunkZ) {
        Bukkit.getScheduler().runTask(HundredDaysChunkLess.getInstance(), () -> {
            Chunk chunk = world.getChunkAt(chunkX, chunkZ);
            if (chunk.load()) {
                if (this.notDeletedChunk(chunkX, chunkZ)) {
                    for (int y = 0; y < 128; ++y) {
                        for (int x = 0; x < 16; ++x) {
                            for (int z = 0; z < 16; ++z) {
                                Block block = chunk.getBlock(x, y, z);
                                if (block.getType() != Material.END_PORTAL_FRAME &&
                                        block.getType() != Material.END_PORTAL) {
                                    block.setType(Material.AIR);
                                }
                            }
                        }
                    }
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendActionBar(ServerUtilities.getMiniMessage().parse(
                            "Se ha borrado el chunk <color:#2E6F95>[" + chunk.getX() + ", " + chunk.getZ() + "]</color>"
                    )));
                    Bukkit.getLogger().info("Se ha borrado el chunk [" + chunk.getX() + ", " + chunk.getZ() + "]");
                    addChunkToDeletedOnes(chunk);
                }
            } else {
                Bukkit.getLogger().info("No se pudo cargar el chunk en " + chunk.getX() + ", " + chunk.getZ());
            }
        });
    }

}
