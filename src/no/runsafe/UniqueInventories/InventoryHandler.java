package no.runsafe.UniqueInventories;

import no.runsafe.framework.event.IPluginEnabled;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.framework.server.player.RunsafePlayer;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryHandler implements IPluginEnabled
{
	public InventoryHandler(InventoryRepository repository, TemplateRepository templates, IOutput output)
	{
		this.repository = repository;
		this.templates = templates;
		this.console = output;
	}

	public void saveInventory(RunsafePlayer player, RunsafeWorld theWorld)
	{
		if(player == null)
		{
			console.writeColoured("Player is NULL!");
			return;
		}
		if(theWorld == null)
		{
			console.writeColoured("World is NULL!");
			return;
		}
		InventoryStorage storage = repository.get(player, theWorld.getName());
		storage.setPlayerName(player.getName());
		storage.setWorldName(theWorld.getName());
		storage.setExperience(player.getXP());
		storage.setLevel(player.getLevel());
		storage.setInventoryYaml(player.getInventory().serialize());
		storage.setSaved(true);
		this.repository.persist(storage);
		if (player.hasPermission("runsafe.uniqueinventory.notices"))
			player.sendMessage(String.format("Inventory for %s was saved.", theWorld.getName()));
	}

	public void resetPlayersInventory(RunsafePlayer player)
	{
		if (player == null || player.getInventory() == null)
			return;

		player.removeBuffs();
		player.setXP(0.0F);
		player.setLevel(0);
		player.getInventory().clear();
		player.updateInventory();
		if (player.hasPermission("runsafe.uniqueinventory.notices"))
			player.sendMessage("Your inventory has been cleared.");
	}

	public void loadInventory(RunsafePlayer player)
	{
		loadInventory(player, this.repository.get(player));
	}

	public void loadTemplateInventory(RunsafePlayer player)
	{
		InventoryStorage stored = this.templates.get(player.getWorld().getName());
		player.removeBuffs();

		if (stored != null)
		{
			player.setXP(stored.getExperience());
			player.setLevel(stored.getLevel());
			player.getInventory().unserialize(stored.getInventoryYaml());
			if (player.hasPermission("runsafe.uniqueinventory.notices"))
				player.sendMessage(String.format("Default inventory for %s was loaded.", stored.getWorldName()));
		}
	}

	public void saveTemplateInventory(RunsafePlayer player)
	{
		InventoryStorage storage;
		storage = new InventoryStorage();
		storage.setWorldName(player.getWorld().getName());
		storage.setInventoryYaml(player.getInventory().serialize());
		storage.setExperience(player.getXP());
		storage.setLevel(player.getLevel());
		this.templates.persist(storage);
		if (player.hasPermission("runsafe.uniqueinventory.notices"))
			player.sendMessage(String.format("Default inventory for %s was updated.", storage.getWorldName()));
	}

	public void PushInventory(RunsafePlayer player)
	{
		saveInventory(player, player.getWorld());
		loadInventory(player, repository.addStack(player));
		if (player.hasPermission("runsafe.uniqueinventory.notices"))
			player.sendMessage("Inventory pushed to the stack");
	}

	public void PopInventory(RunsafePlayer player)
	{
		InventoryStorage inventory = repository.get(player);
		if (inventory.getStack() > 0)
		{
			repository.delete(inventory);
			if (player.hasPermission("runsafe.uniqueinventory.notices"))
				player.sendMessage("Inventory popped from the stack");
		}
		loadInventory(player);
	}

	@Override
	@Deprecated
	public void OnPluginEnabled()
	{
		console.write("Upgrading Unique Inventories..");
		String[] armorSlots = new String[]{"helmet", "chestplate", "leggings", "boots"};
		List<InventoryStorage> toConvert = repository.getByVersion(1);
		int count = 0;
		for (InventoryStorage convert : toConvert)
		{
			count++;
			if (count % 500 == 0)
				console.write(String.format("Processing %d/%d (%.2f%%)", count, toConvert.size(), 100.0 * count / toConvert.size()));
			try
			{
				YamlConfiguration serialize = new YamlConfiguration();
				ConfigurationSection contents = serialize.createSection("contents");
				ConfigurationSection armour = serialize.createSection("armour");
				if (convert.getInventory() != null && !convert.getInventory().isEmpty())
				{
					String[] items = convert.getInventory().split(",");

					for (String item : items)
					{
						//0&61:22:0:0:0
						if (!item.isEmpty())
						{
							String[] itemObject = item.split("&");
							contents.set(itemObject[0], this.unpackItem(itemObject[1]));
						}
					}
				}

				if (convert.getArmor() != null && !convert.getArmor().isEmpty())
				{
					String[] armorItems = convert.getArmor().split(",");
					for (String armorItem : armorItems)
					{
						String[] armorObject = armorItem.split("&");
						armour.set(armorSlots[Integer.parseInt(armorObject[0])], this.unpackItem(armorObject[1]));
					}
				}
				convert.setInventoryYaml(serialize.saveToString());
				convert.setVersion(2);
				repository.persist(convert);
			}
			catch (NumberFormatException e)
			{
				console.write("Error converting inventory " + convert.getPlayerName() + "@" + convert.getWorldName());
				console.write(ExceptionUtils.getFullStackTrace(e));
			}
			catch (IllegalArgumentException e)
			{
				console.write("Error converting inventory " + convert.getPlayerName() + "@" + convert.getWorldName());
				console.write(ExceptionUtils.getFullStackTrace(e));
			}
		}
		console.write(String.format("Converted %d inventories.", count));
	}

	private void loadInventory(RunsafePlayer player, InventoryStorage stored)
	{
		if (stored != null && !stored.getSaved())
			return;

		if (stored != null)
		{
			player.removeBuffs();
			player.setXP(stored.getExperience());
			player.setLevel(stored.getLevel());
			if (stored.getInventoryYaml() != null)
				player.getInventory().unserialize(stored.getInventoryYaml());
			player.updateInventory();
			stored.setSaved(false);
			this.repository.persist(stored);
			if (player.hasPermission("runsafe.uniqueinventory.notices"))
				player.sendMessage(String.format("Inventory for %s was loaded.", stored.getWorldName()));
		}
		else
		{
			loadTemplateInventory(player);
		}
	}

	private Map<Enchantment, Integer> unpackEnchants(String enchantString)
	{
		Map<Enchantment, Integer> returnEnchants = new HashMap<Enchantment, Integer>();
		String enchantSplit[] = enchantString.split("@");
		for (String anEnchantSplit : enchantSplit)
		{
			String enchantData[] = anEnchantSplit.split("#");
			Enchantment enchant = new EnchantmentWrapper(Integer.parseInt(enchantData[0]));
			returnEnchants.put(enchant, Integer.parseInt(enchantData[1]));
		}

		return returnEnchants;
	}

	private ItemStack unpackItem(String itemString)
	{
		String[] itemData = itemString.split(":");
		int itemID = Integer.parseInt(itemData[0]);
		ItemStack itemStack = new ItemStack(itemID);
		itemStack.setAmount(Integer.parseInt(itemData[1]));
		itemStack.setDurability(Short.parseShort(itemData[2]));
		itemStack.setData(new MaterialData(itemID, Byte.parseByte(itemData[3])));

		// Backwards compatibility check
		if (itemData.length > 4 && !itemData[4].equals("0"))
			itemStack.addEnchantments(unpackEnchants(itemData[4]));
		return itemStack;
	}

	private final InventoryRepository repository;
	private final TemplateRepository templates;
	private final IOutput console;
}
