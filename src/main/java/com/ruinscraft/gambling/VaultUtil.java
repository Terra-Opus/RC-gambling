package com.ruinscraft.gambling;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class VaultUtil {

    private static Economy economy;

    public static boolean init(JavaPlugin javaPlugin) {
        if (javaPlugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = javaPlugin.getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {
            return false;
        }

        economy = rsp.getProvider();

        return economy != null;
    }

    public static boolean has(Player player, int amount) {
        return economy.has(player, amount);
    }

    public static int getBalance(Player player) {
        return (int) economy.getBalance(player);
    }

    public static boolean withdraw(Player player, int amount) {
        if (economy.has(player, amount)) {
            economy.withdrawPlayer(player, amount);
            return true;
        } else {
            return false;
        }
    }

    public static void deposit(Player player, int amount) {
        economy.depositPlayer(player, amount);
    }

    public static String getCurrencyNamePlural() {
        return economy.currencyNamePlural();
    }

}
