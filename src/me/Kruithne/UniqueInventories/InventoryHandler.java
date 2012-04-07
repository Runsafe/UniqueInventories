package me.Kruithne.UniqueInventories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import no.runsafe.framework.interfaces.IRepository;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class InventoryHandler {

	private IRepository<InventoryStorage, Player> repository = null;
	
	public InventoryHandler(IRepository<InventoryStorage, Player> repository)
	{
		this.repository = repository;
	}
	
	public void saveInventory(Player player, World theWorld)
	{
		String worldName = theWorld.getName();
		
		if (worldName.equals("world_nether"))
		{
			worldName = "world";
		}

		InventoryStorage storage = new InventoryStorage();
		storage.setPlayerName(player.getName());
		storage.setWorldName(worldName);
		storage.setInventory(this.flatPackInventory(player));
		storage.setExperience(player.getExp());
		storage.setLevel(player.getLevel());
		storage.setArmor(this.flatPackArmor(player));
		this.repository.persist(storage);
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
		
		InventoryStorage stored = this.repository.get(player); 
//				(InventoryStorage)database.getSession().get(
//			InventoryStorage.class, 
//			new InventoryKey(player.getName(), worldName)
//		);
		
		if(stored != null)
		{
			player.setExp(stored.getExperience());
			player.setLevel(stored.getLevel());
			this.unPackToInventory(stored.getInventory(), stored.getArmor(), player);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void unPackToInventory(String packedInventory, String packedArmor, Player player)
	{
		Inventory playerInventory = player.getInventory();
		playerInventory.clear();
		//index, ID, amount, dura, data, helmet, chestplate, leggings, boots
		HashMap<Integer, ItemStack> itemStacks = new HashMap<Integer, ItemStack>();
		
		if (packedInventory != null && !packedInventory.isEmpty())
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
		if (armorImport != null && !armorImport.isEmpty())
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
