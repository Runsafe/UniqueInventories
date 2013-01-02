package no.runsafe.UniqueInventories;

import no.runsafe.framework.database.IDatabase;
import no.runsafe.framework.database.IRepository;
import no.runsafe.framework.database.ISchemaChanges;
import no.runsafe.framework.output.IOutput;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class TemplateRepository implements IRepository<InventoryStorage, String>, ISchemaChanges
{
	public TemplateRepository(IDatabase database, IOutput output, IUniverses universes)
	{
		this.database = database;
		this.output = output;
		this.universes = universes;
	}

	@Override
	public InventoryStorage get(String world)
	{
		String inventoryName = universes.getInventoryName(world);
		try
		{
			PreparedStatement select = this.database.prepare("SELECT * FROM uniqueInventoryTemplates WHERE inventoryName=?");
			select.setString(1, inventoryName);
			ResultSet set = select.executeQuery();
			InventoryStorage inv = new InventoryStorage();
			if (set.first())
			{
				inv.setPlayerName(null);
				inv.setWorldName(set.getString("inventoryName"));
				inv.setArmor(set.getString("armor"));
				inv.setInventory(set.getString("inventory"));
				inv.setLevel(set.getInt("level"));
				inv.setExperience(set.getFloat("experience"));
				inv.setSaved(true);
				inv.setStack(0);
			}
			else
			{
				inv = createInventory(inventoryName);
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
					"Saving template inventory for world %s",
					inventory.getWorldName()
				),
				Level.FINE
			);
			PreparedStatement update = this.database.prepare(
				"UPDATE uniqueInventoryTemplates SET armor=?, inventory=?, level=?, experience=? WHERE inventoryName=?"
			);
			update.setString(1, inventory.getArmor());
			update.setString(2, inventory.getInventory());
			update.setLong(3, inventory.getLevel());
			update.setFloat(4, inventory.getExperience());
			update.setString(8, universes.getInventoryName(inventory.getWorldName()));
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
				"DELETE FROM uniqueInventoryTemplates WHERE inventoryName=?"
			);
			delete.setString(1, universes.getInventoryName(inventory.getWorldName()));
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
//		sql = new ArrayList<String>();
//		sql.add("ALTER TABLE uniqueInventoryTemplates ADD COLUMN version int");
//		sql.add("UPDATE uniqueInventories SET version = 1");
//		sql.add("ALTER TABLE uniqueInventories ADD COLUMN serializedInventory MEDIUMBLOB");
//		versions.put(2, sql);
		return versions;
	}

	private InventoryStorage createInventory(String inventoryName) throws SQLException
	{
		InventoryStorage inv = new InventoryStorage();
		PreparedStatement insert = this.database.prepare("INSERT INTO uniqueInventoryTemplates (inventoryName) VALUES (?)");
		inv.setPlayerName(null);
		inv.setWorldName(inventoryName);
		inv.setSaved(true);
		insert.setString(1, inventoryName);
		insert.executeUpdate();
		insert.close();
		return inv;
	}

	private final IDatabase database;
	private final IOutput output;
	private final IUniverses universes;
}
