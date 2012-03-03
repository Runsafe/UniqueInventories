package me.Kruithne.UniqueInventories;

import org.bukkit.entity.Player;

public class InventoryHandler {

	private DatabaseConnection database = null;
	
	InventoryHandler(DatabaseConnection database)
	{
		this.database = database;
	}
	
	public void saveInventory(Player player)
	{
		this.database.query(String.format("INSERT INTO uniqueinventories (playerName, worldName, gamemodeID, experience) VALUES('%s', '%s', %s, %s) ON DUPLICATE KEY UPDATE Experience = %4$s", player.getName(), player.getWorld().getName(), player.getGameMode(), player.getExp()));
	}
	
}
