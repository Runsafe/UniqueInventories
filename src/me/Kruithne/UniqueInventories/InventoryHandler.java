package me.Kruithne.UniqueInventories;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryHandler {

	private DatabaseConnection database = null;
	
	InventoryHandler(DatabaseConnection database)
	{
		this.database = database;
	}
	
	public void saveInventory(Player player)
	{
		this.database.query(
			String.format("INSERT INTO uniqueInventories (playerName, worldName, gamemodeID, inventory, experience) VALUES('%s', '%s', '%s', '%s', %s) ON DUPLICATE KEY UPDATE Experience = %5$s", 
				player.getName(),
				player.getWorld().getName(),
				player.getGameMode(),
				this.flatPackInventory(player.getInventory()),
				player.getExp()
			)
		);
	}
	
	private String flatPackInventory(Inventory playerInventory)
	{
		ArrayList<String> itemData = new ArrayList<String>();
		Iterator<ItemStack> itemStackIterator = playerInventory.iterator();
		
		int currentIndex = 0;
		while (itemStackIterator.hasNext())
		{	
			ItemStack theItem = itemStackIterator.next();
			if (theItem != null)
			{
				itemData.add(String.format("%s:%s:%s:%s:%s", currentIndex, theItem.getTypeId(), theItem.getAmount(), theItem.getDurability(), theItem.getData().getData()));
			}
			currentIndex++;
		}
		
		return Join(itemData, ",");
	}
	
	private String Join(ArrayList<String> coll, String delimiter)
	{
	    if (coll.isEmpty())
	    	return "";
	 
	    StringBuilder sb = new StringBuilder();
	 
	    for (String x : coll)
	    	sb.append(x + delimiter);
	 
	    sb.delete(sb.length()-delimiter.length(), sb.length());
	 
	    return sb.toString();
	}
	
}
