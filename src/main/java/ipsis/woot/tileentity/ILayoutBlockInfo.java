package ipsis.woot.tileentity;

import net.minecraft.util.math.ChunkCoordinates;

public interface ILayoutBlockInfo {

    ChunkCoordinates getPos();
    void offsetY(int offset);
}
