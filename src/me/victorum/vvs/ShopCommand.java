package me.victorum.vvs;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {
	private final VVS pl;

	public ShopCommand(VVS pl) {
		this.pl = pl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§eDerp");
			return true;
		}

		Player p = (Player) sender;
		p.openInventory(pl.getMainMenu());
		p.sendMessage("§ePaina oikeaa klikkiä myydäksesi ja vasenta ostaaksesi.");
		return true;
	}
}
