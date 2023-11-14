package ipsis.woot.multiblock;

import net.minecraft.util.math.ChunkCoordinates;

public class MobFactoryModule {

    ChunkCoordinates offset;
    EnumMobFactoryModule moduleType;

    public MobFactoryModule(ChunkCoordinates offset, EnumMobFactoryModule moduleType) {

        this.offset = offset;
        this.moduleType = moduleType;
    }

    /**
     * This always returns a new ChunkCoordinates
     */
    public ChunkCoordinates getOffsetBlock(ChunkCoordinates currPos) {

        return currPos.add(offset.getX(), offset.getY(), offset.getZ());
    }

    public EnumMobFactoryModule getModuleType() { return this.moduleType; }
    public ChunkCoordinates getOffset() { return this.offset; }

    @Override
    public String toString() {
        return offset + " : " + moduleType;
    }
}
