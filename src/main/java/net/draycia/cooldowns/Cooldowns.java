package net.draycia.cooldowns;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public final class Cooldowns extends JavaPlugin {

    private CooldownManager cooldownManager = new CooldownManager(this);

    private File dataFile = new File(getDataFolder(), "data.json");

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Type type = new TypeToken<HashMap<UUID, HashMap<String, AtomicLong>>>() {}.getType();

    @Override
    public void onEnable() {
        loadCooldowns();
        getServer().getServicesManager().register(CooldownManager.class, cooldownManager, this, ServicePriority.High);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, cooldownManager::tick, 1, 1);
    }

    @Override
    public void onDisable() {
        saveCooldowns();
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    private void loadCooldowns() {
        if (!dataFile.getParentFile().exists()) {
            dataFile.getParentFile().mkdirs();
        }

        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        try (JsonReader reader = gson.newJsonReader(new FileReader(dataFile))) {
            HashMap<UUID, HashMap<String, AtomicLong>> json = gson.fromJson(reader, type);

            if (json != null) {
                cooldownManager.setCooldowns(json);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO: Remove expired cooldowns on load
    }

    private void saveCooldowns() {
        try (JsonWriter writer = gson.newJsonWriter(new FileWriter(dataFile))) {
            gson.toJson(cooldownManager.getCooldowns(), type, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
