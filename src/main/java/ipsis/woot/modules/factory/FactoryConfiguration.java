package ipsis.woot.modules.factory;

import ipsis.woot.config.ConfigDefaults;
import ipsis.woot.config.ConfigOverride;
import ipsis.woot.config.ConfigPath;
import net.minecraftforge.common.ForgeConfigSpec;

import ipsis.woot.config.ConfigDefaults.Factory;

import java.util.List;

public class FactoryConfiguration {

    public static ForgeConfigSpec.BooleanValue TICK_ACCEL;

    public static ForgeConfigSpec.IntValue CELL_1_CAPACITY;
    public static ForgeConfigSpec.IntValue CELL_1_MAX_TRANSFER;
    public static ForgeConfigSpec.IntValue CELL_2_CAPACITY;
    public static ForgeConfigSpec.IntValue CELL_2_MAX_TRANSFER;
    public static ForgeConfigSpec.IntValue CELL_3_CAPACITY;
    public static ForgeConfigSpec.IntValue CELL_3_MAX_TRANSFER;
    public static ForgeConfigSpec.IntValue CELL_4_CAPACITY;
    public static ForgeConfigSpec.IntValue CELL_4_MAX_TRANSFER;

    public static ForgeConfigSpec.IntValue MASS_COUNT;
    public static ForgeConfigSpec.IntValue SPAWN_TICKS;
    public static ForgeConfigSpec.IntValue UNITS_PER_HEALTH;
    public static ForgeConfigSpec.IntValue MOB_SHARD_KILLS;
    public static ForgeConfigSpec.IntValue TIER_1_MAX_UNITS;
    public static ForgeConfigSpec.IntValue TIER_2_MAX_UNITS;
    public static ForgeConfigSpec.IntValue TIER_3_MAX_UNITS;
    public static ForgeConfigSpec.IntValue TIER_4_MAX_UNITS;
    public static ForgeConfigSpec.IntValue TIER_5_MAX_UNITS;

    public static ForgeConfigSpec.IntValue EFFICIENCY_1;
    public static ForgeConfigSpec.IntValue EFFICIENCY_2;
    public static ForgeConfigSpec.IntValue EFFICIENCY_3;
    public static ForgeConfigSpec.IntValue MASS_COUNT_1;
    public static ForgeConfigSpec.IntValue MASS_COUNT_2;
    public static ForgeConfigSpec.IntValue MASS_COUNT_3;
    public static ForgeConfigSpec.IntValue RATE_1;
    public static ForgeConfigSpec.IntValue RATE_2;
    public static ForgeConfigSpec.IntValue RATE_3;
    public static ForgeConfigSpec.IntValue XP_1;
    public static ForgeConfigSpec.IntValue XP_2;
    public static ForgeConfigSpec.IntValue XP_3;
    public static ForgeConfigSpec.IntValue TIER_SHARD_1;
    public static ForgeConfigSpec.IntValue TIER_SHARD_2;
    public static ForgeConfigSpec.IntValue TIER_SHARD_3;
    public static ForgeConfigSpec.DoubleValue T1_FARM_DROP_CHANCE;
    public static ForgeConfigSpec.ConfigValue<List<Integer>> T1_FARM_DROP_SHARD_WEIGHTS;
    public static ForgeConfigSpec.DoubleValue T2_FARM_DROP_CHANCE;
    public static ForgeConfigSpec.ConfigValue<List<Integer>> T2_FARM_DROP_SHARD_WEIGHTS;
    public static ForgeConfigSpec.DoubleValue T3_FARM_DROP_CHANCE;
    public static ForgeConfigSpec.ConfigValue<List<Integer>> T3_FARM_DROP_SHARD_WEIGHTS;
    public static ForgeConfigSpec.DoubleValue T4_FARM_DROP_CHANCE;
    public static ForgeConfigSpec.ConfigValue<List<Integer>> T4_FARM_DROP_SHARD_WEIGHTS;
    public static ForgeConfigSpec.DoubleValue T5_FARM_DROP_CHANCE;
    public static ForgeConfigSpec.ConfigValue<List<Integer>> T5_FARM_DROP_SHARD_WEIGHTS;

