package ipsis.woot.farmstructure;

import ipsis.woot.power.storage.IPowerStation;
import ipsis.woot.util.EnumEnchantKey;
import ipsis.woot.multiblock.EnumMobFactoryTier;
import ipsis.woot.util.EnumFarmUpgrade;
import ipsis.woot.util.WootMob;
import ipsis.woot.util.WootMobName;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.List;

public interface IFarmSetup {

    @Nonnull
    WootMob getWootMob();
    @Nonnull
    WootMobName getWootMobName();
    int getNumMobs();
    int getUpgradeLevel(EnumFarmUpgrade upgrade);
    boolean hasUpgrade(EnumFarmUpgrade upgrade);
    @Nonnull EnumEnchantKey getEnchantKey();
    @Nonnull EnumMobFactoryTier getFarmTier();
    void setUpgradeLevel(EnumFarmUpgrade upgrade, int level);
    void setFarmTier(EnumMobFactoryTier tier);
    void setEnchantKey(EnumEnchantKey key);
    void setStoredXp(int xp);
    int getStoredXp();


    void setPowerCellChunkCoordinates(ChunkCoordinates ChunkCoordinates);
    void setExportChunkCoordinates(ChunkCoordinates ChunkCoordinates);
    void setImportChunkCoordinates(ChunkCoordinates ChunkCoordinates);
    void setFacing(EnumFacing facing);
    EnumFacing getFacing();

    // TODO REMOVE THESE
    @Nonnull List<IFluidHandler> getConnectedImportTanks();
    @Nonnull List<IFluidHandler> getConnectedExportTanks();
    @Nonnull List<IItemHandler> getConnectedImportChests();
    @Nonnull List<IItemHandler> getConnectedExportChests();

    @Nonnull List<TileEntity> getConnectedImportTanksTiles();
    @Nonnull List<TileEntity> getConnectedExportTanksTiles();
    @Nonnull List<TileEntity> getConnectedImportChestsTiles();
    @Nonnull List<TileEntity> getConnectedExportChestsTiles();
    IPowerStation getPowerStation();
}
