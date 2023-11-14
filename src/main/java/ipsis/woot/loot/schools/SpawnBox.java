package ipsis.woot.loot.schools;

import ipsis.Woot;
import ipsis.woot.util.DebugSetup;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.ChunkCoordinates;

public class SpawnBox {

    private ChunkCoordinates basePos;
    private ChunkCoordinates spawnPos;
    private boolean used = false;
    private AxisAlignedBB axisAlignedBB;

    public SpawnBox(ChunkCoordinates basePos, ChunkCoordinates spawnPos) {
        this.basePos = new ChunkCoordinates(basePos);
        this.spawnPos = new ChunkCoordinates(spawnPos);

        Woot.debugSetup.trace(DebugSetup.EnumDebugType.TARTARUS, "SpawnBox", "BasePos:" + basePos + " SpawnPos:" + spawnPos);
    }

    public void setUsed() {
        this.used = true;

        if (axisAlignedBB == null) {
            // Spawn box is internal 6x6x6
            int range = 3;
            axisAlignedBB = new AxisAlignedBB(spawnPos).grow(range);
        }
    }

    public void clearUsed() {
        this.used = false;
        axisAlignedBB = null;
    }

    public boolean isUsed() {
        return this.used;
    }

    public ChunkCoordinates getBasePos() {
        return this.basePos;
    }
    public ChunkCoordinates getSpawnPos() { return this.spawnPos; }

    public AxisAlignedBB getAxisAlignedBB() {

        return axisAlignedBB;
    }

    @Override
    public String toString() {

        return "Base: " + basePos + "Spawn: " + spawnPos + " Used:" + used + " BB:" + axisAlignedBB;
    }
}
