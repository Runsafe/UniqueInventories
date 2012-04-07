package me.Kruithne.UniqueInventories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;

import no.runsafe.framework.interfaces.IConfiguration;
import no.runsafe.framework.interfaces.IDatabase;
import no.runsafe.framework.interfaces.IOutput;
import no.runsafe.framework.interfaces.IRepository;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class InventoryRepository implements IRepository<InventoryStorage, Player> 
{
	public InventoryRepository(IDatabase database, IOutput output, IConfiguration config)
	{
		this.database = database;
		this.output = output;
		
		ConfigurationSection grouping = config.getSection("groupedInventories");
		groupedInventories = new HashMap<String, String>();
		for(String universe : grouping.getKeys(true))
		{
			for(String world : grouping.getStringList(universe))
			{
				groupedInventories.put(world, universe);
				output.outputToConsole(String.format("Adding world %s to universe %s.", world, universe), Level.INFO);
			}
		}
	}
	
	@Override
	public InventoryStorage get(Player key)
	{
		PreparedStatement select = null;
		String playerName = key.getName();
		String inventoryName = getInventoryName(key.getWorld().getName());
		try
		{
			select = this.database.prepare("SELECT * FROM uniqueInventories WHERE playerName=? AND inventoryName=?");
			select.setString(1, playerName);
			select.setString(2, inventoryName);
			ResultSet set = select.executeQuery();
			InventoryStorage inv = new InventoryStorage();
			if(set.first())
			{
				inv.setPlayerName(set.getString("playerName"));
				inv.setWorldName(set.getString("inventoryName"));
				inv.setArmor(set.getString("armor"));
				inv.setInventory(set.getString("inventory"));
				inv.setLevel(set.getInt("level"));
				inv.setExperience(set.getFloat("experience"));
			}
			else
			{
				PreparedStatement insert = this.database.prepare("INSERT INTO uniqueInventories (playerName, inventoryName) VALUES (?, ?)");
				inv.setPlayerName(playerName);
				inv.setWorldName(inventoryName);
				insert.setString(1, playerName);
				insert.setString(2, inventoryName);
				insert.executeUpdate();
				insert.close();
			}
			select.close();
			return inv;
		}
		catch (SQLException e) 
		{
			this.output.outputToConsole(e.getMessage() + e.getStackTrace(), Level.SEVERE);
		}
		return null;
	}

	@Override
	public void persist(InventoryStorage inventory) 
	{
		try
		{
			this.output.outputDebugToConsole(
				String.format(
					"Saving player %s in world %s (%s)", 
					inventory.getPlayerName(), 
					inventory.getWorldName(), 
					inventory.getInventory()
				),
				Level.FINE
			);
			PreparedStatement update = this.database.prepare(
				"UPDATE uniqueInventories SET armor=?, inventory=?, level=?, experience=? WHERE playerName=? AND inventoryName=?"
			);
			update.setString(1, inventory.getArmor());
			update.setString(2, inventory.getInventory());
			update.setLong(3, inventory.getLevel());
			update.setFloat(4, inventory.getExperience());
			update.setString(5, inventory.getPlayerName());
			update.setString(6, getInventoryName(inventory.getWorldName()));
		}
		catch(SQLException e)
		{
			this.output.outputToConsole(e.getMessage() + e.getStackTrace(), Level.SEVERE);
		}
	}
	
	private String getInventoryName(String world)
	{
		if(groupedInventories.containsKey(world))
			return groupedInventories.get(world);
		
		return world;
	}
	
	private IDatabase database;
	private IOutput output;
	private HashMap<String, String> groupedInventories;
}
