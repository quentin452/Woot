package ipsis.woot.farmstructure;

import ipsis.Woot;
import ipsis.woot.block.BlockMobFactoryStructure;
import ipsis.woot.farmblocks.IFarmBlockController;
import ipsis.woot.init.ModBlocks;
import ipsis.woot.multiblock.EnumMobFactoryTier;
import ipsis.woot.multiblock.MobFactoryModule;
import ipsis.woot.tileentity.TileEntityMobFactoryCell;
import ipsis.woot.tileentity.TileEntityMobFactoryExporter;
import ipsis.woot.tileentity.TileEntityMobFactoryImporter;
import ipsis.woot.util.ChunkCoordinatesHelper;
import ipsis.woot.util.EnumFarmUpgrade;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkCoordinates;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Set;

public class FarmScanner2 {

    private BadFarmBlockInfo createBadFarmBlockInfo(MobFactoryModule m, BadBlockReason r, ChunkCoordinates p, Block badBlock, int badBlockMeta) {

        BadFarmBlockInfo i = new BadFarmBlockInfo(r, p);

        Block b = ModBlocks.blockStructure;
        int meta = m.getModuleType().getMetadata();
        i.setBlockInfo(b, meta, badBlock, badBlockMeta);
        return i;
    }

    private boolean scanTier(World world, ChunkCoordinates origin, EnumFacing facing, EnumMobFactoryTier tier, Set<BadFarmBlockInfo> badList, Set<ChunkCoordinates> goodList) {

        badList.clear();
        goodList.clear();

        boolean valid = true;
        for (MobFactoryModule m : Woot.factoryPatternRepository.getAllModules(tier)) {

            ChunkCoordinates p = new ChunkCoordinates(origin).add(ChunkCoordinatesHelper.rotateFromSouth(m.getOffset(), facing.getOpposite()));

            if (!world.isBlockLoaded(p)) {
                // Stop from loading the chunk to check the block
                badList.add(createBadFarmBlockInfo(m, BadBlockReason.MISSING_BLOCK, p, Blocks.AIR, 0));
                valid = false;
                continue;
            }

            IBlockState blockState = world.getBlockState(p);
            Block b = blockState.getBlock();

            if (!(b instanceof BlockMobFactoryStructure)) {
                badList.add(createBadFarmBlockInfo(m, BadBlockReason.WRONG_BLOCK, p, b, b.getMetaFromState(blockState)));
                valid = false;
                continue;
            }

            if (!(((BlockMobFactoryStructure)b).getModuleTypeFromState(blockState) == m.getModuleType())) {
                badList.add(createBadFarmBlockInfo(m, BadBlockReason.WRONG_STRUCTURE_TYPE, p, b, b.getMetaFromState(blockState)));
                valid = false;
                continue;
            }

            // Block must be valid
            goodList.add(p);
        }

        if (!valid)
            goodList.clear();

        return valid;
    }

    private @Nullable ChunkCoordinates findController(World world, ChunkCoordinates origin, EnumFacing facing) {

        ChunkCoordinates p = getControllerPos(world, origin, facing);
        TileEntity te = world.getTileEntity(p);
        if (te instanceof IFarmBlockController)
            return p;

        return null;
    }

    public ChunkCoordinates getControllerPos(World world, ChunkCoordinates origin, EnumFacing facing) {

        return new ChunkCoordinates(origin).up(1).offset(facing, -1);
    }

    private @Nullable TileEntityMobFactoryCell findCell(World world, ChunkCoordinates origin) {

        int Y_OFFSET = 10;
        for (int offset = 1; offset <= Y_OFFSET; offset++) {

            ChunkCoordinates p = new ChunkCoordinates(origin).down(offset);
            TileEntity te = world.getTileEntity(p);

            // First thing found MUST be the cell
            if (te instanceof TileEntityMobFactoryExporter || te instanceof TileEntityMobFactoryImporter)
                return null;

            if (te instanceof TileEntityMobFactoryCell)
                return (TileEntityMobFactoryCell)te;
        }

        return null;
    }

    private void scanController(World world, ChunkCoordinates origin, EnumFacing facing, ScannedFarmController controller) {

        ChunkCoordinates p = findController(world, origin, facing);
        if (p != null) {
            IFarmBlockController iFarmBlockController = (IFarmBlockController)(world.getTileEntity(p));
            controller.setBlocks(p);
            controller.wootMob = iFarmBlockController.getWootMob();
        }
    }

    private void scanRemote(World world, ChunkCoordinates origin, EnumFacing facing, ScannedFarmRemote remote) {

        TileEntityMobFactoryCell cell = findCell(world, origin);
        if (cell != null) {

            boolean power = cell.getPowerStation() != null;
            if (power)
                remote.setPowerPos(new ChunkCoordinates(cell.getPos()));

            ChunkCoordinates inPos = new ChunkCoordinates(cell.getPos()).down(1);
            ChunkCoordinates outPos = new ChunkCoordinates(cell.getPos()).down(2);
            TileEntity inTe = world.getTileEntity(inPos);
            TileEntity outTe = world.getTileEntity(outPos);

            if (inTe instanceof TileEntityMobFactoryImporter)
                remote.setImportPos(inPos);

            if (outTe instanceof TileEntityMobFactoryExporter)
                remote.setExportPos(outPos);
        }
    }

