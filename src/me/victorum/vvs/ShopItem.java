package me.victorum.vvs;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ShopItem extends ItemStack {

    private final long singleBuyPrice, singleSellPrice;

    public ShopItem(ItemStack item, long price) {
	super(item);
	this.singleBuyPrice = price;
	this.singleSellPrice = 0;
    }

    public ShopItem(ItemStack item, long buyPrice, long sellPrice) {
	super(item);
	this.singleBuyPrice = buyPrice;
	this.singleSellPrice = sellPrice;
    }

    public ShopItem(Material mat, long price) {
	super(mat);
	this.singleBuyPrice = price;
	this.singleSellPrice = price;
    }

    public long getBuyPrice(int amount) {
	return singleBuyPrice * amount;
    }

    public long getSellPrice(int amount) {
	return singleSellPrice * amount;
    }
}
