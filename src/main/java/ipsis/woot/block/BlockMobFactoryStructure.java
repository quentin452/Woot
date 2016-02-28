package ipsis.woot.block;

import ipsis.oss.LogHelper;
import ipsis.woot.init.ModBlocks;
import ipsis.woot.reference.Reference;
import ipsis.woot.tileentity.TileEntityMobFactoryStructure;
import ipsis.woot.tileentity.multiblock.EnumMobFactoryModule;
import ipsis.woot.util.UnlistedPropertyBoolean;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockMobFactoryStructure extends BlockContainerWoot{

    public static final String BASENAME = "structure";

    public static final UnlistedPropertyBoolean FORMED = new UnlistedPropertyBoolean("FORMED");

    public static final PropertyEnum<EnumMobFactoryModule> MODULE = PropertyEnum.<EnumMobFactoryModule>create("module", EnumMobFactoryModule.class);
    public BlockMobFactoryStructure() {

        super (Material.rock, BASENAME);
        this.setDefaultState(this.blockState.getBaseState().withProperty(MODULE, EnumMobFactoryModule.BLOCK_1));
    }

    @Override
    public int getRenderType() {

        return 3;
    }

    public EnumMobFactoryModule getModuleTypeFromState(IBlockState state) {

        return EnumMobFactoryModule.byMetadata(getMetaFromState(state));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {

        return new TileEntityMobFactoryStructure();
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {

        TileEntityMobFactoryStructure te = (TileEntityMobFactoryStructure)worldIn.getTileEntity(pos);
        te.blockAdded();
    }

    @Override
    protected BlockState createBlockState() {
        IUnlistedProperty[] unlistedProperties = new IUnlistedProperty[]{ FORMED };
        return new ExtendedBlockState(this, new IProperty[] { MODULE }, unlistedProperties);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state instanceof IExtendedBlockState) {

            IExtendedBlockState extendedBlockState = (IExtendedBlockState)state;
            TileEntityMobFactoryStructure te = (TileEntityMobFactoryStructure)world.getTileEntity(pos);
            boolean formed = false;
            if (te != null)
                formed = te.isClientHasMaster();

            return extendedBlockState.withProperty(FORMED, formed);
        }

        return state;
    }

    @Override
    public int damageDropped(IBlockState state) {

        return ((EnumMobFactoryModule)state.getValue(MODULE)).getMetadata();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {

        for (EnumMobFactoryModule m : EnumMobFactoryModule.values())
            list.add(new ItemStack(itemIn, 1, m.getMetadata()));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {

        return this.getDefaultState().withProperty(MODULE, EnumMobFactoryModule.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {

        return ((EnumMobFactoryModule)state.getValue(MODULE)).getMetadata();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initModel() {

        ResourceLocation[] locations = new ResourceLocation[EnumMobFactoryModule.values().length];
        for (int i = 0; i < EnumMobFactoryModule.values().length; i++)
            locations[i] = new ResourceLocation(Reference.MOD_NAME_LOWER + ":" + BASENAME + "_" + EnumMobFactoryModule.VALUES[i]);

        ModelBakery.registerItemVariants(Item.getItemFromBlock(ModBlocks.blockStructure), locations);
    }
}
