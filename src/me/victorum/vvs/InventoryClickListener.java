package me.victorum.vvs;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.victorum.data.PlayerData;

public class InventoryClickListener implements Listener {
    private final VVS pl;
    private final Inventory mainMenu;
    private final HashMap<SubCategoryType, SubcategoryInventory> subcategories;
    private final HashMap<Inventory, ShopItem> shopItemMap = new HashMap<>();

    public InventoryClickListener(VVS pl) {
	this.pl = pl;
	subcategories = new HashMap<>();
	mainMenu = Bukkit.createInventory(null, 3 * 9);
	mainMenu.setItem(0, new ItemStackBuilder(Material.COBBLESTONE).fakeEnchant()
		.setDisplayName("§eBasen rakentamiseen").build());

	ShopItem[] itemsForBaseBuilding = new ShopItem[3 * 9];
	itemsForBaseBuilding[0] = new ShopItem(Material.COBBLESTONE, 5);
	itemsForBaseBuilding[1] = new ShopItem(Material.OBSIDIAN, 5);
	itemsForBaseBuilding[2] = new ShopItem(Material.SAND, 5);
	itemsForBaseBuilding[3] = new ShopItem(Material.GRAVEL, 5);

	subcategories.put(SubCategoryType.BASE_BUILDING,
		new SubcategoryInventory("§eBasen rakentamiseen", itemsForBaseBuilding.length, itemsForBaseBuilding));
    }

    @EventHandler
    public void mainMenuClicked(InventoryClickEvent e) {
	Inventory openedInventory = e.getInventory();
	Player p = (Player) e.getWhoClicked();
	ItemStack item = e.getCurrentItem();

	// Handle main menu routing to submenus
	if (openedInventory.equals(mainMenu)) {
	    e.setCancelled(true);
	    if (item == null)
		return;

	    switch (item.getType()) {

	    // Subcategories will be listed here
	    case COBBLESTONE:
		p.openInventory(subcategories.get(SubCategoryType.BASE_BUILDING).getInventory());
		break;
	    default:
		break;
	    }
	}
    }

    @EventHandler
    public void subMenuClicked(InventoryClickEvent e) {
	Player p = (Player) e.getWhoClicked();
	int slot = e.getSlot();
	// Test if one of the submenus
	for (SubcategoryInventory subCategoryInventory : subcategories.values()) {
	    if (subCategoryInventory.getInventory().getName().equals(e.getClickedInventory().getName())
		    && subCategoryInventory.getInventory().getSize() == e.getClickedInventory().getSize()) {
		ShopItem shopItem = subCategoryInventory.getItems()[slot];
		Inventory amountSelectionInv = Bukkit.createInventory(null, 9, "§eValitse määrä");
		ItemStack buy1 = new ItemStackBuilder(shopItem.clone()).setAmount(1)
			.setLore("§eOsta 1 hintaan $" + shopItem.getBuyPrice(1),
				"§eMyy 1 hintaan $" + shopItem.getSellPrice(1))
			.build();
		ItemStack buy4 = new ItemStackBuilder(shopItem.clone()).setAmount(4)
			.setLore("§eOsta 4 hintaan $" + shopItem.getBuyPrice(4),
				"§eMyy 4 hintaan $" + shopItem.getSellPrice(4))
			.build();
		ItemStack buy16 = new ItemStackBuilder(shopItem.clone()).setAmount(16)
			.setLore("§eOsta 16 hintaan $" + shopItem.getBuyPrice(16),
				"§eMyy 16 hintaan $" + shopItem.getSellPrice(16))
			.build();
		ItemStack buy64 = new ItemStackBuilder(shopItem.clone()).setAmount(64)
			.setLore("§eOsta 64 hintaan $" + shopItem.getBuyPrice(64),
				"§eMyy 64 hintaan $" + shopItem.getSellPrice(64))
			.build();
		// ItemStack buy256 = new
		// ItemStackBuilder(shopItem.clone()).setAmount(64).fakeEnchant().setLore(
		// "§eOsta 256 hintaan $" + shopItem.getBuyPrice(256), "§eMyy 256 hintaan $" +
		// shopItem
		// .getSellPrice(256)).build();

		amountSelectionInv.setItem(1, buy1);
		amountSelectionInv.setItem(3, buy4);
		amountSelectionInv.setItem(5, buy16);
		amountSelectionInv.setItem(7, buy64);
		// amountSelectionInv.setItem(-69, buy256);

		p.openInventory(amountSelectionInv);
		shopItemMap.put(amountSelectionInv, shopItem);
		e.setCancelled(true);
		return;
	    }
	}

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
	Player p = (Player) e.getWhoClicked();
	int slot = e.getSlot();

	// Test for amount selection inventory
	if (!shopItemMap.containsKey(e.getClickedInventory()))
	    return;
	e.setCancelled(true);

	Inventory inv = e.getClickedInventory();
	int amount = 0;

	if (slot == 1)
	    amount = 1;
	if (slot == 3)
	    amount = 4;
	if (slot == 5)
	    amount = 16;
	if (slot == 7)
	    amount = 64;
	// if (slot == 8)
	// amount = 256;
	// For now this plugin can only support multiplies of 64

	if (amount == 0)
	    return;

	if (e.getClick() == ClickType.LEFT)
	    buyItem(p, shopItemMap.get(inv), amount);
	if (e.getClick() == ClickType.RIGHT)
	    sellItem(p, shopItemMap.get(inv), amount);

    }

