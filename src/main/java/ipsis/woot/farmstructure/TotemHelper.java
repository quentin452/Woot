package ipsis.woot.farmstructure;

import ipsis.woot.farmblocks.IFarmBlockUpgrade;
import ipsis.woot.util.EnumSpawnerUpgrade;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.ChunkCoordinates;
import net.minecraft.world.World;

public class TotemHelper {

    public static EnumSpawnerUpgrade getUpgrade(World world, ChunkCoordinates origin, int tier) {

        ChunkCoordinates ChunkCoordinates = origin.up(tier - 1);
        TileEntity te = world.getTileEntity(ChunkCoordinates);
        if (te instanceof IFarmBlockUpgrade)
            return ((IFarmBlockUpgrade) te).getUpgrade();

        return null;
    }

    public static int getTier(World world, ChunkCoordinates origin, int tier) {

        ChunkCoordinates ChunkCoordinates = origin.up(tier - 1);
        TileEntity te = world.getTileEntity(ChunkCoordinates);
        if (te instanceof IFarmBlockUpgrade)
            return ((IFarmBlockUpgrade) te).getUpgrade().getTier();

        return -1;
    }
}
