package no.runsafe.UniqueInventories;

import no.runsafe.framework.database.IDatabase;
import no.runsafe.framework.database.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateRepository extends Repository
{
	public TemplateRepository(IDatabase database, IUniverses universes)
	{
		this.database = database;
		this.universes = universes;
	}

	public InventoryStorage get(String world)
	{
		String inventoryName = universes.getInventoryName(world);
		Map<String, Object> template = database.QueryRow(
			"SELECT * FROM uniqueInventoryTemplates WHERE inventoryName=?",
			inventoryName
		);
		if (template == null)
			return createInventory(inventoryName);
		InventoryStorage inventory = new InventoryStorage();
		inventory.setPlayerName(null);
		inventory.setWorldName((String) template.get("inventoryName"));
		inventory.setInventoryYaml((String) template.get("inventory_yaml"));
		inventory.setLevel((Integer) template.get("level"));
		inventory.setExperience((Float) template.get("experience"));
		inventory.setSaved(true);
		inventory.setStack(0);
		return inventory;
	}

	public void persist(InventoryStorage inventory)
	{
		database.Update(
			"UPDATE uniqueInventoryTemplates SET inventory_yaml=?, level=?, experience=? WHERE inventoryName=?",
			inventory.getInventoryYaml(),
			inventory.getLevel(),
			inventory.getExperience(),
			universes.getInventoryName(inventory.getWorldName())
		);
	}

	public void delete(InventoryStorage inventory)
	{
		database.Execute(
			"DELETE FROM uniqueInventoryTemplates WHERE inventoryName=?",
			universes.getInventoryName(inventory.getWorldName())
		);
	}

	@Override
	public String getTableName()
	{
		return "uniqueInventoryTemplates";
	}

	@Override
	public HashMap<Integer, List<String>> getSchemaUpdateQueries()
	{
		HashMap<Integer, List<String>> versions = new HashMap<Integer, List<String>>();
		ArrayList<String> sql = new ArrayList<String>();
		sql.add(
			"CREATE TABLE `uniqueInventoryTemplates` (" +
				"`inventoryName` varchar(255) NOT NULL," +
				"`armor` longtext," +
				"`inventory` longtext," +
				"`level` int(10) unsigned NOT NULL DEFAULT '0'," +
				"`experience` float unsigned NOT NULL DEFAULT '0'," +
				"PRIMARY KEY (`inventoryName`)" +
				")"
		);
		versions.put(1, sql);
		sql = new ArrayList<String>();
		sql.add("ALTER TABLE uniqueInventories ADD COLUMN inventory_yaml longtext");
		versions.put(2, sql);
		return versions;
	}

	private InventoryStorage createInventory(String inventoryName)
	{
		database.Update(
			"INSERT INTO uniqueInventoryTemplates (inventoryName) VALUES (?)",
			inventoryName
		);
		InventoryStorage inv = new InventoryStorage();
		inv.setPlayerName(null);
		inv.setWorldName(inventoryName);
		inv.setSaved(true);
		return inv;
	}

	private final IDatabase database;
	private final IUniverses universes;
}