    public static void init(ForgeConfigSpec.Builder COMMON_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {

        COMMON_BUILDER.comment("Settings for the factory").push(ConfigPath.Factory.CATEGORY);
        CLIENT_BUILDER.comment("Settings for the factory").push(ConfigPath.Factory.CATEGORY);
        {
            COMMON_BUILDER.push(ConfigPath.Factory.CATEGORY_GENERAL);
            {
                TICK_ACCEL = COMMON_BUILDER
                        .comment("Allow tick acceleration to be used on the factory")
                        .define(ConfigPath.Factory.TICK_ACCELERATION_TAG, Factory.TICK_ACCELERATION_DEF);
                TIER_1_MAX_UNITS = COMMON_BUILDER
                        .comment("Max health for a tier 1 mob")
                        .defineInRange(ConfigPath.Factory.TIER_1_MAX_HEALTH_TAG,
                                Factory.TIER_1_MAX_UNITS_DEF, 5, Integer.MAX_VALUE);
                TIER_2_MAX_UNITS = COMMON_BUILDER
                        .comment("Max health for a tier 2 mob")
                        .defineInRange(ConfigPath.Factory.TIER_2_MAX_HEALTH_TAG,
                                Factory.TIER_2_MAX_UNITS_DEF, 5, Integer.MAX_VALUE);
                TIER_3_MAX_UNITS = COMMON_BUILDER
                        .comment("Max health for a tier 3 mob")
                        .defineInRange(ConfigPath.Factory.TIER_3_MAX_HEALTH_TAG,
                                Factory.TIER_3_MAX_UNITS_DEF, 5, Integer.MAX_VALUE);
                TIER_4_MAX_UNITS = COMMON_BUILDER
                        .comment("Max health for a tier 4 mob")
                        .defineInRange(ConfigPath.Factory.TIER_4_MAX_HEALTH_TAG,
                                Factory.TIER_4_MAX_UNITS_DEF, 5, Integer.MAX_VALUE);
                TIER_5_MAX_UNITS = COMMON_BUILDER
                        .comment("Max health for a tier 5 mob")
                        .defineInRange(ConfigPath.Factory.TIER_5_MAX_HEALTH_TAG,
                                Factory.TIER_5_MAX_UNITS_DEF, 5, Integer.MAX_VALUE);
            }
            COMMON_BUILDER.pop();

            COMMON_BUILDER.push(ConfigPath.Factory.CATEGORY_BASE);
            {
                MASS_COUNT = COMMON_BUILDER
                        .comment("Number of mobs to spawn")
                        .defineInRange(ConfigPath.Factory.MASS_COUNT_TAG,
                                Factory.MASS_COUNT_DEF, 1, 100);

                SPAWN_TICKS = COMMON_BUILDER
                        .comment("Number of ticks to spawn a mob")
                        .defineInRange(ConfigPath.Factory.SPAWN_TICKS_TAG,
                                Factory.SPAWN_TICKS_DEF, 1, 65535);
                UNITS_PER_HEALTH = COMMON_BUILDER
                        .comment("Number of units for each health")
                        .defineInRange(ConfigPath.Factory.MB_PER_HEALTH_TAG,
                                Factory.UNITS_PER_HEALTH_DEF, 1, 65535);
                MOB_SHARD_KILLS = COMMON_BUILDER
                        .comment("Number of kills to program the shard")
                        .defineInRange(ConfigPath.Factory.SHARD_KILLS_TAG,
                                Factory.MOB_SHARD_KILLS_DEF, 1, 65535);
            }
            COMMON_BUILDER.pop();

            COMMON_BUILDER.push(ConfigPath.Factory.CATEGORY_CELL1);
            {
                CELL_1_CAPACITY = COMMON_BUILDER
                        .comment(ConfigPath.Common.TANK_CAPACITY_COMMENT)
                        .worldRestart()
                        .defineInRange(ConfigPath.Common.TANK_CAPACITY_TAG,
                                Factory.CELL_1_CAPACITY_DEF, 1, Integer.MAX_VALUE);
                CELL_1_MAX_TRANSFER = COMMON_BUILDER
                        .comment(ConfigPath.Common.TANK_RX_COMMENT)
                        .worldRestart()
                        .defineInRange(ConfigPath.Common.TANK_RX_TAG,
                                Factory.CELL_1_MAX_TRANSFER_DEF, 1, Integer.MAX_VALUE);
            }
            COMMON_BUILDER.pop();

            COMMON_BUILDER.push(ConfigPath.Factory.CATEGORY_CELL2);
            {
                CELL_2_CAPACITY = COMMON_BUILDER
                        .comment(ConfigPath.Common.TANK_CAPACITY_COMMENT)
                        .worldRestart()
                        .defineInRange(ConfigPath.Common.TANK_CAPACITY_TAG,
                            Factory.CELL_2_CAPACITY_DEF, 1, Integer.MAX_VALUE);
                CELL_2_MAX_TRANSFER = COMMON_BUILDER
                        .comment(ConfigPath.Common.TANK_RX_COMMENT)
                        .worldRestart()
                        .defineInRange(ConfigPath.Common.TANK_RX_TAG,
                            Factory.CELL_2_MAX_TRANSFER_DEF, 1, Integer.MAX_VALUE);
            }
            COMMON_BUILDER.pop();

            COMMON_BUILDER.push(ConfigPath.Factory.CATEGORY_CELL3);
            {
                CELL_3_CAPACITY = COMMON_BUILDER
                        .comment(ConfigPath.Common.TANK_CAPACITY_COMMENT)
                        .worldRestart()
                        .defineInRange(ConfigPath.Common.TANK_CAPACITY_TAG,
                            Factory.CELL_3_CAPACITY_DEF, 1, Integer.MAX_VALUE);
                CELL_3_MAX_TRANSFER = COMMON_BUILDER
                        .comment(ConfigPath.Common.TANK_RX_COMMENT)
                        .worldRestart()
                        .defineInRange(ConfigPath.Common.TANK_RX_TAG,
                            Factory.CELL_3_MAX_TRANSFER_DEF, 1, Integer.MAX_VALUE);
            }
            COMMON_BUILDER.pop();

            COMMON_BUILDER.push(ConfigPath.Factory.CATEGORY_CELL4);
            {
                CELL_4_CAPACITY = COMMON_BUILDER
                        .comment(ConfigPath.Common.TANK_CAPACITY_COMMENT)
                        .worldRestart()
                        .defineInRange(ConfigPath.Common.TANK_CAPACITY_TAG,
                                Factory.CELL_4_CAPACITY_DEF, 1, Integer.MAX_VALUE);
                CELL_4_MAX_TRANSFER = COMMON_BUILDER
                        .comment(ConfigPath.Common.TANK_RX_COMMENT)
                        .worldRestart()
                        .defineInRange(ConfigPath.Common.TANK_RX_TAG,
                                Factory.CELL_4_MAX_TRANSFER_DEF, 1, Integer.MAX_VALUE);
            }
            COMMON_BUILDER.pop();

            COMMON_BUILDER.push(ConfigPath.Factory.CATEGORY_PERKS);
            {
                COMMON_BUILDER.push(ConfigPath.Factory.CATEGORY_EFFICIENCY);
                {
                    EFFICIENCY_1 = COMMON_BUILDER
                            .comment("Percentage reduction for efficiency 1 perks")
                            .defineInRange(ConfigPath.Factory.EFFICIENCY_1_TAG,
                                    Factory.EFFICIENCY_1_DEF, 0, 100);
                    EFFICIENCY_2 = COMMON_BUILDER
                            .comment("Percentage reduction for efficiency 2 perks")
                            .defineInRange(ConfigPath.Factory.EFFICIENCY_2_TAG,
                                    Factory.EFFICIENCY_2_DEF, 0, 100);
                    EFFICIENCY_3 = COMMON_BUILDER
                            .comment("Percentage reduction for efficiency 3 perks")
                            .defineInRange(ConfigPath.Factory.EFFICIENCY_3_TAG,
                                    Factory.EFFICIENCY_3_DEF, 0, 100);
                }
                COMMON_BUILDER.pop();

                COMMON_BUILDER.push(ConfigPath.Factory.CATEGORY_MASS);
                {
                    MASS_COUNT_1 = COMMON_BUILDER
                            .comment("Number of mobs to spawn for mass 1 perks")
                            .defineInRange(ConfigPath.Factory.MASS_1_TAG,
                                    Factory.MASS_COUNT_1_DEF, 1, 100);
                    MASS_COUNT_2 = COMMON_BUILDER
                            .comment("Number of mobs to spawn for mass 2 perks")
                            .defineInRange(ConfigPath.Factory.MASS_2_TAG,
                                    Factory.MASS_COUNT_2_DEF, 1, 100);
                    MASS_COUNT_3 = COMMON_BUILDER
                            .comment("Number of mobs to spawn for mass 3 perks")
                            .defineInRange(ConfigPath.Factory.MASS_3_TAG,
                                    Factory.MASS_COUNT_3_DEF, 1, 100);
                }
                COMMON_BUILDER.pop();

                COMMON_BUILDER.push(ConfigPath.Factory.CATEGORY_RATE);
                {
                    RATE_1 = COMMON_BUILDER
                            .comment("Percentage reduction in spawn time for rate 1 perks")
                            .defineInRange(ConfigPath.Factory.RATE_1_TAG,
                                    Factory.RATE_1_DEF, 1, 99);
                    RATE_2 = COMMON_BUILDER
                            .comment("Percentage reduction in spawn time for rate 2 perks")
                            .defineInRange(ConfigPath.Factory.RATE_2_TAG,
                                    Factory.RATE_2_DEF, 1, 99);
                    RATE_3 = COMMON_BUILDER
                            .comment("Percentage reduction in spawn time for rate 3 perks")
                            .defineInRange(ConfigPath.Factory.RATE_3_TAG,
                                    Factory.RATE_3_DEF, 1, 99);
                }
                COMMON_BUILDER.pop();

                COMMON_BUILDER.push(ConfigPath.Factory.CATEGORY_TIER_SHARD);
                {
                    TIER_SHARD_1 = COMMON_BUILDER
                            .comment("Number of chance to generate a shard for tier shard 1 perk")
                            .defineInRange(ConfigPath.Factory.TIER_SHARD_L1_ROLLS_TAG,
                                    Factory.TIER_SHARD_1_DEF, 0, Integer.MAX_VALUE);
                    TIER_SHARD_2 = COMMON_BUILDER
                            .comment("Number of chance to generate a shard for tier shard 2 perk")
                            .defineInRange(ConfigPath.Factory.TIER_SHARD_L2_ROLLS_TAG,
                                    Factory.TIER_SHARD_2_DEF, 0, Integer.MAX_VALUE);
                    TIER_SHARD_3 = COMMON_BUILDER
                            .comment("Number of chance to generate a shard for tier shard 3 perk")
                            .defineInRange(ConfigPath.Factory.TIER_SHARD_L3_ROLLS_TAG,
                                    Factory.TIER_SHARD_3_DEF, 0, Integer.MAX_VALUE);

                    T1_FARM_DROP_CHANCE = COMMON_BUILDER
                            .comment("Chance to drop a tier shard from a Tier 1 factory")
                            .defineInRange(ConfigPath.Factory.TIER_SHARD_T1_DROP_TAG,
                                    Factory.T1_SHARD_DROP_CHANCE_DEF, 0.0F, 100.0F);
                    T2_FARM_DROP_CHANCE = COMMON_BUILDER
                            .comment("Chance to drop a tier shard from a Tier 2 factory")
                            .defineInRange(ConfigPath.Factory.TIER_SHARD_T2_DROP_TAG,
                                    Factory.T2_SHARD_DROP_CHANCE_DEF, 0.0F, 100.0F);
                    T3_FARM_DROP_CHANCE = COMMON_BUILDER
                            .comment("Chance to drop a tier shard from a Tier 3 factory")
                            .defineInRange(ConfigPath.Factory.TIER_SHARD_T3_DROP_TAG,
                                    Factory.T3_SHARD_DROP_CHANCE_DEF, 0.0F, 100.0F);
                    T4_FARM_DROP_CHANCE = COMMON_BUILDER
                            .comment("Chance to drop a tier shard from a Tier 4 factory")
                            .defineInRange(ConfigPath.Factory.TIER_SHARD_T4_DROP_TAG,
                                    Factory.T4_SHARD_DROP_CHANCE_DEF, 0.0F, 100.0F);
                    T5_FARM_DROP_CHANCE = COMMON_BUILDER
                            .comment("Chance to drop a tier shard from a Tier 5 factory")
                            .defineInRange(ConfigPath.Factory.TIER_SHARD_T5_DROP_TAG,
                                    Factory.T5_SHARD_DROP_CHANCE_DEF, 0.0F, 100.0F);

                    T1_FARM_DROP_SHARD_WEIGHTS = COMMON_BUILDER
                            .comment("Weights of the basic, advanced, elite shard from a Tier 1 factory")
                            .define(ConfigPath.Factory.TIER_SHARD_T1_WEIGHTS_TAG,
                                    Factory.T1_SHARD_DROP_WEIGHTS_DEF);
                    T2_FARM_DROP_SHARD_WEIGHTS = COMMON_BUILDER
                            .comment("Weights of the basic, advanced, elite shard from a Tier 2 factory")
                            .define(ConfigPath.Factory.TIER_SHARD_T2_WEIGHTS_TAG,
                                    Factory.T2_SHARD_DROP_WEIGHTS_DEF);
                    T3_FARM_DROP_SHARD_WEIGHTS = COMMON_BUILDER
                            .comment("Weights of the basic, advanced, elite shard from a Tier 3 factory")
                            .define(ConfigPath.Factory.TIER_SHARD_T3_WEIGHTS_TAG,
                                    Factory.T3_SHARD_DROP_WEIGHTS_DEF);
                    T4_FARM_DROP_SHARD_WEIGHTS = COMMON_BUILDER
                            .comment("Weights of the basic, advanced, elite shard from a Tier 4 factory")
                            .define(ConfigPath.Factory.TIER_SHARD_T4_WEIGHTS_TAG,
                                    Factory.T4_SHARD_DROP_WEIGHTS_DEF);
                    T5_FARM_DROP_SHARD_WEIGHTS = COMMON_BUILDER
                            .comment("Weights of the basic, advanced, elite shard from a Tier 5 factory")
                            .define(ConfigPath.Factory.TIER_SHARD_T5_WEIGHTS_TAG,
                                    Factory.T5_SHARD_DROP_WEIGHTS_DEF);
                }
                COMMON_BUILDER.pop();

                COMMON_BUILDER.push(ConfigPath.Factory.CATEGORY_XP);
                {
                    XP_1 = COMMON_BUILDER
                            .comment("Percentage generate of XP for xp 1 perks")
                            .defineInRange(ConfigPath.Factory.XP_1_TAG,
                                    Factory.XP_1_DEF, 0, 1000);
                    XP_2 = COMMON_BUILDER
                            .comment("Percentage generate of XP for xp 2 perks")
                            .defineInRange(ConfigPath.Factory.XP_2_TAG,
                                    Factory.XP_2_DEF, 0, 1000);
                    XP_3 = COMMON_BUILDER
                            .comment("Percentage generate of XP for xp 3 perks")
                            .defineInRange(ConfigPath.Factory.XP_3_TAG,
                                    Factory.XP_3_DEF, 0, 1000);
                }
                COMMON_BUILDER.pop();

            }
            COMMON_BUILDER.pop();
        }
        CLIENT_BUILDER.pop();
        COMMON_BUILDER.pop();
    }
}
