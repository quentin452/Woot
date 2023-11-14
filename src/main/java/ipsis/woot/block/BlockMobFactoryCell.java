package ipsis.woot.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ipsis.Woot;
import ipsis.woot.configuration.EnumConfigKey;
import ipsis.woot.power.storage.IPowerStation;
import ipsis.woot.reference.Reference;
import ipsis.woot.tileentity.TileEntityMobFactoryCell;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * The cell stores power, however it can only be used as part of the formed farm
 */
public class BlockMobFactoryCell extends BlockWoot implements ITileEntityProvider {

    public static final String BASENAME = "cell";

    public BlockMobFactoryCell() {
        super(Material.rock, BASENAME);
     //   this.setDefaultState(this.blockState.getBaseState().withProperty(TIER, EnumCellTier.TIER_I).withProperty(FORMED, false));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {

        TileEntityMobFactoryCell te =  new TileEntityMobFactoryCell();
        te.setTier(EnumCellTier.byMetadata(meta));
        return te;
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {

        TileEntity te = worldIn.getTileEntity(x,y,z);
        if (te instanceof TileEntityMobFactoryCell)
            ((TileEntityMobFactoryCell) te).onBlockAdded();
    }

    @Override
    public int damageDropped(int meta) {
        return EnumCellTier.byMetadata(meta).getMetadata();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list){

        List<ItemStack> items = new ArrayList<>();

        for (EnumCellTier t : EnumCellTier.values())
            items.add(new ItemStack(this, 1, t.getMetadata()));

        list.addAll(items);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {

        TileEntity te = world.getTileEntity(x,y,z);
        if (te instanceof TileEntityMobFactoryCell) {
            ArrayList<ItemStack> drops = super.getDrops(world, x,y,z,metadata,fortune);
            IPowerStation powerStation = ((TileEntityMobFactoryCell) te).getPowerStation();
            if (powerStation != null && (!drops.isEmpty())) {
                    NBTTagCompound compound = drops.get(0).getTagCompound();
                    if (compound == null) {
                        compound = new NBTTagCompound();
                        drops.get(0).setTagCompound(compound);
                    }

                    powerStation.writeToNBT(compound);

            }
            return drops;

        }

        return super.getDrops(world, x,y,z, metadata, fortune);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {

        super.onBlockPlacedBy(worldIn, x,y,z, placer, itemIn);
        if (itemIn.hasTagCompound() && !worldIn.isRemote) {
            TileEntity te = worldIn.getTileEntity(x,y,z);
            if (te instanceof TileEntityMobFactoryCell) {
                ((TileEntityMobFactoryCell) te).getPowerStation().readFromNBT(itemIn.getTagCompound());
            }
        }
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        int metadata = world.getBlockMetadata(x, y, z);

        // From TinkersConstruct to allow the TE to exist while processing the getDrops
        this.onBlockDestroyedByPlayer(world, x, y, z, metadata);

        if (willHarvest) {
            this.harvestBlock(world, player, x, y, z, metadata);
        }

        world.setBlockToAir(x, y, z);
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initModel() {
        Item itemBlockVariants = Item.REGISTRY.getObject(new ResourceLocation(Reference.MOD_ID, BASENAME));
        for (int i = 0; i < EnumCellTier.VALUES.length; i++) {
            EnumCellTier e = EnumCellTier.VALUES[i];
            ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(
                    Reference.MOD_ID + ":" + BASENAME + "_" + e, "inventory");
            ModelLoader.setCustomModelResourceLocation(itemBlockVariants, i, itemModelResourceLocation);
        }
    }

    public enum EnumCellTier {

        TIER_I("tier_i"),
        TIER_II("tier_ii"),
        TIER_III("tier_iii");

        public static EnumCellTier[] VALUES = {TIER_I, TIER_II, TIER_III};

        private final String name;

        EnumCellTier(String name) {
            this.name = name;
        }

        public String getName() {

            return this.name;
        }

        int getMetadata() {

            return this.ordinal();
        }

        public static EnumCellTier byMetadata(int metadata) {

            if (metadata < 0 || metadata >= VALUES.length)
                return TIER_I;

            return VALUES[metadata];
        }

        public static int getMaxPower(EnumCellTier tier) {

            if (tier == TIER_I)
                return Woot.wootConfiguration.getInteger(EnumConfigKey.T1_POWER_MAX);
            else if (tier == TIER_II)
                return Woot.wootConfiguration.getInteger(EnumConfigKey.T2_POWER_MAX);

            return Woot.wootConfiguration.getInteger(EnumConfigKey.T3_POWER_MAX);
        }

        public static int getMaxTransfer(EnumCellTier tier) {

            if (tier == TIER_I)
                return Woot.wootConfiguration.getInteger(EnumConfigKey.T1_POWER_RX_TICK);
            else if (tier == TIER_II)
                return Woot.wootConfiguration.getInteger(EnumConfigKey.T2_POWER_RX_TICK);

            return Woot.wootConfiguration.getInteger(EnumConfigKey.T3_POWER_RX_TICK);
        }
    }
}
