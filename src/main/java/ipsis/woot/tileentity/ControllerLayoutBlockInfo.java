package ipsis.woot.tileentity;

import net.minecraft.util.math.ChunkCoordinates;

public class ControllerLayoutBlockInfo implements  ILayoutBlockInfo {

    ChunkCoordinates ChunkCoordinates;

    public ControllerLayoutBlockInfo(ChunkCoordinates pos) {

        this.ChunkCoordinates = pos;
    }

    @Override
    public ChunkCoordinates getPos() {
        return ChunkCoordinates;
    }

    @Override
    public void offsetY(int offset) {

        this.ChunkCoordinates = ChunkCoordinates.up(offset);
    }
}
