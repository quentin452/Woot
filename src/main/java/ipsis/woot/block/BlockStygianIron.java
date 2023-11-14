package ipsis.woot.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ipsis.woot.init.ModBlocks;
import ipsis.woot.oss.client.ModelHelper;
import net.minecraft.block.material.Material;

public class BlockStygianIron extends BlockWoot {

    public static final String BASENAME = "stygianiron";

    public BlockStygianIron() {

        super(Material.rock, BASENAME);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initModel() {

        ModelHelper.registerBlock(ModBlocks.blockStygianIron, BASENAME);
    }
}
