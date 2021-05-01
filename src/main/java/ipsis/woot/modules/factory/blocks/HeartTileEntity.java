package ipsis.woot.modules.factory.blocks;

import ipsis.woot.Woot;
import ipsis.woot.fluilds.FluidSetup;
import ipsis.woot.mod.ModNBT;
import ipsis.woot.modules.factory.*;
import ipsis.woot.modules.factory.calculators.CalculatorVersion2;
import ipsis.woot.modules.factory.client.ClientFactorySetup;
import ipsis.woot.modules.factory.generators.LootGeneration;
import ipsis.woot.modules.factory.layout.Layout;
import ipsis.woot.modules.factory.multiblock.MultiBlockMaster;
import ipsis.woot.modules.factory.network.HeartStaticDataReply;
import ipsis.woot.modules.factory.perks.Perk;
import ipsis.woot.simulator.MobSimulator;
import ipsis.woot.util.FakeMob;
import ipsis.woot.util.WootDebug;
import ipsis.woot.util.helper.StorageHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The factory is formed manually by the user via the intern -> interrupt
 * When an attached block is removed or unloaded it should inform the heart -> interrupt
 */
public class HeartTileEntity extends TileEntity implements ITickableTileEntity, MultiBlockMaster, WootDebug, INamedContainerProvider {

    static final Logger LOGGER = LogManager.getLogger();

    /**
     * Layout will not exist until after the first update call
     * Setup will only exist if the layout is formed
     */
    Layout layout;
    FormedSetup formedSetup;
    HeartRecipe recipe;
    TickTracker tickTracker = new TickTracker();
    boolean loadedFromNBT = false;

    public HeartTileEntity() {
        super(FactorySetup.HEART_BLOCK_TILE.get());
        tickTracker.setStructureTickCount(20);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (layout != null)
            layout.fullDisconnect();
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (layout != null)
            layout.fullDisconnect();
    }

    public boolean isRunning() {
        return !level.hasNeighborSignal(worldPosition);
    }

    public boolean isFormed() { return layout != null && layout.isFormed(); }

    @Override
    public void tick() {
        if (level == null)
            return;

       if (level.isClientSide)
           return;

       if (layout == null) {
           layout = new Layout();
           layout.setLocation(level, worldPosition, level.getBlockState(worldPosition).getValue(BlockStateProperties.HORIZONTAL_FACING));
           layout.setDirty();
       }

       // Check for tick acceleration
       if (!tickTracker.tick(level))
           return;

       layout.tick(tickTracker, this);
       if (layout.isFormed()) {
           if (layout.hasChanged()) {
               formedSetup = FormedSetup.createFromValidLayout(level, layout);
               LOGGER.debug("formedSetup: {}", formedSetup);

               formedSetup.getAllMobs().forEach(m -> MobSimulator.getInstance().learn(m));
               recipe = CalculatorVersion2.calculate(formedSetup);

               // Restore the progress on load
               if (loadedFromNBT) {
                   loadedFromNBT = false;
               } else {
                   consumedUnits = 0;
                   setChanged();
               }

               layout.clearChanged();
           }

           if (!isRunning())
               return;

           tickRecipe();
           if (consumedUnits >= recipe.getNumTicks()) {
               // get and process the ingredients
               consumedUnits = 0;
               setChanged();

               List<ItemStack> items = createItemIngredients(recipe, formedSetup);
               List<FluidStack> fluids = createFluidIngredients(recipe, formedSetup);

               if (hasItemIngredients(items, formedSetup) && hasFluidIngredients(fluids, formedSetup)) {
                   LazyOptional<IFluidHandler> hdlr = formedSetup.getCellFluidHandler();
                   if (hdlr.isPresent()) {
                       IFluidHandler iFluidHandler = hdlr.orElseThrow(NullPointerException::new);
                       FluidStack fluidStack = iFluidHandler.drain(recipe.getNumUnits(), IFluidHandler.FluidAction.SIMULATE);
                       if (fluidStack.getAmount() == recipe.getNumUnits()) {
                           //LOGGER.debug("Generate loot");
                           consumeItemIngredients(items, formedSetup);
                           consumeFluidIngredients(fluids, formedSetup);
                           iFluidHandler.drain(recipe.getNumUnits(), IFluidHandler.FluidAction.EXECUTE);
                           LootGeneration.get().generate(this, formedSetup);
                       }
                   }
               }
           }
       }
    }