    /**
     * When the player closes the buy menu, delete the inventory
     */
    @EventHandler
    public void closeInventory(InventoryCloseEvent e) {
	shopItemMap.remove(e.getInventory());
    }

    /**
     * Handles custom items, like possible armors or kits
     */
    private void buyItem(Player p, ShopItem shopItem, int amount) {
	PlayerData pd = pl.getVictorum().getPlayerDataHandler().getPlayerData(p.getUniqueId());

	// Test for balance
	if (pd.getBalance() < shopItem.getBuyPrice(amount)) {
	    p.sendMessage("§eSinulla ei ole tarpeeksi rahaa.");
	    return;
	}

	// Take their money! Take it! Take all of it!!
	pd.subtractBalance(shopItem.getBuyPrice(amount));

	// Give or drop the items
	ItemStack bought = shopItem.clone();
	bought.setAmount(amount);

	switch (shopItem.getType()) {
	default:
	    // Full inv
	    HashMap<Integer, ItemStack> didntFit = p.getInventory().addItem(bought);

	    if (didntFit.size() > 0) {
		for (Entry<Integer, ItemStack> entry : didntFit.entrySet()) {
		    p.getWorld().dropItemNaturally(p.getLocation(), entry.getValue());
		}
		p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1);
	    }
	}
    }

    private void sellItem(Player p, ShopItem shopItem, int originalSellAmount) {
	PlayerData pd = pl.getVictorum().getPlayerDataHandler().getPlayerData(p.getUniqueId());

	switch (shopItem.getType()) {
	default:
	    HashMap<Integer, ? extends ItemStack> sellableItems = p.getInventory().all(shopItem.getType());
	    int itemsInInventory = 0;
	    for (ItemStack itemStack : sellableItems.values()) {
		itemsInInventory += itemStack.getAmount();
	    }
	    originalSellAmount = Math.min(originalSellAmount, itemsInInventory);
	    int sellAmount = originalSellAmount;

	    // Remove items
	    for (Entry<Integer, ? extends ItemStack> e : sellableItems.entrySet()) {
		ItemStack item = e.getValue();
		if (item.getAmount() > sellAmount) {
		    item.setAmount(item.getAmount() - sellAmount);
		    break;
		} else if (item.getAmount() == sellAmount) {
		    sellAmount = 0;
		    item.setType(Material.AIR);
		    break;
		} else {
		    sellAmount -= item.getAmount();
		    item.setAmount(0);
		}
	    }
	    // Add money
	    long price = shopItem.getSellPrice(originalSellAmount);
	    pd.addBalance(price);
	    p.sendMessage(
		    "§eMyyty " + originalSellAmount + " " + shopItem.getType().name() + " hintaan $" + price + ".");
	}
    }

    public Inventory getMainMenu() {
	return this.mainMenu;
    }

}
