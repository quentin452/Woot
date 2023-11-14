package ipsis.woot.farmstructure;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class AbstractUpgradeTotemBuilder {

    public static @Nullable
    AbstractUpgradeTotem build(Class clazz, World world, ChunkCoordinates pos) {

        if (clazz == UpgradeTotemTierOne.class)
            return new UpgradeTotemTierOne(world, pos);
        else if (clazz == UpgradeTotemTierTwo.class)
            return new UpgradeTotemTierTwo(world, pos);
        else if (clazz == UpgradeTotemTierThree.class)
            return new UpgradeTotemTierThree(world, pos);

        return null;
    }
}
