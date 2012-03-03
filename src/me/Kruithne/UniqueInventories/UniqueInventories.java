package me.Kruithne.UniqueInventories;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class UniqueInventories extends JavaPlugin {
	
	public void onEnable()
	{
		this.getServer().getPluginManager().registerEvents(
			new PlayerListener(
				new InventoryHandler(
					new DatabaseConnection(Logger.getLogger("Minecraft"))
				),
				this.getServer()
				
			),
			this
		);
	}

}