    private void scanUpgrades(World world, ChunkCoordinates origin, EnumFacing facing, EnumMobFactoryTier tier, ScannedFarmUpgrade upgrades) {

        ChunkCoordinates[] positions;
        Class upgradeClass;

        if (tier == EnumMobFactoryTier.TIER_ONE) {
            positions = new ChunkCoordinates[]{
                    new ChunkCoordinates(1, 1, 1), new ChunkCoordinates(-1, 1, 1)
            };
            upgradeClass = UpgradeTotemTierOne.class;
        } else if (tier == EnumMobFactoryTier.TIER_TWO) {
            positions = new ChunkCoordinates[]{
                    new ChunkCoordinates(1, 1, 1), new ChunkCoordinates(-1, 1, 1),
                    new ChunkCoordinates(2, 1, 1), new ChunkCoordinates(-2, 1, 1)};
            upgradeClass = UpgradeTotemTierTwo.class;
        } else {
            positions = new ChunkCoordinates[]{
                    new ChunkCoordinates(1, 1, 1), new ChunkCoordinates(-1, 1, 1),
                    new ChunkCoordinates(2, 1, 1), new ChunkCoordinates(-2, 1, 1),
                    new ChunkCoordinates(3, 1, 1), new ChunkCoordinates(-3, 1, 1)};
            upgradeClass = UpgradeTotemTierThree.class;
        }


        for (ChunkCoordinates p : positions) {
            ChunkCoordinates offset = ChunkCoordinatesHelper.rotateFromSouth(p, facing.getOpposite());
            ChunkCoordinates p2 = origin.add(offset.getX(), offset.getY(), offset.getZ());

            AbstractUpgradeTotem upgradeTotem = AbstractUpgradeTotemBuilder.build(upgradeClass, world, p2);
            if (upgradeTotem != null) {
                upgradeTotem.scan();

                if (upgradeTotem.isValid()) {

                    ScannedFarmUpgrade.Upgrade upgrade = new ScannedFarmUpgrade.Upgrade();
                    upgrade.upgradeTier = upgradeTotem.spawnerUpgradeLevel;
                    upgrade.blocks.addAll(upgradeTotem.ChunkCoordinatesList);
                    upgrade.upgrade = EnumFarmUpgrade.getFromEnumSpawnerUpgrade(upgradeTotem.spawnerUpgrade);
                    upgrades.addUpgrade(upgrade);
                }
            }
        }
    }

    public ScannedFarm2 scanFarm(World world, ChunkCoordinates origin, EnumFacing facing, EnumMobFactoryTier tier) {

        ScannedFarm2 farm = new ScannedFarm2();
        if (scanTier(world, origin, facing, tier, farm.badBlocks, farm.base.getBlocks())) {
            farm.base.tier = tier;
            scanController(world, origin, facing, farm.controller);
            scanRemote(world, origin, facing, farm.remote);
            scanUpgrades(world, origin, facing, tier, farm.upgrades);
        }

        return farm;
    }

    public ScannedFarm2 scanFarm(World world, ChunkCoordinates origin, EnumFacing facing) {

        ScannedFarm2 farm = new ScannedFarm2();
        for (int i = EnumMobFactoryTier.values().length - 1; i >= 0; i--) {
            ScannedFarm2 farm2 = scanFarm(world, origin, facing, EnumMobFactoryTier.values()[i]);
            if (farm2.isValidStructure() && farm2.isValidCofiguration(world)) {
                farm = farm2;
                break;
            }
        }

        return farm;
    }


    public enum BadBlockReason {
        MISSING_BLOCK,          // No block at that position
        WRONG_BLOCK,            // Not a structure block
        WRONG_STRUCTURE_TYPE    // Wrong structure block
    }

    public class BadFarmBlockInfo {

        private ChunkCoordinates pos;
        private BadBlockReason reason;
        private Block correctBlock;
        private int correctBlockMeta;
        private Block invalidBlock;
        private int invalidBlockMeta;

        public BadFarmBlockInfo(BadBlockReason reason, ChunkCoordinates pos) {
            this.reason = reason;
            this.pos = new ChunkCoordinates(pos);
        }

        public BadBlockReason getReason() { return reason; }
        public ChunkCoordinates getPos() { return pos; }
        public void setBlockInfo(Block correctBlock, int correctBlockMeta, Block invalidBlock, int invalidBlockMeta) {
            this.correctBlock = correctBlock;
            this.correctBlockMeta = correctBlockMeta;
            this.invalidBlock = invalidBlock;
            this.invalidBlockMeta = invalidBlockMeta;
        }

        public Block getCorrectBlock() { return correctBlock; }
        public int getCorrectBlockMeta() { return correctBlockMeta; }
        public Block getInvalidBlock() { return invalidBlock; }
        public int getInvalidBlockMeta() { return invalidBlockMeta; }
    }

}
