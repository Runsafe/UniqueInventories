package no.runsafe.UniqueInventories;

import no.runsafe.framework.database.IDatabase;
import no.runsafe.framework.database.Repository;
import no.runsafe.framework.server.player.RunsafePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryRepository extends Repository
{
	public InventoryRepository(IDatabase database, IUniverses universes)
	{
		this.database = database;
		this.universes = universes;
	}

	public InventoryStorage get(RunsafePlayer key)
	{
		return get(key, key.getWorld().getName());
	}

	public InventoryStorage get(RunsafePlayer player, String world)
	{
		String playerName = player.getName();
		String inventoryName = universes.getInventoryName(world);
		Map<String, Object> data = database.QueryRow(
			"SELECT * FROM uniqueInventories WHERE playerName=? AND inventoryName=? ORDER BY stack DESC",
			playerName, inventoryName
		);
		if (data == null)
			return createInventory(playerName, inventoryName, 0);

		InventoryStorage inv = new InventoryStorage();
		inv.setPlayerName((String) data.get("playerName"));
		inv.setWorldName((String) data.get("inventoryName"));
		inv.setArmor((String) data.get("armor"));
		inv.setInventory((String) data.get("inventory"));
		inv.setLevel((Integer) data.get("level"));
		inv.setExperience((Float) data.get("experience"));
		inv.setSaved((Boolean) data.get("saved"));
		inv.setStack((Integer) data.get("stack"));
		inv.setVersion((Integer) data.get("version"));
		inv.setInventoryYaml((String) data.get("yaml_inventory"));
		return inv;
	}

	public void persist(InventoryStorage inventory)
	{
		database.Update(
			"UPDATE uniqueInventories " +
				"SET armor=?, inventory=?, level=?, experience=?, saved=?, stack=?, yaml_inventory=?, version=? " +
				"WHERE playerName=? AND inventoryName=? AND stack=?",
			inventory.getArmor(), inventory.getInventory(), inventory.getLevel(), inventory.getExperience(),
			inventory.getSaved(), inventory.getStack(), inventory.getInventoryYaml(), inventory.getVersion(),
			inventory.getPlayerName(), universes.getInventoryName(inventory.getWorldName()), inventory.getStack()
		);
	}

	public void delete(InventoryStorage inventory)
	{
		database.Execute(
			"DELETE FROM uniqueInventories WHERE playerName=? AND inventoryName=? AND stack=?",
			inventory.getPlayerName(), universes.getInventoryName(inventory.getWorldName()), inventory.getStack()
		);
	}

	public InventoryStorage addStack(RunsafePlayer key)
	{
		InventoryStorage inv = get(key);
		return createInventory(key.getName(), universes.getInventoryName(key.getWorld().getName()), inv.getStack() + 1);
	}

	private InventoryStorage createInventory(String playerName, String inventoryName, int stack)
	{
		if (database.Update(
			"INSERT INTO uniqueInventories (playerName, inventoryName, stack, version) VALUES (?, ?, ?, ?)",
			playerName, inventoryName, stack, VERSION
		) == 0)
			return null;

		InventoryStorage inv = new InventoryStorage();
		inv.setPlayerName(playerName);
		inv.setWorldName(inventoryName);
		inv.setStack(stack);
		inv.setVersion(VERSION);
		inv.setSaved(true);
		return inv;
	}

	public List<InventoryStorage> getByVersion(int version)
	{
		List<Map<String, Object>> data = database.Query(
			"SELECT * FROM uniqueInventories WHERE version=? ORDER BY stack DESC",
			version
		);
		if (data == null)
			return null;
		ArrayList<InventoryStorage> results = new ArrayList<InventoryStorage>();
		for (Map<String, Object> set : data)
		{
			InventoryStorage inv = new InventoryStorage();
			inv.setPlayerName((String) set.get("playerName"));
			inv.setWorldName((String) set.get("inventoryName"));
			inv.setArmor((String) set.get("armor"));
			inv.setInventory((String) set.get("inventory"));
			inv.setLevel((Integer) set.get("level"));
			inv.setExperience((Float) set.get("experience"));
			inv.setSaved((Boolean) set.get("saved"));
			inv.setStack((Integer) set.get("stack"));
			inv.setVersion((Integer) set.get("version"));
			inv.setInventoryYaml((String) set.get("yaml_inventory"));
			results.add(inv);
		}
		return results;
	}

	public void Wipe(String world)
	{
		database.Execute(
			"DELETE FROM uniqueInventories WHERE inventoryName=?",
			universes.getInventoryName(world)
		);
	}

	@Override
	public String getTableName()
	{
		return "uniqueInventories";
	}

	@Override
	public HashMap<Integer, List<String>> getSchemaUpdateQueries()
	{
		HashMap<Integer, List<String>> versions = new HashMap<Integer, List<String>>();
		ArrayList<String> sql = new ArrayList<String>();
		sql.add(
			"CREATE TABLE `uniqueInventories` (" +
				"`playerName` varchar(50) NOT NULL," +
				"`inventoryName` varchar(255) NOT NULL DEFAULT ''," +
				"`armor` longtext," +
				"`inventory` longtext," +
				"`saved` tinyint(1) unsigned NOT NULL DEFAULT '1'," +
				"`level` int(10) unsigned NOT NULL DEFAULT '0'," +
				"`experience` float unsigned NOT NULL DEFAULT '0'," +
				"`stack` int(11) NOT NULL DEFAULT '0'," +
				"PRIMARY KEY (`playerName`,`inventoryName`,`stack`)" +
				")"
		);
		versions.put(1, sql);
		sql = new ArrayList<String>();
		sql.add("ALTER TABLE uniqueInventories ADD COLUMN `version` int(10) NOT NULL default 1");
		sql.add("ALTER TABLE uniqueInventories ADD COLUMN `yaml_inventory` longtext");
		versions.put(2, sql);
		return versions;
	}

	private static final int VERSION = 2;
	private final IDatabase database;
	private final IUniverses universes;
}
