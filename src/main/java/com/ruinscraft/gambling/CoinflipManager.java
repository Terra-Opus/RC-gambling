package com.ruinscraft.gambling;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CoinflipManager {

    public class Coinflip {
        private Player initiator;
        private Player participant;
        private int betAmount;
        private int coinWorth;

        public Coinflip(Player initiator, Player participant, int betAmount) {
            this.initiator = initiator;
            this.participant = participant;
            this.betAmount = betAmount;
            this.coinWorth = betAmount * 2;
        }

        public Player getInitiator() {
            return initiator;
        }

        public Player getParticipant() {
            return participant;
        }

        public int getBetAmount() {
            return betAmount;
        }

        public int getCoinWorth() {
            return coinWorth;
        }

        public Player decideWinner() {
            if (RandomNumbers.getRandomBoolean()) {
                return participant;
            } else {
                return initiator;
            }
        }
    }

    private GamblingPlugin gamblingPlugin;
    private Map<Player, Coinflip> activeCoinFlips;

    public CoinflipManager(GamblingPlugin gamblingPlugin) {
        this.gamblingPlugin = gamblingPlugin;
        activeCoinFlips = new HashMap<>();
    }

    public void createCoinflip(Player initiator, Player participant, int amount) {
        Coinflip coinflip = new Coinflip(initiator, participant, amount);
        activeCoinFlips.put(initiator, coinflip);

        gamblingPlugin.getServer().getScheduler().runTaskLater(gamblingPlugin, () -> {
            if (activeCoinFlips.containsKey(initiator)) {
                if (initiator.isOnline()) {
                    initiator.sendMessage(ChatColor.GOLD + "Your coinflip has expired.");
                }

                activeCoinFlips.remove(initiator);
            }
        }, 20 * 20L);
    }

    public boolean hasActiveCoinflip(Player player) {
        return activeCoinFlips.containsKey(player);
    }

    public Coinflip acceptCoinflip(Player player, int amount) {
        for (Coinflip coinflip : activeCoinFlips.values()) {
            if (coinflip.getParticipant().equals(player)) {
                if (coinflip.getBetAmount() == amount) {
                    activeCoinFlips.remove(coinflip.getInitiator());
                    return coinflip;
                }
            }
        }

        return null;
    }

}
