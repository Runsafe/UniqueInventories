package no.runsafe.UniqueInventories;

import no.runsafe.framework.database.IDatabase;
import no.runsafe.framework.database.IRepository;
import no.runsafe.framework.database.ISchemaChanges;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.player.RunsafePlayer;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class InventoryRepository implements IRepository<InventoryStorage, RunsafePlayer>, ISchemaChanges
{
	public InventoryRepository(IDatabase database, IOutput output, IUniverses universes)
	{
		this.database = database;
		this.output = output;
		this.universes = universes;
	}

	@Override
	public InventoryStorage get(RunsafePlayer key)
	{
		return get(key, key.getWorld().getName());
	}

	public InventoryStorage get(RunsafePlayer player, String world)
	{
		String playerName = player.getName();
		String inventoryName = universes.getInventoryName(world);
		try
		{
			PreparedStatement select = this.database.prepare("SELECT * FROM uniqueInventories WHERE playerName=? AND inventoryName=? ORDER BY stack DESC");
			select.setString(1, playerName);
			select.setString(2, inventoryName);
			ResultSet set = select.executeQuery();
			InventoryStorage inv = new InventoryStorage();
			if (set.first())
			{
				inv.setPlayerName(set.getString("playerName"));
				inv.setWorldName(set.getString("inventoryName"));
				inv.setArmor(set.getString("armor"));
				inv.setInventory(set.getString("inventory"));
				inv.setLevel(set.getInt("level"));
				inv.setExperience(set.getFloat("experience"));
				inv.setSaved(set.getBoolean("saved"));
				inv.setStack(set.getInt("stack"));
//				inv.setInventoryData(set.getBytes("serializedInventory"));
			}
			else
			{
				inv = createInventory(playerName, inventoryName, 0);
			}
			select.close();
			return inv;
		}
		catch (SQLException e)
		{
			this.output.outputToConsole(e.getMessage(), Level.SEVERE);
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
				"UPDATE uniqueInventories " +
					"SET armor=?, inventory=?, level=?, experience=?, saved=?, stack=?, "  + //serializedInventory=? " +
					"WHERE playerName=? AND inventoryName=? AND stack=?"
			);
			update.setString(1, inventory.getArmor());
			update.setString(2, inventory.getInventory());
			update.setLong(3, inventory.getLevel());
			update.setFloat(4, inventory.getExperience());
			update.setBoolean(5, inventory.getSaved());
			update.setInt(6, inventory.getStack());
//			if (inventory.getInventoryData() == null)
//				update.setBytes(7, null);
//			else
//				update.setBinaryStream(
//					7,
//					new ByteArrayInputStream(inventory.getInventoryData()),
//					inventory.getInventoryData().length
//				);
			update.setString(7, inventory.getPlayerName());
			update.setString(8, universes.getInventoryName(inventory.getWorldName()));
			update.setInt(9, inventory.getStack());
			update.execute();
		}
		catch (SQLException e)
		{
			this.output.outputToConsole(e.getMessage(), Level.SEVERE);
		}
	}

	@Override
	public void delete(InventoryStorage inventory)
	{
		try
		{
			PreparedStatement delete = this.database.prepare(
				"DELETE FROM uniqueInventories WHERE playerName=? AND inventoryName=? AND stack=?"
			);
			delete.setString(1, inventory.getPlayerName());
			delete.setString(2, universes.getInventoryName(inventory.getWorldName()));
			delete.setInt(3, inventory.getStack());
			delete.execute();
		}
		catch (SQLException e)
		{
			this.output.outputToConsole(e.getMessage(), Level.SEVERE);
		}
	}

	public InventoryStorage addStack(RunsafePlayer key)
	{
		try
		{
			InventoryStorage inv = get(key);
			return createInventory(key.getName(), universes.getInventoryName(key.getWorld().getName()), inv.getStack() + 1);
		}
		catch (SQLException e)
		{
			this.output.outputToConsole(e.getMessage(), Level.SEVERE);
			return null;
		}
	}

	private InventoryStorage createInventory(String playerName, String inventoryName, int stack) throws SQLException
	{
		InventoryStorage inv = new InventoryStorage();
		PreparedStatement insert = this.database.prepare("INSERT INTO uniqueInventories (playerName, inventoryName, stack) VALUES (?, ?, ?)");
		inv.setPlayerName(playerName);
		inv.setWorldName(inventoryName);
		inv.setSaved(true);
		insert.setString(1, playerName);
		insert.setString(2, inventoryName);
		insert.setInt(3, stack);
		insert.executeUpdate();
		insert.close();
		return inv;
	}

	private final IDatabase database;
	private final IOutput output;
	private final IUniverses universes;

	public void Wipe(String world)
	{
		try
		{
			PreparedStatement delete = this.database.prepare(
				"DELETE FROM uniqueInventories WHERE inventoryName=?"
			);
			delete.setString(1, universes.getInventoryName(world));
			delete.execute();
		}
		catch (SQLException e)
		{
			this.output.outputToConsole(e.getMessage(), Level.SEVERE);
		}
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
//		sql = new ArrayList<String>();
//		sql.add("ALTER TABLE uniqueInventories ADD COLUMN version int");
//		sql.add("UPDATE uniqueInventories SET version = 1");
//		sql.add("ALTER TABLE uniqueInventories ADD COLUMN serializedInventory MEDIUMBLOB");
//		versions.put(2, sql);
		return versions;
	}
}
