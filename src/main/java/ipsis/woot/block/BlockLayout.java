package ipsis.woot.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ipsis.woot.init.ModBlocks;
import ipsis.woot.oss.client.ModelHelper;
import ipsis.woot.tileentity.TileEntityLayout;
import ipsis.woot.multiblock.EnumMobFactoryTier;
import ipsis.woot.util.StringHelper;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class BlockLayout extends BlockWoot implements ITileEntityProvider, ITooltipInfo {

    public static final String BASENAME = "layout";

    public BlockLayout() {

        super(Material.rock, BASENAME);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initModel() {

        ModelHelper.registerBlock(ModBlocks.blockLayout, BASENAME);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {

        return new TileEntityLayout();
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        TileEntity te = worldIn.getTileEntity(x, y, z);
        if(te != null && te instanceof TileEntityLayout) {
            int facing = MathHelper.floor_double((placer.rotationYaw * 4F) / 360F + 0.5D) & 3;
            EnumFacing f;
            switch(facing) {
                case 0: f = EnumFacing.EAST; break;
                case 1: f = EnumFacing.WEST; break;
                case 2: f = EnumFacing.NORTH; break;
                default: f = EnumFacing.SOUTH;
            }

            EnumFacing oppositeFacing;
            switch (f) {
                case EAST: oppositeFacing = EnumFacing.WEST; break;
                case WEST: oppositeFacing = EnumFacing.EAST; break;
                case NORTH: oppositeFacing = EnumFacing.SOUTH; break;
                case SOUTH: oppositeFacing = EnumFacing.NORTH; break;
                default: oppositeFacing = EnumFacing.NORTH;
            }

            ((TileEntityLayout) te).setFacing(oppositeFacing);
            ((TileEntityLayout) te).refreshLayout();
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        /**
         * Activer avec de la redstone en mode créatif - construit l'usine
         * Activer pour passer à travers les différentes catégories
         * Activer tout en se faufilant pour changer le niveau de y: tout -> 1 -> 2 -> .... -> tout
         */
        if (!worldIn.isRemote) {
            TileEntity te = worldIn.getTileEntity(x, y, z);
            if (te != null && te instanceof TileEntityLayout) {
                ItemStack heldItem = player.getHeldItem();
                if (player.capabilities.isCreativeMode && heldItem != null && heldItem.getItem() == Items.redstone) {
                    ((TileEntityLayout) te).buildFactory();
                } else if (player.isSneaking()) {
                    ((TileEntityLayout) te).setNextLevel();
                    worldIn.markBlockForUpdate(x, y, z);
                } else {
                    ((TileEntityLayout) te).setNextTier();
                    EnumMobFactoryTier tier = ((TileEntityLayout) te).getTier();
                    player.addChatMessage(new ChatComponentText(tier.getTranslated("info.woot.tier")));
                    worldIn.markBlockForUpdate(x, y, z);
                }
            }
        }
        return true;
    }


    @Override
    public boolean isOpaqueCube(){
        /* This stops the TESR rendering really dark! */
        return false;
    }

    @Override
    public void getTooltip(List<String> toolTip, boolean showAdvanced, int meta, boolean detail) {

        toolTip.add(StringHelper.localize("info.woot.guide.rclick"));
        toolTip.add(StringHelper.localize("info.woot.guide.srclick"));
    }
}
