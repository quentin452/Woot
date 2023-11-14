package ipsis.woot.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ipsis.woot.tileentity.TileEntityMobFactoryUpgrade;
import ipsis.woot.util.EnumSpawnerUpgrade;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public abstract class BlockMobFactoryUpgradeBase extends BlockWoot implements ITileEntityProvider{

    public BlockMobFactoryUpgradeBase(String basename) {
        super(Material.rock, basename);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {

        return new TileEntityMobFactoryUpgrade();
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {

        TileEntity te = worldIn.getTileEntity(x,y,z);
        if (te instanceof TileEntityMobFactoryUpgrade)
            ((TileEntityMobFactoryUpgrade) te).onBlockAdded();
    }

    public void getUpgradeTooltip(EnumSpawnerUpgrade u, List<String> toolTip, boolean showAdvanced, int meta, boolean detail) {

    }

    @Override
    public boolean isOpaqueCube() {

        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {

        TileEntity te = worldIn.getTileEntity(x,y,z);
        if (te instanceof TileEntityMobFactoryUpgrade) {
            boolean validBlock =  !isAir(worldIn, x,y,z.offset(side.getOpposite()));

            if (validBlock && !((TileEntityMobFactoryUpgrade) te).isClientFormed())
                return true;
        }

        return super.shouldSideBeRendered(worldIn, x,y,z, side);
    }

    /**
     * The enum is split over multiple blocks because of metadata limits
     * This function returns the metadata value for the enum , depending on the block it is on
     */
    public static int getBlockSplitMeta(EnumSpawnerUpgrade u) {

        if (u.ordinal() >= EnumSpawnerUpgrade.RATE_I.ordinal() && u.ordinal() <= EnumSpawnerUpgrade.DECAPITATE_III.ordinal())
            return u.ordinal();

        return u.ordinal() - (EnumSpawnerUpgrade.DECAPITATE_III.ordinal() + 1);
    }
}
