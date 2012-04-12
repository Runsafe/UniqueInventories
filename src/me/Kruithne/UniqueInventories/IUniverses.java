package me.Kruithne.UniqueInventories;

import org.bukkit.World;

/**
 * Created with IntelliJ IDEA.
 * User: mortenn
 * Date: 13.04.12
 * Time: 00:54
 * To change this template use File | Settings | File Templates.
 */
public interface IUniverses
{
	String getInventoryName(String world);

	boolean isDifferentUniverse(World world, World from);
}
