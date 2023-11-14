package ipsis.woot.farming;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidHandler;


import javax.annotation.Nullable;
import java.util.List;

public interface ISpawnRecipeConsumer {

    /**
     * Simulate == true does not remove any items or fluids
     */
    boolean consume(World world, ChunkCoordinates pos, List<IFluidHandler> fluidHandlerList, List<IItemHandler> itemHandlerList, @Nullable ISpawnRecipe spawnRecipe, int mobCount, boolean simulate);
}
