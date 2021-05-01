package ipsis.woot.datagen.modules;

import ipsis.woot.Woot;
import ipsis.woot.crafting.DyeSqueezerRecipe;
import ipsis.woot.crafting.DyeSqueezerRecipeBuilder;
import ipsis.woot.modules.generic.GenericSetup;
import ipsis.woot.modules.infuser.InfuserSetup;
import ipsis.woot.modules.squeezer.DyeMakeup;
import ipsis.woot.modules.squeezer.SqueezerSetup;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import java.util.Locale;
import java.util.function.Consumer;

public class Squeezer {

    private static final int DYE_ENERGY_COST = 1000;

    public static void registerRecipes(Consumer<IFinishedRecipe> consumer) {


        ShapedRecipeBuilder.shaped(SqueezerSetup.SQUEEZER_BLOCK.get())
                .pattern("dpd")
                .pattern("dcd")
                .pattern("dbd")
                .define('p', Blocks.PISTON)
                .define('d', Tags.Items.DYES)
                .define('c', GenericSetup.MACHINE_CASING_ITEM.get())
                .define('b', Items.BUCKET)
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.Instance.hasItems(Blocks.COBBLESTONE))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(SqueezerSetup.SQUEEZER_BLOCK.get())
                .requires(SqueezerSetup.SQUEEZER_BLOCK.get())
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.Instance.hasItems(Blocks.COBBLESTONE))
                .save(consumer, "squeezer_1");

        ShapedRecipeBuilder.shaped(SqueezerSetup.ENCHANT_SQUEEZER_BLOCK.get())
                .pattern("tpt")
                .pattern("tct")
                .pattern("tbt")
                .define('p', Blocks.PISTON)
                .define('c', GenericSetup.MACHINE_CASING_ITEM.get())
                .define('b', Items.BUCKET)
                .define('t', Items.BOOK)
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.Instance.hasItems(Blocks.COBBLESTONE))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(SqueezerSetup.ENCHANT_SQUEEZER_BLOCK.get())
                .requires(SqueezerSetup.ENCHANT_SQUEEZER_BLOCK.get())
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.Instance.hasItems(Blocks.COBBLESTONE))
                .save(consumer, "ench_squeezer_1");

        /**
         * Vanilla dyes
         */
        for (DyeMakeup d : DyeMakeup.values()) {
            Woot.setup.getLogger().info("Generating Dye Squeezer recipe for {}", d);
            DyeSqueezerRecipeBuilder.dyeSqueezerRecipe(
                    Ingredient.of(d.getItemTag()),
                    DYE_ENERGY_COST,
                    d.getRed(), d.getYellow(), d.getBlue(), d.getWhite())
                    .build(consumer, d.name().toLowerCase(Locale.ROOT));
        }

        /**
         * Vanilla items that produce dyes
         */
        class VanillaDyes {
            Item item;
            DyeMakeup dyeMakeup;
            String name;
            public VanillaDyes(Item item, DyeMakeup dyeMakeup, String name) {
                this.item = item;
                this.dyeMakeup = dyeMakeup;
                this.name = name;
            }
        }
        VanillaDyes[] dyes = {
                new VanillaDyes(Items.RED_TULIP, DyeMakeup.RED, "red_tulip"),
                new VanillaDyes(Items.BEETROOT, DyeMakeup.RED, "beetroot"),
                new VanillaDyes(Items.POPPY, DyeMakeup.RED, "poppy"),
                new VanillaDyes(Items.ROSE_BUSH, DyeMakeup.RED, "rose_bush"),
                new VanillaDyes(Items.CACTUS, DyeMakeup.GREEN, "cactus"),
                new VanillaDyes(Items.OXEYE_DAISY, DyeMakeup.LIGHTGRAY, "oxeye_daisy"),
                new VanillaDyes(Items.AZURE_BLUET, DyeMakeup.LIGHTGRAY, "azure_bluet"),
                new VanillaDyes(Items.WHITE_TULIP, DyeMakeup.LIGHTGRAY, "white_tulip"),
                new VanillaDyes(Items.PINK_TULIP, DyeMakeup.PINK, "pink_tulip"),
                new VanillaDyes(Items.PEONY, DyeMakeup.PINK, "peony"),
                new VanillaDyes(Items.SEA_PICKLE, DyeMakeup.LIME, "sea_pickle"),
                new VanillaDyes(Items.DANDELION, DyeMakeup.YELLOW, "dandelion"),
                new VanillaDyes(Items.SUNFLOWER, DyeMakeup.YELLOW, "sunflower"),
                new VanillaDyes(Items.BLUE_ORCHID, DyeMakeup.LIGHTBLUE, "blue_orchid"),
                new VanillaDyes(Items.LILAC, DyeMakeup.MAGENTA, "lilac"),
                new VanillaDyes(Items.ALLIUM, DyeMakeup.MAGENTA, "allium"),
                new VanillaDyes(Items.ORANGE_TULIP, DyeMakeup.ORANGE, "orange_tulip"),
                new VanillaDyes(Items.CORNFLOWER, DyeMakeup.BLUE, "cornflower"),
                new VanillaDyes(Items.COCOA_BEANS, DyeMakeup.BROWN, "cocoa_beans"),
                new VanillaDyes(Items.WITHER_ROSE, DyeMakeup.BLACK, "wither_rose"),
                new VanillaDyes(Items.LILY_OF_THE_VALLEY, DyeMakeup.WHITE, "lily_of_the_valley"),
        };
        for (VanillaDyes d : dyes) {
            Woot.setup.getLogger().info("Generating Dye Squeezer recipe for {}", d.name);
            DyeSqueezerRecipeBuilder.dyeSqueezerRecipe(
                    Ingredient.of(d.item),
                    DYE_ENERGY_COST,
                    d.dyeMakeup.getRed(),
                    d.dyeMakeup.getYellow(),
                    d.dyeMakeup.getBlue(),
                    d.dyeMakeup.getWhite())
                    .build(consumer, d.name);
        }
    }
}
