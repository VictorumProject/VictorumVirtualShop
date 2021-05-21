package me.victorum.vvs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemConfigLoader {
    public ItemConfigLoader(VVS pl) {
	InputStream f = pl.getResource("items.conf");
	try {
	    BufferedReader in = new BufferedReader(new InputStreamReader(f));

	    // First collect all the categorynames
	    Set<Category> categories = new HashSet<>();
	    HashMap<String, Set<ShopItem>> items = new HashMap<>();

	    String line;
	    // int tabLevel = 0;
	    while ((line = in.readLine()) != null) {
		if (line.trim().isEmpty())
		    continue;
		if (line.startsWith("CATEGORIES")) {
		    // Read tabrised categorylines and break
		    while ((line = in.readLine()) != null) {
			if (!line.startsWith("\t"))
			    break;

			String[] split = line.split(":");
			int menuSlot = Integer.parseInt(split[0].trim());
			String name = split[1].trim();
			categories.add(new Category(menuSlot, name));
		    }
		}
		for (Category category : categories) {
		    System.out.println(category.name + ": " + category.mainMenuSlot);
		}
		break;
	    }

	    // Read items in categories
	    String currentCategory = null;
	    Set<ShopItem> currentItems = new HashSet<>();
	    while ((line = in.readLine()) != null) {
		if (line.trim().isEmpty())
		    continue;
		if (!line.startsWith("\t")) {
		    if (currentCategory != null)
			items.put(currentCategory, currentItems);
		    currentCategory = line;
		    currentItems = new HashSet<>();
		    System.out.println(line);
		    continue;
		}

		String[] split = line.trim().split(":");
		String[] priceSplit = split[1].trim().split("/");

		Material mat = Material.valueOf(split[0]);
		int buyPrice = Integer.parseInt(priceSplit[0]);
		int sellPrice = Integer.parseInt(priceSplit[1]);

		currentItems.add(new ShopItem(new ItemStack(mat), buyPrice, sellPrice));
	    }

	    in.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public static void main(String[] args) {
	new ItemConfigLoader(null);
    }

    private class Category {
	private int mainMenuSlot;
	private String name;

	public Category(int mainMenuSlot, String name) {
	    this.mainMenuSlot = mainMenuSlot;
	    this.name = name;
	}
    }
}
