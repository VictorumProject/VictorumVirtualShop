package me.victorum.vvs;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import me.victorum.victorum.Victorum;

public class VVS extends JavaPlugin {
    private InventoryClickListener clickListener;

    @Override
    public void onEnable() {
	if (!Bukkit.getPluginManager().isPluginEnabled("Victorum")) {
	    this.setEnabled(false);
	    this.getLogger().warning("Can't enable plugin. Dependency Victorum not found.");
	    return;
	}

	getCommand("shop").setExecutor(new ShopCommand(this));

	this.clickListener = new InventoryClickListener(this);
	Bukkit.getPluginManager().registerEvents(clickListener, this);
    }

    public Inventory getMainMenu() {
	return clickListener.getMainMenu();
    }

    public Victorum getVictorum() {
	return (Victorum) Bukkit.getPluginManager().getPlugin("Victorum");
    }
}
