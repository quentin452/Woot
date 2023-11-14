package ipsis.woot.loot;

import ipsis.woot.farmstructure.IFarmSetup;
import ipsis.woot.farming.ITickTracker;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public interface ILootLearner {

    void tick(ITickTracker tickTracker, World world, ChunkCoordinates origin, IFarmSetup farmSetup);
}
