package me.Kruithne.UniqueInventories;

import java.io.Serializable;

public class InventoryKey implements Serializable 
{
	public InventoryKey(String name, String worldName) 
	{
		this.playerName = name;
		this.worldName = worldName;
	}

	public String getPlayerName()
	{
		return playerName;
	}

	public void setPlayerName(String playerName)
	{
		this.playerName = playerName;
	}
	
	public String getWorldName()
	{
		return worldName;
	}
	
	public void setWorldName(String worldName)
	{
		this.worldName = worldName;
	}
	
	private String playerName;
	private String worldName;
	private static final long serialVersionUID = -1924359096771138288L;
}