    private List<ItemStack> createItemIngredients(HeartRecipe recipe, FormedSetup formedSetup) {
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack itemStack : recipe.recipeItems)
            items.add(itemStack.copy());
        return items;
    }

    private List<FluidStack> createFluidIngredients(HeartRecipe recipe, FormedSetup formedSetup) {
        List<FluidStack> fluids = new ArrayList<>();
        for (FluidStack fluidStack : recipe.recipeFluids)
            fluids.add(fluidStack.copy());
        return fluids;
    }

    private boolean hasItemIngredients(List<ItemStack> items, FormedSetup formedSetup) {
        if (items.isEmpty())
            return true;

        for (ItemStack itemStack : items) {
            int count = StorageHelper.getCount(itemStack, formedSetup.getImportHandlers());
            if (count == 0 || count < itemStack.getCount())
                return false;
        }

        return true;
    }

    private boolean hasFluidIngredients(List<FluidStack> fluids, FormedSetup formedSetup) {
        if (fluids.isEmpty())
            return true;

        for (FluidStack fluidStack : fluids) {
            int amount = StorageHelper.getAmount(fluidStack, formedSetup.getImportFluidHandlers());
            if (amount == 0 || amount < fluidStack.getAmount())
                return false;
        }

        return true;
    }

    private void consumeItemIngredients(List<ItemStack> items, FormedSetup formedSetup) {
        if (items.isEmpty())
            return;

        for (LazyOptional<IItemHandler> hdlr : formedSetup.getImportHandlers()) {
            if (items.isEmpty())
                break;

            hdlr.ifPresent(h -> {
                for (ItemStack itemStack : items) {
                    if (itemStack.isEmpty())
                        continue;

                    Woot.setup.getLogger().debug("consumeItemIngredients: to consume {}", itemStack);

                    for (int slot = 0; slot < h.getSlots(); slot++) {
                        ItemStack slotStack = h.getStackInSlot(slot);
                        if (!slotStack.isEmpty() && ItemStack.isSame(itemStack, slotStack)) {
                            Woot.setup.getLogger().debug("consumeItemIngredients: slot {} consume {}", slot, itemStack.getCount());
                            ItemStack extractedStack = h.extractItem(slot, itemStack.getCount(), false);
                            if (!extractedStack.isEmpty())
                                itemStack.shrink(extractedStack.getCount());
                        }
                    }
                }
            });
        }
    }

    private void consumeFluidIngredients(List<FluidStack> fluids, FormedSetup formedSetup) {
        if (fluids.isEmpty())
            return;

        for (LazyOptional<IFluidHandler> hdlr : formedSetup.getImportFluidHandlers()) {
            if (fluids.isEmpty())
                break;

            hdlr.ifPresent(h -> {
                for (FluidStack fluidStack : fluids) {
                    if (fluidStack.isEmpty())
                        continue;

                    Woot.setup.getLogger().debug("consumeFluidIngredients: to consume {}", fluidStack);

                    FluidStack drainedStack = h.drain(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                    int consumed = drainedStack.getAmount();
                    fluidStack.setAmount(fluidStack.getAmount() - consumed);
                    if (fluidStack.getAmount() < 0)
                        fluidStack.setAmount(0);
                    Woot.setup.getLogger().debug("consumeFluidIngredients: consumed {}", consumed);
                }
            });
        }
    }

    /**
     * NBT
     */

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        super.deserializeNBT(compound);
        readFromNBT(compound);
    }

    @Override
    public void load(BlockState blockState, CompoundNBT compoundNBT) {
        super.load(blockState, compoundNBT);
        readFromNBT(compoundNBT);
    }

    private void readFromNBT(CompoundNBT compound) {
        if (compound.contains(ModNBT.Heart.PROGRESS_TAG)) {
            loadedFromNBT = true;
            consumedUnits = compound.getInt(ModNBT.Heart.PROGRESS_TAG);
            LOGGER.debug("read: loading progress " + consumedUnits);
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        super.save(compound);
        if (isFormed() && recipe != null) {
            compound.putInt(ModNBT.Heart.PROGRESS_TAG, consumedUnits);
            LOGGER.debug("write: saving progress " + consumedUnits);
        }
        return compound;
    }

    /**
     * For BloodMagic
     */
    public boolean couldFinish() {
        /**
         * Enough Conatus in the tank to complete the spawn and we are running
         */

        if (!isRunning() || !isFormed())
            return false;

        return formedSetup.getCellFluidAmount() >= recipe.getNumUnits();
    }

    public boolean hasFlayedPerk() {
        if (!isFormed())
            return false;
        return formedSetup.getAllPerks().containsKey(Perk.Group.FLAYED);
    }

    /**
     * MultiBlockMaster
     */
    @Override
    public void interrupt() {
        //LOGGER.debug("interrupt layout:" + layout);
        if (layout != null)
            layout.setDirty();
    }

    /**
     * Recipe handling
     */
    int consumedUnits = 0;
    void tickRecipe() {
        // Purely the passage of time
        consumedUnits++;
        setChanged();
    }

    /**
     * Tick Tracker
     */
    public class TickTracker {
        long lastGameTime = -1;
        int structureTicksTimeout = 0;
        int currStructureTicks = 0;

        public boolean tick(World world) {

            boolean realTick = false;
            long currGameTime = world.getGameTime();
            if (FactoryConfiguration.TICK_ACCEL.get() || lastGameTime != currGameTime) {
                // actual time has passed - no acceleration
                lastGameTime = currGameTime;
                realTick = true;

                if (structureTicksTimeout > 0)
                    currStructureTicks++;
            }

            return realTick;
        }

        public boolean hasStructureTickExpired() {
            return structureTicksTimeout > 0 && currStructureTicks >= structureTicksTimeout;
        }

        public void setStructureTickCount(int ticks) {
            structureTicksTimeout = ticks;
        }

        public void resetStructureTickCount() {
            currStructureTicks = 0;
        }
    }


    /**
     * WootDebug
     */
    @Override
    public List<String> getDebugText(List<String> debug, ItemUseContext itemUseContext) {
        debug.add("====> HeartTileEntity");
        debug.add("      layout: " + layout);
        debug.add("      setup: " + formedSetup);
        debug.add("      recipe: " + recipe);
        debug.add("      consumed: " + consumedUnits);
        return debug;
    }

    /**
     * INamedContainerProvider
     */
    @Override
    public ITextComponent getDisplayName() {
        if (isFormed())
            return new TranslationTextComponent(formedSetup.getTier().getTranslationKey());

        return new StringTextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new HeartContainer(windowId, level, worldPosition, playerInventory, playerEntity);
    }

    public int getFluidCapacity() {
        return formedSetup != null ? formedSetup.getCellCapacity() : 0;
    }
    public int getCellType() {
        return formedSetup != null ? formedSetup.getCellType() : 0;
    }
    public FluidStack getTankFluid() {
        return formedSetup != null ?
                new FluidStack(FluidSetup.CONATUS_FLUID.get(), formedSetup.getCellFluidAmount()) :
                FluidStack.EMPTY;
    }

    public int getProgress() {
        if (formedSetup == null)
            return 0;

        return (int)((100.0F / recipe.getNumTicks() * consumedUnits));
    }

    public List<FakeMob> getFormedMobs() {
        List<FakeMob> mobs = new ArrayList<>();
        if (isFormed())
            mobs.addAll(formedSetup.getAllMobs());
        return mobs;
    }

    public Map<Perk.Group, Integer> getPerks() {
        Map<Perk.Group, Integer> perks = new HashMap<>();
        if (isFormed())
            perks.putAll(formedSetup.getAllPerks());
        return perks;
    }

    @Nullable
    public FormedSetup getFormedSetup() {
        return formedSetup;
    }

    public Tier getTier() {
        return isFormed() ? formedSetup.getTier() : Tier.UNKNOWN;
    }

    public Exotic getExotic() {
        return isFormed() ? formedSetup.getExotic() : Exotic.NONE;
    }

    @OnlyIn(Dist.CLIENT)
    public ClientFactorySetup clientFactorySetup;

    @OnlyIn(Dist.CLIENT)
    public void setClientFactorySetup(ClientFactorySetup clientFactorySetup) {
        this.clientFactorySetup = clientFactorySetup;
    }

    public HeartStaticDataReply createStaticDataReply2() {
        return new HeartStaticDataReply(formedSetup, recipe);
    }
}
