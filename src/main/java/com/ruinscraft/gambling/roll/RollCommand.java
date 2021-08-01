package com.ruinscraft.gambling.roll;

import com.ruinscraft.gambling.GamblingPlugin;
import com.ruinscraft.gambling.RandomNumbers;
import com.ruinscraft.gambling.VaultUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
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
                case 1:
                    return "Doubles!";
                case 2:
                    return "Oh baby a triple!";
                case 3:
                    return "CAN YOU BELIEVE IT? QUADS!";
                case 4:
                    return "What a quintuple!";
                case 5:
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

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Rolling the dice..."));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1f, 1f);

            gamblingPlugin.getServer().getScheduler().runTaskLater(gamblingPlugin, () -> {
                if (player.isOnline()) {
                    VaultUtil.withdraw(player, bet);

                    Roll roll = new Roll();

                    // make the rolls more jewish
                    if (roll.isWinner() && RandomNumbers.chance(8)) {
                        roll = new Roll();
                    }

                    String message = ChatColor.LIGHT_PURPLE + player.getName() + " bet " + ChatColor.GREEN + bet + ChatColor.LIGHT_PURPLE + " " + VaultUtil.getCurrencyNamePlural() + " and rolled " + ChatColor.GREEN + roll.getNumbers();

                    if (roll.isWinner()) {
                        int winAmount = bet * 10;
                        message += ChatColor.LIGHT_PURPLE + " and won " + ChatColor.GREEN + winAmount + ChatColor.LIGHT_PURPLE + " " + VaultUtil.getCurrencyNamePlural() + "! " + roll.getMessage();
                        VaultUtil.deposit(player, winAmount);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You won " + winAmount + "!"));
                        // play a nice sound if winner
                        gamblingPlugin.getServer().getScheduler().runTaskLater(gamblingPlugin, () -> {
                            if (player.isOnline()) {
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1f, 1.3f);
                            }

                            gamblingPlugin.getServer().getScheduler().runTaskLater(gamblingPlugin, () -> {
                                if (player.isOnline()) {
                                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1f, 1.5f);
                                }
                            }, 10L);
                        }, 5L);
                    }

                    Bukkit.broadcastMessage(message);
                }
            }, 40L);
        }

        return true;
    }

}
