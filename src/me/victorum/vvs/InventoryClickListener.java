package me.victorum.vvs;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.victorum.data.PlayerData;

public class InventoryClickListener implements Listener {
	private final VVS pl;
	private final Inventory mainMenu;
	private final HashMap<SubCategoryType, Inventory> shops;
	private final HashMap<SubCategoryType, ShopItem[]> items;

	public InventoryClickListener(VVS pl) {
		this.pl = pl;
		mainMenu = Bukkit.createInventory(null, 1 * 9);
		items = new HashMap<>(3);

		mainMenu.setItem(1, new ItemStackBuilder(Material.OBSIDIAN).setDisplayName("§7Basen rakennukseen").fakeEnchant()
				.build());
		ShopItem[] itemsForShop = new ShopItem[27];
		itemsForShop[10] = getShopItem(Material.COBBLESTONE, 64, 5);
		itemsForShop[13] = getShopItem(Material.OBSIDIAN, 64, 5);
		itemsForShop[16] = new ShopItem(new ItemStack(Material.STAINED_CLAY, 16, (short) 4), 5000);

		items.put(SubCategoryType.BASE_BUILDING, itemsForShop);
		items.put(SubCategoryType.RAIDING, new ShopItem[27]);
		items.put(SubCategoryType.GRINDING, new ShopItem[27]);

		mainMenu.setItem(4, new ItemStackBuilder(Material.TNT).setDisplayName("§7Basen tuhoamiseen").fakeEnchant()
				.build());
		mainMenu.setItem(7, new ItemStackBuilder(Material.SUGAR_CANE).setDisplayName("§7Basen rahoittamiseen")
				.fakeEnchant().build());

		shops = new HashMap<>(3);
		for (Entry<SubCategoryType, ShopItem[]> entry : items.entrySet()) {
			Inventory shop = Bukkit.createInventory(null, entry.getValue().length);
			shop.setContents(entry.getValue());
			shops.put(entry.getKey(), shop);
		}
	}

	private static ShopItem getShopItem(Material mat, int amount, long pricePerItem) {
		NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
		ItemStack item = new ItemStackBuilder(mat, amount).setDisplayName("§e" + String.valueOf(mat.name().charAt(0))
				.toUpperCase() + mat.name().toLowerCase().substring(1)).setLore("§eHinta: " + format.format(pricePerItem
						* amount) + "/kpl").build();
		return new ShopItem(item, pricePerItem);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		Player p = (Player) e.getWhoClicked();
		ItemStack item = e.getCurrentItem();

		// Test for shopinventory
		if (!inv.equals(mainMenu) && !shops.values().contains(inv))
			return;

		e.setCancelled(true);
		if (item == null || item.getType() == Material.AIR)
			return;

		if (!e.getClickedInventory().equals(e.getInventory()))
			return;

		if (inv.equals(mainMenu)) {
			switch (item.getType()) {
			case OBSIDIAN:
				p.openInventory(shops.get(SubCategoryType.BASE_BUILDING));
				break;
			case TNT:
				p.openInventory(shops.get(SubCategoryType.RAIDING));
				break;
			case SUGAR_CANE:
				p.openInventory(shops.get(SubCategoryType.GRINDING));
				break;
			default:
				break;
			}
		} else {
			if (!e.getClickedInventory().equals(e.getInventory())) {
				e.setCancelled(true);
				return;
			}
			ShopItem shopItem = null;
			for (Entry<SubCategoryType, Inventory> entry : shops.entrySet()) {
				if (entry.getValue().equals(inv)) {
					shopItem = items.get(entry.getKey())[e.getSlot()];
				}
			}

			// This is one of the subcategories
			long price = shopItem.getBuyPrice(item.getAmount());
			PlayerData pd = pl.getVictorum().getPlayerDataHandler().getPlayerData(p.getUniqueId());
			if (pd.getBalance() < price) {
				p.sendMessage("§eSinulla ei ole tarpeeksi rahaa. Tarvitset vielä $" + (price - pd.getBalance()) + ".");
				return;
			}
			buyItem(pd, shopItem);
			pd.subtractBalance(price);
			p.sendMessage("§eOstit §a" + shopItem.getAmount() + " " + shopItem.getType().name() + " §ehintaan §a$"
					+ price + "§e.");
		}
	}

	private void buyItem(PlayerData pd, ShopItem clickedItem) {
		switch (clickedItem.getType()) {
		default:
			giveItem(pd.getPlayer(), clickedItem.getType(), clickedItem.getAmount());
		}
	}

	private void giveItem(Player p, Material type, int amount) {
		ItemStack item = new ItemStack(type, amount);
		if (p.getInventory().firstEmpty() == -1) {
			// Full inv
			p.getWorld().dropItemNaturally(p.getLocation(), item);
		} else {
			p.getInventory().addItem(item);
			p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1);
		}
	}

	public Inventory getMainMenu() {
		return this.mainMenu;
	}

}
