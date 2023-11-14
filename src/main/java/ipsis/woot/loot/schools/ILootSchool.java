package ipsis.woot.loot.schools;

import ipsis.woot.farmstructure.IFarmSetup;
import net.minecraft.util.math.ChunkCoordinates;
import net.minecraft.world.World;

public interface ILootSchool {

    void tick(World world, ChunkCoordinates origin, IFarmSetup farmSetup);
}
