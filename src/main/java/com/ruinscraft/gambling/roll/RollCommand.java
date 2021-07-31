package com.ruinscraft.gambling.roll;

import com.ruinscraft.gambling.GamblingPlugin;
import com.ruinscraft.gambling.RandomNumbers;
import com.ruinscraft.gambling.VaultUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RollCommand implements CommandExecutor {

    private class Roll {

        private int[] numbers;

        public Roll() {
            numbers = new int[6];

            for (int i = 0; i < 6; i++) {
                numbers[i] = RandomNumbers.getRandomIntInRange(1, 9);
            }
        }

        public String getNumbers() {
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < 6; i++) {
                stringBuilder.append(numbers[i]);
            }

            return stringBuilder.toString();
        }

        // 1 in 10 chance
        public boolean isWinner() {
            return numbers[4] == numbers[5];
        }

        public String getMessage() {
            int matches = 0;

            for (int i = 5; i > 0; i--) {
                if (numbers[i] == numbers[i - 1]) {
                    matches++;
                }
            }

            switch (matches) {
                case 2:
                    return "Doubles!";
                case 3:
                    return "Oh baby a triple!";
                case 4:
                    return "CAN YOU BELIEVE IT? QUADS!";
                case 5:
                    return "What a quintuple!";
                case 6:
                    return "Sextuple.";
                default:
                    return "";
            }
        }

    }

    private GamblingPlugin gamblingPlugin;

    public RollCommand(GamblingPlugin gamblingPlugin) {
        this.gamblingPlugin = gamblingPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (!gamblingPlugin.getCooldownManager().isOnCooldown(player)) {
            final int bet;

            if (args.length < 1) {
                bet = 0;
            } else {
                try {
                    bet = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Invalid bet amount.");
                    return false;
                }
            }

            if (!VaultUtil.has(player, bet)) {
                player.sendMessage(ChatColor.RED + "You do not have enough to make this bet.");
                return false;
            }

            VaultUtil.withdraw(player, bet);

            Roll roll = new Roll();

            String message = ChatColor.LIGHT_PURPLE + player.getName() + " bet " + ChatColor.GREEN + bet + ChatColor.LIGHT_PURPLE + " " + VaultUtil.getCurrencyNamePlural() + " and rolled " + ChatColor.GREEN + roll.getNumbers();

            if (roll.isWinner()) {
                int winAmount = bet * 10;
                message += ChatColor.LIGHT_PURPLE + " and won " + ChatColor.GREEN + winAmount + ChatColor.LIGHT_PURPLE + " " + VaultUtil.getCurrencyNamePlural() + "! " + roll.getMessage();
                VaultUtil.deposit(player, winAmount);
            }

            Bukkit.broadcastMessage(message);
        }

        return true;
    }

}
