package me.Kruithne.UniqueInventories;

import org.bukkit.plugin.java.JavaPlugin;

public class UniqueInventories extends JavaPlugin {

	public PlayerListener playerListener = null;
	
	public void onEnable()
	{
		this.playerListener = new PlayerListener(this);
		
		this.getServer().getPluginManager().registerEvents(
			this.playerListener,
			this
		);
	}
	
	public void onDisable()
	{
		this.playerListener.onServerClosing();
	}

}
