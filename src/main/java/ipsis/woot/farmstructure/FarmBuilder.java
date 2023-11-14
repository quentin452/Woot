package ipsis.woot.farmstructure;

import ipsis.Woot;
import ipsis.woot.block.BlockMobFactoryHeart;
import ipsis.woot.farmblocks.IFactoryGlue;
import ipsis.woot.farmblocks.IFactoryGlueProvider;
import ipsis.woot.util.DebugSetup;
import ipsis.woot.util.EnumEnchantKey;
import ipsis.woot.util.EnumFarmUpgrade;
import ipsis.woot.farming.ITickTracker;
import ipsis.woot.farmblocks.IFarmBlockMaster;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FarmBuilder implements IFarmStructure {

    private boolean farmDirty = false;
    private boolean proxyDirty = false;
    private World world;
    private ChunkCoordinates origin;

    private boolean changed = false;
    private ScannedFarm2 currFarm;

    public FarmBuilder() {

    }

    private void disconnectOldFarm(@Nullable ScannedFarm2 oldFarm, ScannedFarm2 newFarm) {

        if (oldFarm == null)
            return;

        Set<ChunkCoordinates> oldBlocks = new HashSet<>();
        oldBlocks.addAll((Collection<? extends ChunkCoordinates>) oldFarm.base.getBlocks());
        oldBlocks.add(oldFarm.controller.getBlocks());
        oldBlocks.addAll((Collection<? extends ChunkCoordinates>) oldFarm.upgrades.getBlocks());
        oldBlocks.addAll((Collection<? extends ChunkCoordinates>) oldFarm.remote.getBlocks());

        Set<ChunkCoordinates> newBlocks = new HashSet<>();
        if (newFarm != null) {
            newBlocks.addAll((Collection<? extends ChunkCoordinates>) newFarm.base.getBlocks());
            newBlocks.add(newFarm.controller.getBlocks());
            newBlocks.addAll((Collection<? extends ChunkCoordinates>) newFarm.upgrades.getBlocks());
            newBlocks.addAll((Collection<? extends ChunkCoordinates>) newFarm.remote.getBlocks());
        }

        oldBlocks.removeAll(newBlocks);
        for (ChunkCoordinates pos : oldBlocks) {
            if (world.isBlockLoaded(pos)) {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof IFactoryGlueProvider) {
                    Woot.debugSetup.trace(DebugSetup.EnumDebugType.FARM_CLIENT_SYNC, "clearMaster", pos);
                    ((IFactoryGlueProvider) te).getIFactoryGlue().clearMaster();
                }
            }
        }
    }

    private void connectNewFarm(ScannedFarm2 oldFarm, ScannedFarm2 newFarm) {

        IFarmBlockMaster master = (IFarmBlockMaster)world.getTileEntity(origin);
        Set<ChunkCoordinates> oldBlocks = new HashSet<>();

        if (oldFarm != null) {
            oldBlocks.addAll((Collection<? extends ChunkCoordinates>) oldFarm.base.getBlocks());
            oldBlocks.add(oldFarm.controller.getBlocks());
            oldBlocks.addAll((Collection<? extends ChunkCoordinates>) oldFarm.upgrades.getBlocks());
            oldBlocks.addAll((Collection<? extends ChunkCoordinates>) oldFarm.remote.getBlocks());
        }

        Set<ChunkCoordinates> newBlocks = new HashSet<>();
        newBlocks.addAll((Collection<? extends ChunkCoordinates>) newFarm.base.getBlocks());
        newBlocks.add(newFarm.controller.getBlocks());
        newBlocks.addAll((Collection<? extends ChunkCoordinates>) newFarm.upgrades.getBlocks());
        newBlocks.addAll((Collection<? extends ChunkCoordinates>) newFarm.remote.getBlocks());

        newBlocks.removeAll(oldBlocks);
        for (ChunkCoordinates pos : newBlocks) {
            if (world.isBlockLoaded(pos)) {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof  IFactoryGlueProvider && ((IFactoryGlueProvider) te).getIFactoryGlue().getType() == IFactoryGlue.FactoryBlockType.UPGRADE)
                    Woot.debugSetup.trace(DebugSetup.EnumDebugType.FARM_BUILD, "connectNewFarm: connecting upgrade", "");
                if (te instanceof IFactoryGlueProvider) {
                    Woot.debugSetup.trace(DebugSetup.EnumDebugType.FARM_CLIENT_SYNC, "setMaster", pos);
                    ((IFactoryGlueProvider) te).getIFactoryGlue().setMaster(master);
                }
            }
        }
    }

    private @Nullable ScannedFarm2 scanFarm() {

        EnumFacing facing = world.getBlock(origin).getValue(BlockMobFactoryHeart.FACING);
        FarmScanner2 farmScanner = new FarmScanner2();
        ScannedFarm2 scannedFarm = farmScanner.scanFarm(world, origin, facing);

        if (!scannedFarm.isValidStructure() || !scannedFarm.isValidCofiguration(world)) {
            Woot.debugSetup.trace(DebugSetup.EnumDebugType.FARM_BUILD, "scanFullFarm: invalid farm", "");
            return null;
        }

        return scannedFarm;
    }

    private void handleDirtyFarm() {

        ScannedFarm2 scannedFarm = scanFarm();

        if (currFarm == null && scannedFarm == null) {
            // NA
        } else if (currFarm == null && scannedFarm != null) {

            Woot.debugSetup.trace(DebugSetup.EnumDebugType.FARM_BUILD, "handleDirtyFarm: new farm", "");
            connectNewFarm(currFarm, scannedFarm);
            currFarm = scannedFarm;
            changed = true;
        } else if (currFarm != null && scannedFarm == null) {

            Woot.debugSetup.trace(DebugSetup.EnumDebugType.FARM_BUILD, "handleDirtyFarm: goodbye farm", "");
            disconnectOldFarm(currFarm, scannedFarm);
            currFarm = null;
        } else if (currFarm != null && scannedFarm != null) {

            if (!ScannedFarm2.areFarmsEqual(currFarm, scannedFarm)) {
                Woot.debugSetup.trace(DebugSetup.EnumDebugType.FARM_BUILD, "handleDirtyFarm: changed farm", "");
                disconnectOldFarm(currFarm, scannedFarm);
                connectNewFarm(currFarm, scannedFarm);
                currFarm = scannedFarm;
                changed = true;
            }
        }
    }

    private void handleDirtyProxy() {

        handleDirtyFarm();
    }

    /**
     * IFarmStructure
     */

    @Override
    public void setStructureDirty() {

        farmDirty = true;
    }

    @Override
    public void setUpgradeDirty() {

        setStructureDirty();
    }

    @Override
    public IFarmStructure setWorld(@Nonnull World world) {

        this.world = world;
        return this;
    }

    @Override
    public IFarmStructure setPosition(ChunkCoordinates origin) {

        this.origin = origin;
        return this;
    }

    @Override
    public void tick(ITickTracker tickTracker) {

        if (tickTracker.hasStructureTickExpired()) {
            if (farmDirty) {
                handleDirtyFarm();
                tickTracker.resetStructureTickCount();
                farmDirty = false;
            } else if (proxyDirty) {
                handleDirtyProxy();
                tickTracker.resetStructureTickCount();
                proxyDirty = false;
            }
        }
    }

    /**
     * This should only ever be called when the farm is formed
     */
    @Override
    public IFarmSetup createSetup() {

        IFarmSetup farmSetup = new FarmSetup(world, currFarm.controller.wootMob);
        farmSetup.setFarmTier(currFarm.base.tier);
        for (ScannedFarmUpgrade.Upgrade u : currFarm.upgrades.getUpgrades())
            farmSetup.setUpgradeLevel(u.upgrade, u.upgradeTier);

        int level = farmSetup.getUpgradeLevel(EnumFarmUpgrade.LOOTING);
        if (level == 1)
            farmSetup.setEnchantKey(EnumEnchantKey.LOOTING_I);
        else if (level == 2)
            farmSetup.setEnchantKey(EnumEnchantKey.LOOTING_II);
        else if (level == 3)
            farmSetup.setEnchantKey(EnumEnchantKey.LOOTING_III);

        farmSetup.setFacing(world.getBlock(origin).getValue(BlockMobFactoryHeart.FACING));

        farmSetup.setPowerCellChunkCoordinates(currFarm.remote.getPowerPos());
        farmSetup.setExportChunkCoordinates(currFarm.remote.getExportPos());
        farmSetup.setImportChunkCoordinates(currFarm.remote.getImportPos());

        return farmSetup;
    }

    @Override
    public boolean isFormed() {

        return currFarm != null;
    }

    @Override
    public boolean hasChanged() {

        return changed;
    }

    @Override
    public void clearChanged() {

        this.changed = false;
    }

    @Override
    public void fullDisconnect() {

        if (currFarm == null)
            return;

        Set<ChunkCoordinates> oldBlocks = new HashSet<>();
        oldBlocks.addAll(currFarm.base.getBlocks());
        oldBlocks.add(currFarm.controller.getBlocks());
        oldBlocks.addAll(currFarm.upgrades.getBlocks());
        oldBlocks.addAll(currFarm.remote.getBlocks());

        for (ChunkCoordinates pos : oldBlocks) {
            if (world.isBlockLoaded(pos)) {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof IFactoryGlueProvider) {
                    Woot.debugSetup.trace(DebugSetup.EnumDebugType.FARM_CLIENT_SYNC, "clearMaster", pos);
                    ((IFactoryGlueProvider) te).getIFactoryGlue().clearMaster();
                }
            }
        }
    }
}
