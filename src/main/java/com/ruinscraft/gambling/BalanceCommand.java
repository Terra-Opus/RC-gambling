package com.ruinscraft.gambling;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {

    private GamblingPlugin gamblingPlugin;

    public BalanceCommand(GamblingPlugin gamblingPlugin) {
        this.gamblingPlugin = gamblingPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (!gamblingPlugin.getCooldownManager().isOnCooldown(player)) {
            int balance = VaultUtil.getBalance(player);
            Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + " has " + ChatColor.GREEN + balance + ChatColor.GOLD + " gold ingots on them.");
        }

        return true;
    }

}
