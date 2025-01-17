package com.projectkorra.projectkorra.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.util.MultiAbilityManager;
import com.projectkorra.projectkorra.configuration.ConfigManager;

/**
 * Executor for /bending clear. Extends {@link PKCommand}.
 */
public class ClearCommand extends PKCommand {

	private final String cantEditBinds;
	private final String cleared;
	private final String wrongNumber;
	private final String clearedSlot;
	private final String alreadyEmpty;

	public ClearCommand() {
		super("clear", "/bending clear [Slot | Player] [Player]", ConfigManager.languageConfig.get().getString("Commands.Clear.Description"), new String[] { "clear", "cl", "c" });

		this.cantEditBinds = ConfigManager.languageConfig.get().getString("Commands.Clear.CantEditBinds");
		this.cleared = ConfigManager.languageConfig.get().getString("Commands.Clear.Cleared");
		this.wrongNumber = ConfigManager.languageConfig.get().getString("Commands.Clear.WrongNumber");
		this.clearedSlot = ConfigManager.languageConfig.get().getString("Commands.Clear.ClearedSlot");
		this.alreadyEmpty = ConfigManager.languageConfig.get().getString("Commands.Clear.AlreadyEmpty");
	}

	@Override
	public void execute(final CommandSender sender, final List<String> args) {
		if (!this.hasPermission(sender) || !this.correctLength(sender, args.size(), 0, 2)) {
			return;
		} else if (isPlayer(sender) && MultiAbilityManager.hasMultiAbilityBound((Player) sender)) {
			GeneralMethods.sendBrandingMessage(sender, ChatColor.RED + this.cantEditBinds);
			return;
		}

		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(sender.getName());
		if (bPlayer == null && isPlayer(sender)) {
			GeneralMethods.createBendingPlayer(((Player) sender).getUniqueId(), sender.getName());
			bPlayer = BendingPlayer.getBendingPlayer(sender.getName());
		}

		if (args.size() == 0) {
			if (!this.isPlayer(sender)) return;
			bPlayer.getAbilities().clear();
			for (int i = 1; i <= 9; i++) {
				GeneralMethods.saveAbility(bPlayer, i, null);
			}
			GeneralMethods.sendBrandingMessage(sender, ChatColor.YELLOW + this.cleared);
		} else {
			try {
				final int slot = Integer.parseInt(args.get(0));
				if (slot < 1 || slot > 9) {
					GeneralMethods.sendBrandingMessage(sender, ChatColor.RED + this.wrongNumber);
					return;
				}
				if (args.size() == 1) {
					if (!isPlayer(sender)) return;
					if (bPlayer.getAbilities().get(slot) != null) {
						bPlayer.getAbilities().remove(slot);
						GeneralMethods.saveAbility(bPlayer, slot, null);
						GeneralMethods.sendBrandingMessage(sender, ChatColor.YELLOW + this.clearedSlot.replace("{slot}", String.valueOf(slot)));
					} else {
						GeneralMethods.sendBrandingMessage(sender, ChatColor.YELLOW + this.alreadyEmpty);
					}
				} else if (args.size() == 2) {
					final String playerName = args.get(1);
					final Player player = Bukkit.getPlayer(playerName);
					if (player == null) {
						GeneralMethods.sendBrandingMessage(player, ChatColor.RED + this.wrongNumber);
						return;
					}
					bPlayer = BendingPlayer.getBendingPlayer(player.getName());
					if (bPlayer == null) {
						GeneralMethods.createBendingPlayer(player.getUniqueId(), player.getName());
						bPlayer = BendingPlayer.getBendingPlayer(player.getName());
					}
					if (bPlayer.getAbilities().get(slot) != null) {
						bPlayer.getAbilities().remove(slot);
						GeneralMethods.saveAbility(bPlayer, slot, null);
						GeneralMethods.sendBrandingMessage(player, ChatColor.YELLOW + this.clearedSlot.replace("{slot}", String.valueOf(slot)));
					} else {
						GeneralMethods.sendBrandingMessage(player, ChatColor.YELLOW + this.alreadyEmpty);
					}
				}
			} catch (final NumberFormatException e) {
				final String playerName = args.get(0);
				final Player player = Bukkit.getPlayer(playerName);
				if (player == null) {
					GeneralMethods.sendBrandingMessage(sender, ChatColor.RED + this.wrongNumber);
					return;
				}
				bPlayer = BendingPlayer.getBendingPlayer(player.getName());
				if (bPlayer == null) {
					GeneralMethods.createBendingPlayer(player.getUniqueId(), player.getName());
					bPlayer = BendingPlayer.getBendingPlayer(player.getName());
				}
				bPlayer.getAbilities().clear();
				for (int i = 1; i <= 9; i++) {
					GeneralMethods.saveAbility(bPlayer, i, null);
				}
			}
		}
	}

	@Override
	protected List<String> getTabCompletion(final CommandSender sender, final List<String> args) {
		if (args.size() >= 1 || !sender.hasPermission("bending.command.clear")) {
			return new ArrayList<String>();
		}
		return Arrays.asList("123456789".split(""));
	}

}
