package me.victorum.vvs;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ShopItemBuilder extends ItemStackBuilder {

    private long buyPrice, sellPrice;

    public ShopItemBuilder(Material mat, long price) {
	super(mat, 1);
	this.buyPrice = price;
    }

    @Override
    public ShopItem build() {
	ItemStack item = super.build();
	return new ShopItem(item, buyPrice, sellPrice);
    }

    public ShopItemBuilder setBuyPrice(long price) {
	this.buyPrice = price;
	return this;
    }

    public ShopItemBuilder setSellPrice(long price) {
	this.sellPrice = price;
	return this;
    }

}
