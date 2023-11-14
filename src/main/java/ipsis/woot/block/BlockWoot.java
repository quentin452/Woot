package ipsis.woot.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ipsis.Woot;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public abstract class BlockWoot extends Block {

    public BlockWoot(Material m, String name) {

        super(m);
        setCreativeTab(Woot.tabWoot);
        setHardness(1.5F);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {

    }
}
