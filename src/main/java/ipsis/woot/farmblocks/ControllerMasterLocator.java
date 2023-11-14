package ipsis.woot.farmblocks;

import ipsis.Woot;
import ipsis.woot.util.DebugSetup;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.ChunkCoordinates;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ControllerMasterLocator implements IFarmBlockMasterLocator {


    @Nullable
    @Override
    public IFarmBlockMaster findMaster(World world, ChunkCoordinates origin, IFactoryGlueProvider iFactoryGlueProvider) {

        Woot.debugSetup.trace(DebugSetup.EnumDebugType.FARM_SCAN, "findMaster(Controller)", origin);

        StructureMasterLocator structureMasterLocator = new StructureMasterLocator();

        ChunkCoordinates ChunkCoordinates = origin.down(1);
        TileEntity te = world.getTileEntity(ChunkCoordinates);
        if (te instanceof IFactoryGlueProvider && ((IFactoryGlueProvider) te).getIFactoryGlue().getType() == IFactoryGlue.FactoryBlockType.STRUCTURE) {

            Woot.debugSetup.trace(DebugSetup.EnumDebugType.FARM_SCAN, "IFarmBlockStructre(Controller)", ChunkCoordinates);
            return structureMasterLocator.findMaster(world, ChunkCoordinates, (IFactoryGlueProvider) te);
        }

        return null;
    }
}
