package ipsis.woot.farmblocks;

import net.minecraft.util.math.ChunkCoordinates;
import net.minecraft.world.World;

public interface IFactoryGlue {

    void clearMaster();
    void setMaster(IFarmBlockMaster master);
    void onHello(World world, ChunkCoordinates pos);
    void onGoodbye();
    boolean hasMaster();
    ChunkCoordinates getPos();

    FactoryBlockType getType();

    enum FactoryBlockType {
        STRUCTURE,
        CONTROLLER,
        UPGRADE,
        CELL,
        IMPORTER,
        EXPORTER
    }
}
