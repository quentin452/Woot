package ipsis.woot.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ipsis.woot.init.ModBlocks;
import ipsis.woot.oss.client.ModelHelper;
import ipsis.woot.tileentity.TileEntityMobFactoryImporter;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockMobFactoryImporter extends BlockWoot implements ITileEntityProvider {

    public static final String BASENAME = "importer";

    public BlockMobFactoryImporter() {

        super (Material.rock, BASENAME);
      //  this.setDefaultState(this.blockState.getBaseState().withProperty(FORMED, false));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {

        return new TileEntityMobFactoryImporter();
    }


    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {

        TileEntity te = worldIn.getTileEntity(x,y,z);
        if (te instanceof TileEntityMobFactoryImporter)
            ((TileEntityMobFactoryImporter) te).onBlockAdded();
    }

    @Override
    public boolean isOpaqueCube() {

        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {

        return true;
    }
    @SideOnly(Side.CLIENT)
    @Override
    public void initModel() {
        ModelHelper.registerBlock(ModBlocks.blockImporter, BASENAME);
    }
}
