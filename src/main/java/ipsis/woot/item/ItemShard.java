package ipsis.woot.item;

import ipsis.woot.init.ModItems;
import ipsis.woot.oss.client.ModelHelper;
import ipsis.woot.reference.Reference;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemShard extends ItemWoot {

    public static final String BASENAME = "shard";

    public static final String[] VARIANTS = new String[] {
            EnumShardType.DIAMOND.getName(),
            EnumShardType.EMERALD.getName(),
            EnumShardType.QUARTZ.getName(),
            EnumShardType.NETHERSTAR.getName(),
            EnumShardType.TIER_II.getName(),
            EnumShardType.TIER_III.getName(),
            EnumShardType.TIER_IV.getName()
    };

    public enum EnumShardType {
        DIAMOND("diamond"),
        EMERALD("emerald"),
        QUARTZ("quartz"),
        NETHERSTAR("netherstar"),
        TIER_II("tier_ii"),
        TIER_III("tier_iii"),
        TIER_IV("tier_iv");

        public int getMeta() {
            return ordinal();
        }

        EnumShardType(String name) {
            this.name = name;
        }

        private String name;

        public String getName() {
            return this.name;
        }
    }

    public ItemShard() {

        super(BASENAME);
        setMaxStackSize(64);
        setHasSubtypes(true);
    }

    @Override
    public void initModel() {

        for (int i = 0; i < VARIANTS.length; i++) {
            ModelHelper.registerItem(ModItems.itemShard, i, BASENAME + "." + VARIANTS[i]);
            ModelBakery.registerItemVariants(ModItems.itemShard,  new ResourceLocation(Reference.MOD_ID + ":" + BASENAME + "." + VARIANTS[i]));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {

        if (isInCreativeTab(tab)) {
            for (int i = 0; i < VARIANTS.length; i++)
                items.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {

        int idx = stack.getItemDamage() % VARIANTS.length;
        return super.getUnlocalizedName() + "." + VARIANTS[idx];
    }

    @Override
    public boolean hasEffect(ItemStack stack) {

        if (stack != null && stack.getItemDamage() == EnumShardType.NETHERSTAR.getMeta())
            return true;

        return false;
    }
}
