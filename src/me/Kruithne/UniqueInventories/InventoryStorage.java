package me.Kruithne.UniqueInventories;

import no.runsafe.framework.database.RunsafeEntity;

public class InventoryStorage extends RunsafeEntity
{
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
	
	public String getInventory()
	{
		return inventory;
	}
	
	public void setInventory(String inventory)
	{
		this.inventory = inventory;
	}

	public float getExperience()
	{
		return experience;
	}
	
	public void setExperience(float experience)
	{
		this.experience = experience;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public void setLevel(int level)
	{
		this.level = level;
	}
	
	public String getArmor()
	{
		return armor;
	}
	
	public void setArmor(String armor)
	{
		this.armor = armor;
	}
	
	public boolean getSaved()
	{
		return saved;
	}
	
	public void setSaved(boolean saved)
	{
		this.saved = saved;
	}

	private String playerName;
	private String worldName;
	private String inventory;
	private float experience;
	private int level;
	private String armor;
	private boolean saved;
}
