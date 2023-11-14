package ipsis.woot.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ipsis.woot.farmblocks.IFarmBlockController;
import ipsis.woot.init.ModBlocks;
import ipsis.woot.oss.client.ModelHelper;
import ipsis.woot.tileentity.TileEntityMobFactoryController;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BlockMobFactoryController extends BlockWoot implements ITileEntityProvider {

    public static final String BASENAME = "controller";

    public BlockMobFactoryController() {
        super(Material.rock, BASENAME);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityMobFactoryController();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initModel() {
        ModelHelper.registerBlock(ModBlocks.blockFactoryController, BASENAME);
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te instanceof TileEntityMobFactoryController)
            ((TileEntityMobFactoryController) te).onBlockAdded();
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te instanceof IFarmBlockController && itemIn != null && itemIn.hasTagCompound())
            ((IFarmBlockController) te).readControllerFromNBT(itemIn.getTagCompound());
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
        super.harvestBlock(world, player, x, y, z, meta);
        world.setBlockToAir(x, y, z);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        TileEntity te = world.getTileEntity(x, y, z);
        ArrayList<ItemStack> drops = new ArrayList<>();

        if (te instanceof TileEntityMobFactoryController) {
            TileEntityMobFactoryController controller = (TileEntityMobFactoryController) te;
            drops.add(controller.getDroppedItemStack());
        } else {
            drops.addAll(super.getDrops(world, x, y, z, metadata, fortune));
        }

        return drops;
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        if (willHarvest) {
            this.harvestBlock(world, player, x, y, z, world.getBlockMetadata(x, y, z));
        }

        world.setBlockToAir(x, y, z);
        return true;
    }
}
