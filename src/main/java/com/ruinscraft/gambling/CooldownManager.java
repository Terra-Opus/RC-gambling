package com.ruinscraft.gambling;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CooldownManager implements Listener {

    private static final long COOLDOWN_MILLIS = TimeUnit.SECONDS.toMillis(45);

    private Map<Player, Long> recentUsages;

    public CooldownManager() {
        recentUsages = new HashMap<>();
    }

    public boolean isOnCooldown(Player player) {
        long currentTime = System.currentTimeMillis();
        if (recentUsages.containsKey(player)) {
            long lastUseTime = recentUsages.get(player);
            if (lastUseTime + COOLDOWN_MILLIS > currentTime) return true;
        }
        recentUsages.put(player, currentTime);
        return false;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        recentUsages.remove(event.getPlayer());
    }

}
