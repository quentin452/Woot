package ipsis.woot.farmblocks;

import ipsis.Woot;
import ipsis.woot.util.DebugSetup;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.ChunkCoordinates;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Walks upwards until it finds the master
 */
public class SimpleMasterLocator implements IFarmBlockMasterLocator {

    @Nullable
    @Override
    public IFarmBlockMaster findMaster(World world, ChunkCoordinates origin, IFactoryGlueProvider iFactoryGlueProvider) {

        Woot.debugSetup.trace(DebugSetup.EnumDebugType.FARM_SCAN, "findMaster(Proxy)", origin);

        IFarmBlockMaster tmpMaster = null;

        for (int step = 0; step < 10; step++) {

            ChunkCoordinates ChunkCoordinates = origin.up(step + 1);
            TileEntity te = world.getTileEntity(ChunkCoordinates);
            if (te instanceof IFarmBlockMaster) {
                Woot.debugSetup.trace(DebugSetup.EnumDebugType.FARM_SCAN, "IFarmMaster", ChunkCoordinates);
                tmpMaster = (IFarmBlockMaster) te;
            }
        }

        return tmpMaster;
    }
}
