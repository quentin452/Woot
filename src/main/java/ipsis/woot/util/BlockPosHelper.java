package ipsis.woot.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;

/**
 * South +Z
 * North -Z
 * East +X
 * West -X
 *
 * When facing south, left is east (+z), right is west (-z)
 *
 * These functions are only to support rotating the factory patterns which are all based around facing south
 */

public class BlockPosHelper {

    public static ChunkCoordinates rotateToSouth(ChunkCoordinates ChunkCoordinates, EnumFacing from) {

        if (from == EnumFacing.EAST)
            return new ChunkCoordinates(ChunkCoordinates.posZ * -1, ChunkCoordinates.posY, ChunkCoordinates.posX);
        else if (from == EnumFacing.NORTH)
            return new ChunkCoordinates(ChunkCoordinates.posX * -1, ChunkCoordinates.posY, ChunkCoordinates.posZ * -1);
        else if (from == EnumFacing.WEST)
            return new ChunkCoordinates(ChunkCoordinates.posZ, ChunkCoordinates.posY, ChunkCoordinates.posX * -1);
        else
            return new ChunkCoordinates(ChunkCoordinates);
    }

    public static ChunkCoordinates rotateFromSouth(ChunkCoordinates ChunkCoordinates, EnumFacing to) {

        if (to == EnumFacing.EAST)
            return new ChunkCoordinates(ChunkCoordinates.posZ, ChunkCoordinates.posY, ChunkCoordinates.posX * -1);
        else if (to == EnumFacing.WEST)
            return new ChunkCoordinates(ChunkCoordinates.posZ * -1, ChunkCoordinates.posY, ChunkCoordinates.posX);
        else if (to == EnumFacing.NORTH)
            return new ChunkCoordinates(ChunkCoordinates.posX * -1, ChunkCoordinates.posY, ChunkCoordinates.posZ * -1);
        else
            return new ChunkCoordinates(ChunkCoordinates);
    }


    public static void writeToNBT(ChunkCoordinates p, NBTTagCompound compound) {

        compound.setInteger("xCoord", p.posX);
        compound.setInteger("yCoord", p.posY);
        compound.setInteger("zCoord", p.posZ);
    }

    public static ChunkCoordinates readFromNBT(NBTTagCompound compound) {

        return new ChunkCoordinates(compound.getInteger("xCoord"), compound.getInteger("yCoord"), compound.getInteger("zCoord"));
    }
}
