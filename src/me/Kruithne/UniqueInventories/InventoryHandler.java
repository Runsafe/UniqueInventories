package me.Kruithne.UniqueInventories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class InventoryHandler {

	private DatabaseConnection database = null;
	
	InventoryHandler(DatabaseConnection database)
	{
		this.database = database;
	}
	
	public void saveInventory(Player player, World theWorld)
	{
		this.database.query(
			String.format(
				"INSERT INTO uniqueInventories (playerName, worldName, gamemodeID, inventory, experience) VALUES('%s', '%s', '%s', '%s', %s) ON DUPLICATE KEY UPDATE Experience = %5$s, Inventory = '%4$s'", 
				player.getName(),
				theWorld.getName(),
				player.getGameMode(),
				this.flatPackInventory(player.getInventory()),
				player.getExp()
			)
		);
	}
	
	public void loadInventory(Player player, World theWorld)
	{
		player.setExp(0.0F);
		
		ResultSet storedData = this.database.getQuery(
			String.format(
				"SELECT inventory, experience FROM uniqueInventories WHERE playerName = '%s' AND worldName = '%s' AND gamemodeID = '%s'",
				player.getName(),
				theWorld.getName(),
				player.getGameMode()
			)
		);	
		
		try
		{
			if (storedData.next())
			{
				player.setExp(storedData.getFloat("experience"));
				this.unPackToInventory(storedData.getString("inventory"), player.getInventory());
				
			}
		}
		catch (SQLException e)
		{
			this.database.log.log(Level.SEVERE, String.format("SQL Error: %s", e.getMessage()));
		}
	}
	
	private void unPackToInventory(String packedInventory, Inventory playerInventory)
	{
		playerInventory.clear();
		//index, ID, amount, dura, data
		String[] items = packedInventory.split(",");
		
		HashMap<Integer, ItemStack> itemStacks = new HashMap<Integer, ItemStack>();
		
		for (int i = 0; i < items.length; i++)
		{
			String[] itemData = items[i].split(":");
			int itemID = Integer.parseInt(itemData[1]);
			ItemStack itemStack = new ItemStack(itemID);
			itemStack.setAmount(Integer.parseInt(itemData[2]));
			itemStack.setDurability(Short.parseShort(itemData[3]));
			itemStack.setData(new MaterialData(itemID, Byte.parseByte(itemData[4])));
			
			itemStacks.put(Integer.parseInt(itemData[0]), itemStack);
		}
		
		Iterator<ItemStack> itemStackIterator = playerInventory.iterator();
		
		int currentIndex = 0;
		while (itemStackIterator.hasNext())
		{	
			if (itemStacks.containsKey(currentIndex))
			{
				playerInventory.setItem(currentIndex, itemStacks.get(currentIndex));
			}
			
			currentIndex++;
		}
		
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
