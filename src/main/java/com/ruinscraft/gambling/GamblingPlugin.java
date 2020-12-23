package com.ruinscraft.gambling;

import org.bukkit.plugin.java.JavaPlugin;

public class GamblingPlugin extends JavaPlugin {

    private CoinflipManager coinflipManager;

    @Override
    public void onEnable() {
        if (!VaultUtil.init(this)) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        coinflipManager = new CoinflipManager(this);

//        getCommand("roll").setExecutor(new RollCommand());
        getCommand("coinflip").setExecutor(new CoinflipCommand(coinflipManager));
        getCommand("coinflipaccept").setExecutor(new CoinflipAcceptCommand(coinflipManager));
    }

    @Override
    public void onDisable() {

    }

}
