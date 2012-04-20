package no.runsafe.UniqueInventories;

import no.runsafe.framework.server.RunsafeWorld;

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

	boolean isDifferentUniverse(RunsafeWorld world, RunsafeWorld from);
}
