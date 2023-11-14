package ipsis.woot.spawning;

import ipsis.woot.util.EnumEnchantKey;
import ipsis.woot.util.WootMobName;
import net.minecraft.util.math.ChunkCoordinates;
import net.minecraft.world.World;

public interface IEntitySpawner {

    void spawn(WootMobName wootMobName, EnumEnchantKey key, World world, ChunkCoordinates pos);
}
