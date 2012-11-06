package no.runsafe.UniqueInventories;

import no.runsafe.framework.server.RunsafeWorld;

public interface IUniverses
{
	String getInventoryName(String world);

	boolean isDifferentUniverse(RunsafeWorld world, RunsafeWorld from);
}
