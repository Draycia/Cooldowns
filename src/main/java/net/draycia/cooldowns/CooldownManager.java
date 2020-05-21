package net.draycia.cooldowns;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CooldownManager {

    private HashMap<UUID, HashMap<String, Long>> cooldowns = new HashMap<>();

    HashMap<UUID, HashMap<String, Long>> getCooldowns() {
        return cooldowns;
    }

    void setCooldowns(HashMap<UUID, HashMap<String, Long>> cooldowns) {
        this.cooldowns = cooldowns;
    }

    public boolean isOnCooldown(UUID uuid, String id) {
        return System.currentTimeMillis() < getUserCooldown(uuid, id);
    }

    public long getUserCooldown(UUID uuid, String id) {
        HashMap<String, Long> userCooldowns =
                cooldowns
                    .get(
                        uuid);

        if (userCooldowns == null) {
            return 0;
        }

        if (userCooldowns.containsKey(id)) {
            long cooldown = userCooldowns.get(id);

            if (cooldown <= 0) {
                userCooldowns.remove(id);
                return 0;
            }

            return cooldown;
        }

        return 0;
    }

    public void setUserCooldown(UUID player, String id, TimeUnit timeUnit, long cooldown) {
        long cooldownEnd = System.currentTimeMillis() + timeUnit.toMillis(cooldown);

        HashMap<String, Long> userCooldowns = cooldowns.get(player);

        if (userCooldowns == null) {
            HashMap<String, Long> newCooldowns = new HashMap<>();

            cooldowns.put(player, newCooldowns);
            userCooldowns = newCooldowns;
        }

        userCooldowns.put(id, cooldownEnd);
    }

}
