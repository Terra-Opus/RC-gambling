package com.ruinscraft.gambling;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CoinflipCommand implements CommandExecutor {

    private CoinflipManager coinflipManager;
    private Map<Player, Long> recentFlips;

    public CoinflipCommand(CoinflipManager coinflipManager) {
        this.coinflipManager = coinflipManager;
        this.recentFlips = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (coinflipManager.hasActiveCoinflip(player)) {
            player.sendMessage(ChatColor.RED + "You have an active coinflip. Wait for it to expire.");
            return false;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "/" + label + " <username> <amount>");
            return false;
        }

        if (recentFlips.containsKey(player)) {
            long lastBetTime = recentFlips.get(player);
            long currentTime = System.currentTimeMillis();

            if (lastBetTime + TimeUnit.SECONDS.toMillis(20) > currentTime) {
                player.sendMessage(ChatColor.RED + "Please wait before making another bet.");
                return false;
            }
        }

        Player target = null;

        try {
            target = Bukkit.matchPlayer(args[0]).get(0);
        } catch (Exception e) {
            // ignore
        }

        if (target == null || !target.isOnline()) {
            player.sendMessage(ChatColor.RED + args[0] + " is not online.");
            return false;
        }

        if (target.equals(player)) {
            player.sendMessage(ChatColor.RED + "You cannot coinflip yourself.");
            return false;
        }

        int amount;

        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid bet amount.");
            return false;
        }

        if (amount < 0) {
            player.sendMessage(ChatColor.RED + "You cannot bet a negative amount.");
            return false;
        }

        if (!VaultUtil.has(player, amount)) {
            player.sendMessage(ChatColor.RED + "You do not have enough to make this bet.");
            return false;
        }

        coinflipManager.createCoinflip(player, target, amount);

        target.sendMessage(ChatColor.GOLD + player.getName() + " wants to flip a coin for " + ChatColor.GREEN + amount + ChatColor.GOLD + ". Type '/coinflipaccept " + amount + "' to accept.");
        player.sendMessage(ChatColor.GOLD + "Sending coinflip request to " + target.getName() + "...");

        recentFlips.put(player, System.currentTimeMillis());

        return true;
    }

}
