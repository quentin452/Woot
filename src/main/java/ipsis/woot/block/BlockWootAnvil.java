package ipsis.woot.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ipsis.Woot;
import ipsis.woot.init.ModBlocks;
import ipsis.woot.init.ModItems;
import ipsis.woot.oss.client.ModelHelper;
import ipsis.woot.tileentity.TileEntityAnvil;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;

/**
 * Based off BlockAnvil in vanilla - but without the damage
 */
public class BlockWootAnvil extends BlockWoot implements ITileEntityProvider {

    public static final String BASENAME = "anvil";

    public BlockWootAnvil() {

        super(Material.anvil, BASENAME);
      //  this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public boolean isOpaqueCube() {

        return false;
    }


    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();

        // Get the main block
        super.getDrops(world, x, y, z, metadata, fortune);

        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityAnvil) {
            // Add any items that were in the anvil
            TileEntityAnvil anvil = (TileEntityAnvil) te;
            ItemStack itemStack = anvil.getBaseItem();
            if (itemStack != null) {
                drops.add(itemStack.copy());
            }
        }

        return drops;
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        // From TinkersConstruct to allow the TE exist while processing the getDrops
        this.onBlockDestroyedByPlayer(world, x, y, z, world.getBlockMetadata(x, y, z));
        if (willHarvest) {
            this.harvestBlock(world, player, x, y, z, world.getBlockMetadata(x, y, z));
        }

        world.setBlockToAir(x, y, z);
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {

        return true;
    }

    @Override
    public int getRenderType() {
        return 3;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initModel() {

        ModelHelper.registerBlock(ModBlocks.blockAnvil, BASENAME);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {

        return new TileEntityAnvil();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityAnvil) {
                TileEntityAnvil anvil = (TileEntityAnvil) te;
                ItemStack playerItem = player.getHeldItem();

                if (anvil.getBaseItem() == null) {
                    if (Woot.anvilManager.isValidBaseItem(playerItem)) {
                        // From player hand to empty anvil
                        ItemStack baseItem = playerItem.copy();
                        baseItem.stackSize = 1;
                        anvil.setBaseItem(baseItem);

                        playerItem.stackSize--;
                        if (playerItem.stackSize <= 0) {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                        }
                        player.openContainer.detectAndSendChanges();
                    }
                } else {
                    if (playerItem != null && playerItem.getItem() == ModItems.itemYahHammer) {
                        anvil.tryCraft(player);
                    } else {
                        // From anvil to player
                        ItemStack baseItem = anvil.getBaseItem();
                        anvil.setBaseItem(null);
                        if (!player.inventory.addItemStackToInventory(baseItem)) {
                            EntityItem entityItem = new EntityItem(world, x + 0.5, y + 1.5, z + 0.5, baseItem);
                            world.spawnEntityInWorld(entityItem);
                        } else {
                            player.openContainer.detectAndSendChanges();
                        }
                    }
                }
            }
        }
        return true;
    }
}
