package me.Kruithne.UniqueInventories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Server;
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
		String worldName = theWorld.getName();
		
		if (worldName.equals("world_nether"))
		{
			worldName = "world";
		}
		
		this.database.query(
			String.format(
				"INSERT INTO uniqueInventories (playerName, worldName, inventory, experience, level, armor) VALUES('%s', '%s', '%s', %s, %s, '%s') ON DUPLICATE KEY UPDATE experience = %4$s, inventory = '%3$s', level = %5$s, armor = '%6$s'", 
				player.getName(),
				worldName,
				this.flatPackInventory(player),
				player.getExp(),
				player.getLevel(),
				this.flatPackArmor(player)
			)
		);
	}
	
	public void saveAllInventories(Server server)
	{
		List<World> worlds = server.getWorlds();
		Iterator<World> worldIterator = worlds.iterator();
		
		while (worldIterator.hasNext())
		{
			World world = worldIterator.next();
			List<Player> players = world.getPlayers();
			Iterator<Player> playerIterator = players.iterator();
			
			while (playerIterator.hasNext())
			{
				Player player = playerIterator.next();
				this.saveInventory(player, world);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void resetPlayersInventory(Player player)
	{
		player.setExp(0.0F);
		player.setLevel(0);
		ItemStack[] armorReset = {
			new ItemStack(0),
			new ItemStack(0),
			new ItemStack(0),
			new ItemStack(0)
		};
		player.getInventory().setArmorContents(armorReset);
		player.getInventory().clear();
		player.updateInventory();
	}
	
	public void loadInventory(Player player, World theWorld)
	{
		this.resetPlayersInventory(player);
		
		String worldName = theWorld.getName();
		
		if (worldName.equals("world_nether"))
		{
			worldName = "world";
		}
		
		ResultSet storedData = this.database.getQuery(
			String.format(
				"SELECT inventory, experience, level, armor FROM uniqueInventories WHERE playerName = '%s' AND worldName = '%s'",
				player.getName(),
				worldName
			)
		);	
		
		try
		{
			if (storedData.next())
			{
				player.setExp(storedData.getFloat("experience"));
				player.setLevel(storedData.getInt("level"));
				this.unPackToInventory(storedData.getString("inventory"), storedData.getString("armor"), player);
				//this.database.query(String.format("UPDATE uniqueInventories SET saved = 0 WHERE playerName = '%s' AND worldName = '%s'", player.getName(), worldName));
				
			}
		}
		catch (SQLException e)
		{
			this.database.log.log(Level.SEVERE, String.format("SQL Error: %s", e.getMessage()));
		}
	}
	
	@SuppressWarnings("deprecation")
	private void unPackToInventory(String packedInventory, String packedArmor, Player player)
	{
		Inventory playerInventory = player.getInventory();
		playerInventory.clear();
		//index, ID, amount, dura, data, helmet, chestplate, leggings, boots
		HashMap<Integer, ItemStack> itemStacks = new HashMap<Integer, ItemStack>();
		
		if (!packedInventory.isEmpty())
		{
			String[] items = packedInventory.split(",");
			
			for (int i = 0; i < items.length; i++)
			{
				if (!items[i].isEmpty())
				{
					String[] itemData = items[i].split(":");
					int itemID = Integer.parseInt(itemData[1]);
					ItemStack itemStack = new ItemStack(itemID);
					itemStack.setAmount(Integer.parseInt(itemData[2]));
					itemStack.setDurability(Short.parseShort(itemData[3]));
					itemStack.setData(new MaterialData(itemID, Byte.parseByte(itemData[4])));
					
					itemStacks.put(Integer.parseInt(itemData[0]), itemStack);
				}
			}
		}
		
		this.unpackArmorToPlayer(packedArmor, player);
		
		Iterator<ItemStack> itemStackIterator = playerInventory.iterator();
		
		int currentIndex = 0;
		while (itemStackIterator.hasNext())
		{	
			itemStackIterator.next();
			
			if (itemStacks.containsKey(currentIndex))
			{
				playerInventory.setItem(currentIndex, itemStacks.get(currentIndex));
			}
			
			currentIndex++;
		}
		player.updateInventory();
	}
	
	private String flatPackArmor(Player player)
	{
		ItemStack[] armor = player.getInventory().getArmorContents();
		
		return String.format(
			"%s:%s:%s,%s:%s:%s,%s:%s:%s,%s:%s:%s",
			armor[0].getTypeId(),
			armor[0].getDurability(),
			armor[0].getData().getData(),
			armor[1].getTypeId(),
			armor[1].getDurability(),
			armor[1].getData().getData(),
			armor[2].getTypeId(),
			armor[2].getDurability(),
			armor[2].getData().getData(),
			armor[3].getTypeId(),
			armor[3].getDurability(),
			armor[3].getData().getData()
		);
	}
	
	private void unpackArmorToPlayer(String armorImport, Player player)
	{
		if (!armorImport.isEmpty())
		{
			String[] armorItems = armorImport.split(",");
			
			ItemStack[] armorPack = {
				this.compactArmorItem(armorItems[0]),
				this.compactArmorItem(armorItems[1]),
				this.compactArmorItem(armorItems[2]),
				this.compactArmorItem(armorItems[3])
			};
			
			player.getInventory().setArmorContents(armorPack);
		}
	}
	
	private ItemStack compactArmorItem(String armorDataRaw)
	{
		String[] armorData = armorDataRaw.split(":");
		
		int itemID = Integer.parseInt(armorData[0]);
		ItemStack armorItem = new ItemStack(itemID);
		armorItem.setDurability(Short.parseShort(armorData[1]));
		MaterialData armorMatData = new MaterialData(itemID);
		armorMatData.setData(Byte.parseByte(armorData[2]));
		armorItem.setData(armorMatData);
		return armorItem;
	}
	
	private String flatPackInventory(Player player)
	{
		Inventory playerInventory = player.getInventory();
		
		ArrayList<String> itemData = new ArrayList<String>();
		Iterator<ItemStack> itemStackIterator = playerInventory.iterator();
		
		int currentIndex = 0;
		while (itemStackIterator.hasNext())
		{	
			ItemStack theItem = itemStackIterator.next();
			if (theItem != null)
			{
				itemData.add(
					String.format(
						"%s:%s:%s:%s:%s",
						currentIndex,
						theItem.getTypeId(),
						theItem.getAmount(),
						theItem.getDurability(),
						theItem.getData().getData()
					)
				);
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
