package net.draycia.cooldowns;

import net.draycia.cooldowns.events.CooldownEndEvent;
import net.draycia.cooldowns.events.CooldownStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class CooldownManager implements Listener {

    private JavaPlugin main;

    private HashMap<UUID, HashMap<String, MutableLong>> cooldowns = new HashMap<>();

    private CooldownManager() {}

    public CooldownManager(JavaPlugin main) {
        this.main = main;
    }

    void tick() {
        Iterator<Map.Entry<String, MutableLong>> iterator;
        Map.Entry<String, MutableLong> cooldown;

        for (Map.Entry<UUID, HashMap<String, MutableLong>> playerEntry : cooldowns.entrySet()) {
            Set<Map.Entry<String, MutableLong>> entrySet = playerEntry.getValue().entrySet();

            iterator = entrySet.iterator();

            while (iterator.hasNext()) {
                cooldown = iterator.next();

                double value = cooldown.getValue().decrementAndGet();

                if (value <= 0) {
                    CooldownEndEvent cooldownEndEvent = new CooldownEndEvent(playerEntry.getKey(), cooldown.getKey());
                    Bukkit.getPluginManager().callEvent(cooldownEndEvent);

                    iterator.remove();
                }
            }
        }
    }

    HashMap<UUID, HashMap<String, MutableLong>> getCooldowns() {
        return cooldowns;
    }

    void setCooldowns(HashMap<UUID, HashMap<String, MutableLong>> cooldowns) {
        this.cooldowns = cooldowns;
    }

    public boolean isOnCooldown(UUID uuid, String id) {
        return getUserCooldown(uuid, id) > 0;
    }

    public long getUserCooldown(UUID uuid, String id) {
        HashMap<String, MutableLong> userCooldowns = cooldowns.get(uuid);

        if (userCooldowns == null) {
            return 0;
        }

        if (userCooldowns.containsKey(id)) {
            long cooldown = userCooldowns.get(id).getValue();

            if (cooldown <= 0) {
                userCooldowns.remove(id);
                return 0;
            }

            return cooldown;
        }

        return 0;
    }

    public boolean setUserCooldown(UUID player, String id, TimeUnit timeUnit, long cooldown) {
        return setUserCooldown(player, id, timeUnit.toSeconds(cooldown) * 20);
    }

    public boolean setUserCooldown(final UUID player, final String id, TimeUnit timeUnit, long cooldown, BiConsumer<UUID, String> function) {
        return setUserCooldown(player, id, timeUnit.toSeconds(cooldown) * 20, function);
    }

    public boolean setUserCooldown(UUID player, String id, long cooldown, BiConsumer<UUID, String> function) {
        boolean success = setUserCooldown(player, id, cooldown);

        if (!success) {
            return false;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> function.accept(player, id), cooldown);

        return true;
    }

    public boolean setUserCooldown(UUID player, String id, long cooldown) {
        CooldownStartEvent cooldownStartEvent = new CooldownStartEvent(player, id);
        Bukkit.getPluginManager().callEvent(cooldownStartEvent);

        if (cooldownStartEvent.isCancelled()) {
            return false;
        }

        HashMap<String, MutableLong> userCooldowns = cooldowns.get(player);

        if (userCooldowns == null) {
            HashMap<String, MutableLong> newCooldowns = new HashMap<>();

            cooldowns.put(player, newCooldowns);
            userCooldowns = newCooldowns;
        }

        userCooldowns.put(id, new MutableLong(cooldown));

        return true;
    }

    // TODO: stop callbacks for cooldowns
    public void resetUserCooldowns(UUID player) {
        cooldowns.remove(player);
    }

    public void resetAllCooldowns() {
        cooldowns.clear();
    }

}
