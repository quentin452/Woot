package ipsis.woot.farmstructure;

import ipsis.Woot;
import ipsis.woot.multiblock.EnumMobFactoryTier;
import ipsis.woot.util.WootMob;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.math.ChunkCoordinates;
import net.minecraft.world.World;

public class ScannedFarmController {

    private ChunkCoordinates pos;
    WootMob wootMob;

    public static boolean isEqual(ScannedFarmController a, ScannedFarmController b) {

        if (a == null || b == null)
            return false;

        if (!a.pos.equals(b.pos))
            return false;

        return a.wootMob.getWootMobName().equals(b.wootMob.getWootMobName());
    }

    public boolean isValid() {

        return isPresent() && isValidMob();
    }

    // Is controller present
    public boolean isPresent() {

        if (pos == null || wootMob == null)
            return false;

        return true;
    }

    private boolean isValidMob() {

        if (wootMob == null)
            return false;

        if (!EntityList.isRegistered(wootMob.getWootMobName().getResourceLocation()))
            return false;

        if (!Woot.policyRepository.canGenerateFrom(wootMob.getWootMobName()))
            return false;

        return true;
    }

    public boolean canGenerateFrom() {

        return wootMob != null && Woot.policyRepository.canGenerateFrom(wootMob.getWootMobName());
    }

    public boolean isTierValid(World world, EnumMobFactoryTier tier) {

        if (wootMob == null)
            return false;

        EnumMobFactoryTier mobTier = Woot.wootConfiguration.getFactoryTier(world, wootMob.getWootMobName());
        return EnumMobFactoryTier.isLessThanOrEqual(mobTier, tier);
    }

    public ChunkCoordinates getBlocks() {

        return pos;
    }

    public void setBlocks(ChunkCoordinates pos) {

        this.pos = pos;
    }
}
