package no.runsafe.UniqueInventories;

import no.runsafe.framework.database.IDatabase;
import no.runsafe.framework.database.IRepository;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.player.RunsafePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class TemplateRepository implements IRepository<InventoryStorage, String>
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
		PreparedStatement select = null;
		String inventoryName = universes.getInventoryName(world);
		try
		{
			select = this.database.prepare("SELECT * FROM uniqueInventoryTemplates WHERE inventoryName=?");
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
					"Saving template inventory for world %s (%s)",
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

	private IDatabase database;
	private IOutput output;
	private IUniverses universes;
}
