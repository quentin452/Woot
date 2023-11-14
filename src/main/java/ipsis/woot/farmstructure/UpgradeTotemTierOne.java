package ipsis.woot.farmstructure;

import ipsis.woot.util.EnumSpawnerUpgrade;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.math.ChunkCoordinates;
import net.minecraft.world.World;

/**
 * Totem can only be one tall and must be a tier 1 upgrade
 */
public class UpgradeTotemTierOne extends AbstractUpgradeTotem {

    @Override
    public void scan() {

        if (!world.isBlockLoaded(origin))
            return;

        EnumSpawnerUpgrade baseUpgrade = TotemHelper.getUpgrade(world, origin, 1);
        if (baseUpgrade == null)
            return;

        if (TotemHelper.getTier(world, origin, 1) != 1)
            return;

        spawnerUpgrade = baseUpgrade;
        spawnerUpgradeLevel = 1;
        ChunkCoordinatesList.add(new ChunkCoordinates(origin));
    }

    public UpgradeTotemTierOne(World world, ChunkCoordinates pos) {
        super(world, pos);
    }
}
