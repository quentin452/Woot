package ipsis.woot.farmstructure;

import ipsis.woot.util.EnumSpawnerUpgrade;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractUpgradeTotem {

    EnumSpawnerUpgrade spawnerUpgrade;
    int spawnerUpgradeLevel;
    Set<ChunkCoordinates> ChunkCoordinatesList;
    ChunkCoordinates origin;
    World world;

    public boolean isValid() {

        return spawnerUpgrade != null && spawnerUpgradeLevel != 0;
    }

    AbstractUpgradeTotem(@Nonnull World world, @Nonnull ChunkCoordinates origin) {

        spawnerUpgrade = null;
        spawnerUpgradeLevel = 0;
        ChunkCoordinatesList = new HashSet<>();
        this.world = world;
        this.origin = origin;
    }

    abstract public void scan();
}
