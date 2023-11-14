package ipsis.woot.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ipsis.woot.util.EnumSpawnerUpgrade;
import ipsis.woot.reference.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class BlockMobFactoryUpgradeB extends BlockMobFactoryUpgradeBase implements ITooltipInfo {

    public static final String BASENAME = "upgradeb";

    public BlockMobFactoryUpgradeB() {

        super(BASENAME);
      //  this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumVariantUpgradeB.EFFICIENCY_I).withProperty(ACTIVE, false));
    }

    @Override
    public int damageDropped(int meta) {

        return state.getValue(VARIANT).getMetadata();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {

        for (EnumVariantUpgradeB u : EnumVariantUpgradeB.values())
            items.add(new ItemStack(this, 1, u.getMetadata()));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initModel() {

        Item itemBlockVariants = Item.REGISTRY.getObject(new ResourceLocation(Reference.MOD_ID, BASENAME));

        for (int i = 0; i < EnumVariantUpgradeB.values().length; i++) {

            EnumVariantUpgradeB e = EnumVariantUpgradeB.getFromMetadata(i);
            ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(
                    Reference.MOD_ID + ":" + BASENAME + "_" + e, "inventory");
            ModelLoader.setCustomModelResourceLocation(itemBlockVariants, i, itemModelResourceLocation);
        }
    }

    @Override
    public void getTooltip(List<String> toolTip, boolean showAdvanced, int meta, boolean detail) {

        EnumSpawnerUpgrade type = EnumSpawnerUpgrade.getFromVariant(EnumVariantUpgradeB.getFromMetadata(meta));
        getUpgradeTooltip(type, toolTip, showAdvanced, meta, detail);
    }
}
