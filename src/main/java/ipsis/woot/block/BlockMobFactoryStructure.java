package ipsis.woot.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ipsis.Woot;
import ipsis.woot.reference.Reference;
import ipsis.woot.tileentity.TileEntityMobFactoryStructure;
import ipsis.woot.multiblock.EnumMobFactoryModule;
import ipsis.woot.util.DebugSetup;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class BlockMobFactoryStructure extends BlockWoot implements ITileEntityProvider {

    public static final String BASENAME = "structure";

    public BlockMobFactoryStructure() {

        super (Material.rock, BASENAME);
       // this.setDefaultState(this.blockState.getBaseState().withProperty(MODULE, EnumMobFactoryModule.BLOCK_1).withProperty(FORMED, false));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {

        return new TileEntityMobFactoryStructure();
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {

        Woot.debugSetup.trace(DebugSetup.EnumDebugType.MULTIBLOCK, "BlockMobFactoryStructure:", "onBlockAdded");

        TileEntity te = worldIn.getTileEntity(x,y,z);
        if (te instanceof TileEntityMobFactoryStructure)
            ((TileEntityMobFactoryStructure) te).onBlockAdded();
    }

    @Override
    public int damageDropped(int meta) {

        return state.getValue(MODULE).getMetadata();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {

        for (EnumMobFactoryModule m : EnumMobFactoryModule.values())
            items.add(new ItemStack(this, 1, m.getMetadata()));
    }

    public ItemStack getItemStack(EnumMobFactoryModule m) {

        return new ItemStack(this,  1, m.getMetadata());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initModel() {

        Item itemBlockVariants = Item.REGISTRY.getObject(new ResourceLocation(Reference.MOD_ID, BASENAME));

        for (int i = 0; i < EnumMobFactoryModule.VALUES.length; i++) {

            EnumMobFactoryModule e = EnumMobFactoryModule.VALUES[i];
            ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(
                    Reference.MOD_ID + ":" + BASENAME + "_" + e, "inventory");
            ModelLoader.setCustomModelResourceLocation(itemBlockVariants, i, itemModelResourceLocation);
        }
    }

    @Override
    public boolean isOpaqueCube() {

        return false;
    }


    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side){

        TileEntity te = worldIn.getTileEntity(x,y,z);
        if (te instanceof TileEntityMobFactoryStructure) {
            boolean validBlock =  !isAir(worldIn, x,y,z.offset(side.getOpposite()));

            if (validBlock && !((TileEntityMobFactoryStructure) te).isClientFormed())
                return true;
        }

        return super.shouldSideBeRendered(worldIn, x,y,z, side);
    }
}
