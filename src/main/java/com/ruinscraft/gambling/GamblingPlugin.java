package com.ruinscraft.gambling;

import com.ruinscraft.gambling.coinflip.CoinflipAcceptCommand;
import com.ruinscraft.gambling.coinflip.CoinflipCommand;
import com.ruinscraft.gambling.coinflip.CoinflipManager;
import com.ruinscraft.gambling.roll.RollCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class GamblingPlugin extends JavaPlugin {

    private CooldownManager cooldownManager;
    private CoinflipManager coinflipManager;
    private boolean enableCoinflips;
    private boolean enableRolling;
    private boolean enableSlotMachines;

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public CoinflipManager getCoinflipManager() {
        return coinflipManager;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (!VaultUtil.init(this)) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        cooldownManager = new CooldownManager();
        getServer().getPluginManager().registerEvents(cooldownManager, this);

        enableCoinflips = getConfig().getBoolean("enable-coinflips");
        enableRolling = getConfig().getBoolean("enable-rolling");
        enableSlotMachines = getConfig().getBoolean("enable-slotmachines");

        if (enableCoinflips) {
            coinflipManager = new CoinflipManager(this);
            getCommand("coinflip").setExecutor(new CoinflipCommand(this));
            getCommand("coinflipaccept").setExecutor(new CoinflipAcceptCommand(coinflipManager));
        }

        if (enableRolling) {
            getCommand("roll").setExecutor(new RollCommand(this));
        }

        getCommand("balance").setExecutor(new BalanceCommand(this));
    }

    @Override
    public void onDisable() {

    }

}
