package ipsis.woot.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ipsis.woot.init.ModBlocks;
import ipsis.woot.multiblock.EnumMobFactoryTier;
import ipsis.woot.oss.client.ModelHelper;
import ipsis.woot.tileentity.TileEntityMobFactoryHeart;
import ipsis.woot.tools.IValidateTool;
import ipsis.woot.tools.ValidateToolUtils;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class BlockMobFactoryHeart extends BlockWoot implements ITooltipInfo, ITileEntityProvider {

    public static final String BASENAME = "factory";

    public BlockMobFactoryHeart() {
        super(Material.rock, BASENAME);
        this.setHardness(2.0F); // Set hardness as an example, adjust as needed
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityMobFactoryHeart();
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        int rotation = determineOrientation(worldIn, x, y, z, placer);
        worldIn.setBlockMetadataWithNotify(x, y, z, rotation, 2);
    }

    private int determineOrientation(World world, int x, int y, int z, EntityLivingBase entity) {
        if (Math.abs(entity.posX - x) < 2.0 && Math.abs(entity.posZ - z) < 2.0) {
            double d0 = entity.posY + 1.82 - entity.getYOffset();

            if (d0 - y > 2.0) {
                return 1; // Top
            }

            if (y - d0 > 0.0) {
                return 0; // Bottom
            }
        }

        int facing = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        return facing == 0 ? 2 : (facing == 1 ? 5 : (facing == 2 ? 3 : (facing == 3 ? 4 : 0))); // North by default
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        if (worldIn.isRemote) {
            return true;
        }

        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te instanceof TileEntityMobFactoryHeart) {
            TileEntityMobFactoryHeart heart = (TileEntityMobFactoryHeart) te;
            ItemStack heldItem = player.getHeldItem();

            if (heldItem == null || heldItem.getItem() == null) {
                heart.showGui(player, worldIn, x, y, z);
                return true;
            }

            if (heldItem.getItem() instanceof IValidateTool) {
                IValidateTool tool = (IValidateTool) heldItem.getItem();
                if (tool.isValidateTier(heldItem)) {
                    EnumMobFactoryTier tier = ValidateToolUtils.getModeFromNbt(heldItem).getTierFromMode();
                    if (tier != null) {
                        heart.manualFarmScan(player, tier);
                    }
                } else if (tool.isValidateExport(heldItem)) {
                    heart.outputFarmScan(player);
                } else if (tool.isValidateImport(heldItem)) {
                    heart.inputFarmScan(player);
                }

                return true;
            }
        }

        return super.onBlockActivated(worldIn, x, y, z, player, side, subX, subY, subZ);
    }

    @Override
    public void getTooltip(List<String> toolTip, boolean showAdvanced, int meta, boolean detail) {

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelHelper.registerBlock(ModBlocks.blockFactoryHeart, BASENAME);
    }
}
