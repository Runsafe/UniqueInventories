package me.Kruithne.UniqueInventories;

import javax.persistence.Column;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Table;

@DynamicUpdate
@Table(appliesTo = "uniqueInventories")
public class InventoryStorage {

	@Id
	@Column(name = "playerName")
	public String getPlayerName()
	{
		return playerName;
	}

	public void setPlayerName(String playerName)
	{
		this.playerName = playerName;
	}
	
	@Id
	@Column(name = "worldName")
	public String getWorldName()
	{
		return worldName;
	}
	
	public void setWorldName(String worldName)
	{
		this.worldName = worldName;
	}
	
	@Column(name = "inventory", nullable = false)
	public String getInventory()
	{
		return inventory;
	}
	
	public void setInventory(String inventory)
	{
		this.inventory = inventory;
	}

	@Column(name = "experience", nullable = false)
	public float getExperience()
	{
		return experience;
	}
	
	public void setExperience(float experience)
	{
		this.experience = experience;
	}
	
	@Column(name = "level", nullable = false)
	public int getLevel()
	{
		return level;
	}
	
	public void setLevel(int level)
	{
		this.level = level;
	}
	
	@Column(name = "armor")
	public String getArmor()
	{
		return armor;
	}
	
	public void setArmor(String armor)
	{
		this.armor = armor;
	}
	
	private String playerName;
	private String worldName;
	private String inventory;
	private float experience;
	private int level;
	private String armor;
}
