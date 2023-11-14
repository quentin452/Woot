package ipsis.woot.farmblocks;

import net.minecraft.util.math.ChunkCoordinates;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IFarmBlockMasterLocator {

    @Nullable  IFarmBlockMaster findMaster(World world, ChunkCoordinates origin, IFactoryGlueProvider iFactoryGlueProvider);
}
