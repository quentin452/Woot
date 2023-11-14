package ipsis.woot.oss;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;

/**
 * These are the functions from ChunkCoordinates in CofhLIB
 */
public class BlockPosHelper {

    public static ChunkCoordinates moveRight(ChunkCoordinates p, EnumFacing f, int step) {

        switch (f) {
            case UP:
            case SOUTH:
                return new ChunkCoordinates(p.posX - step, p.posY, p.posZ);
            case DOWN:
            case NORTH:
                return new ChunkCoordinates(p.posX + step, p.posY, p.posZ);
            case EAST:
                return new ChunkCoordinates(p.posX, p.posY, p.posZ + step);
            case WEST:
                return new ChunkCoordinates(p.posX, p.posY, p.posZ - step);
            default:
                break;
        }

        // Not possible
        return null;
    }

    public static ChunkCoordinates moveLeft(ChunkCoordinates p, EnumFacing f, int step) {

        return moveRight(p, f, -step);
    }

    public static ChunkCoordinates moveForwards(ChunkCoordinates p, EnumFacing f, int step) {

        switch (f) {
            case UP:
                return new ChunkCoordinates(p.posX, p.posY + step, p.posZ);
            case DOWN:
                return new ChunkCoordinates(p.posX, p.posY - step, p.posZ);
            case SOUTH:
                return new ChunkCoordinates(p.posX, p.posY, p.posZ + step);
            case NORTH:
                return new ChunkCoordinates(p.posX, p.posY, p.posZ - step);
            case EAST:
                return new ChunkCoordinates(p.posX + step, p.posY, p.posZ);
            case WEST:
                return new ChunkCoordinates(p.posX - step, p.posY, p.posZ);
            default:
                break;
        }

        // Not possible
        return null;
    }

    public static ChunkCoordinates moveBackwards(ChunkCoordinates p, EnumFacing f, int step) {

        return moveForwards(p, f, -step);
    }
}
