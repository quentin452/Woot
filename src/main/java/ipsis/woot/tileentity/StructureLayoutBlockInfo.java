package ipsis.woot.tileentity;

import ipsis.woot.multiblock.EnumMobFactoryModule;
import net.minecraft.util.math.ChunkCoordinates;

public class StructureLayoutBlockInfo implements ILayoutBlockInfo {

    public ChunkCoordinates ChunkCoordinates;
    public EnumMobFactoryModule module;

    public StructureLayoutBlockInfo(ChunkCoordinates ChunkCoordinates, EnumMobFactoryModule module) {

        this.ChunkCoordinates = ChunkCoordinates;
        this.module = module;
    }

    @Override
    public String toString() {

        return this.ChunkCoordinates + ":" + module;
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
