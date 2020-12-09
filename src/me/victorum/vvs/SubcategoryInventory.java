package me.victorum.vvs;

import java.text.NumberFormat;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class SubcategoryInventory {
	private final Inventory inv;
	private final ShopItem[] shopItems;

	public SubcategoryInventory(String name, int size, ShopItem[] shopItems) {
		this.inv = Bukkit.createInventory(null, size, name);
		this.shopItems = shopItems;
	}

	public Inventory getInventory() {
		Inventory shopInventory = Bukkit.createInventory(null, shopItems.length, this.inv.getName());
		ShopItem[] items = new ShopItem[shopItems.length];

		for (int i = 0; i < shopItems.length; i++) {
			ShopItem item = shopItems[i];
			if (item != null) {
				items[i] = addPrices(item, item.getBuyPrice(1), item.getSellPrice(1));
			}
		}
		shopInventory.setContents(items);
		return shopInventory;
	}

	private ShopItem addPrices(ShopItem item, long buyOne, long sellOne) {
		NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
		format.setMaximumFractionDigits(0);
		ItemStackBuilder itemStackBuilder = new ItemStackBuilder(item.getType());
		itemStackBuilder.setDisplayName("§e" + String.valueOf(item.getType().name().charAt(0)).toUpperCase() + item
				.getType().name().toLowerCase().substring(1).replace("_", " "));

		itemStackBuilder.setLore("§eOstohinta: " + format.format(buyOne) + "/kpl", "§eMyyntihinta: " + format.format(
				sellOne) + "/kpl");
		return new ShopItem(itemStackBuilder.build(), buyOne, sellOne);
	}

	public ShopItem[] getItems() {
		return shopItems;
	}

}
