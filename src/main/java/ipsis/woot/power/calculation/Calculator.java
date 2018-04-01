package ipsis.woot.power.calculation;

import ipsis.Woot;
import ipsis.woot.configuration.EnumConfigKey;
import ipsis.woot.farmstructure.IFarmSetup;
import ipsis.woot.farming.PowerRecipe;
import ipsis.woot.util.ConfigKeyHelper;
import ipsis.woot.util.DebugSetup;
import ipsis.woot.util.WootMobName;
import net.minecraft.world.World;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Calculator implements IPowerCalculator {

    private List<AbstractUpgradePowerCalc> powerCalcList = new ArrayList<>();

    public Calculator() {

        powerCalcList.add(new UpgradePowerCalcDecapitate());
        powerCalcList.add(new UpgradePowerCalcEfficiency());
        powerCalcList.add(new UpgradePowerCalcLooting());
        powerCalcList.add(new UpgradePowerCalcMass());
        powerCalcList.add(new UpgradePowerCalcRate());
        powerCalcList.add(new UpgradePowerCalcXp());
    }

    /**
     * IPowerCalculator
     */
    @Override
    public PowerRecipe calculate(World world, IFarmSetup farmSetup) {

        WootMobName wootMobName = farmSetup.getWootMobName();
        Woot.debugSetup.trace(DebugSetup.EnumDebugType.POWER_CALC, "calculate", wootMobName);

        PowerValues powerValues = new PowerValues();

        int tierPowerPerTick = Woot.wootConfiguration.getInteger(wootMobName, ConfigKeyHelper.getPowerPerTick(farmSetup.getFarmTier()));
        int spawnTicks = Woot.wootConfiguration.getInteger(wootMobName, EnumConfigKey.SPAWN_TICKS);
        int spawnUnits = Woot.wootConfiguration.getSpawnCost(world, wootMobName);
        int powerPerSpawnUnit = Woot.wootConfiguration.getInteger(wootMobName, EnumConfigKey.POWER_PER_UNIT);

        Woot.debugSetup.trace(DebugSetup.EnumDebugType.POWER_CALC, "calculate", "####");
        Woot.debugSetup.trace(DebugSetup.EnumDebugType.POWER_CALC, "calculate",
                "tierPowerPerTick:" + tierPowerPerTick + " spawnUnits:" + spawnUnits +
                      " powerPerSpawnUnit:" + powerPerSpawnUnit + " spawnTicks:" + spawnTicks);

        /**
         * Power calculation has multiple components
         *
         * spawnTicks is the base number of ticks and will differ depending on the mob
         * All power is based of this spawnTicks value and cannot be modified by upgrades
         *
         * 1. how much to run the base factory for spawnTicks
         * 2. how much to generate one mob
         * 3. how much to power the upgrades for spawnTicks
         *
         * Efficiency decreases the total power needed
         * Rate decreases the real time to spawn
         */
        BigInteger factoryCost = BigInteger.valueOf(tierPowerPerTick);
        factoryCost = factoryCost.multiply(BigInteger.valueOf(spawnTicks));

        BigInteger mobCost = BigInteger.valueOf(spawnUnits);
        mobCost = mobCost.multiply(BigInteger.valueOf(powerPerSpawnUnit));
        mobCost = mobCost.multiply(BigInteger.valueOf(spawnTicks));

        powerValues.factoryCost = factoryCost.longValue();
        powerValues.mobCost = mobCost.longValue();
        for (AbstractUpgradePowerCalc calc : powerCalcList)
            calc.calculate(farmSetup, powerValues, spawnTicks);

        /**
         * The calculator converts mobCost into upgradeCost as it has to handle the mass upgrade
         */
        BigInteger bigTotalPower = BigInteger.valueOf(powerValues.factoryCost);
        bigTotalPower.add(BigInteger.valueOf(powerValues.upgradeCost));

        long totalPower = bigTotalPower.longValue();

        Woot.debugSetup.trace(DebugSetup.EnumDebugType.POWER_CALC, "calculate",
                "PowerValues " + powerValues);
        Woot.debugSetup.trace(DebugSetup.EnumDebugType.POWER_CALC, "calculate",
                "totalPower:" + totalPower);

        /**
         * Efficiency saves you a percentage of the total
         */
        if (powerValues.efficiency != 0)
            totalPower -= ((long) ((totalPower / 100.0F) * powerValues.efficiency));

        Woot.debugSetup.trace(DebugSetup.EnumDebugType.POWER_CALC, "calculate",
            "efficiency:" + powerValues.efficiency  +  "=" + totalPower);

        /**
         * Spawn rate reduces the spawn time by a percentage
         */
        int finalSpawnTicks = spawnTicks;
        if (powerValues.rate != 0)
            finalSpawnTicks -= ((int)((spawnTicks / 100.0F) * powerValues.rate));

        Woot.debugSetup.trace(DebugSetup.EnumDebugType.POWER_CALC, "calculate",
                    "rate:" + powerValues.rate  + "=" + finalSpawnTicks);

        if (finalSpawnTicks <= 0)
            finalSpawnTicks = 1;

        PowerRecipe powerRecipe = new PowerRecipe(finalSpawnTicks, totalPower);

        Woot.debugSetup.trace(DebugSetup.EnumDebugType.POWER_CALC, "calculate", "recipe: " + powerRecipe);

        return powerRecipe;
    }

    public class PowerValues {

        public long factoryCost = 0;
        public long mobCost = 0;
        public long upgradeCost = 0;
        public long efficiency = 0; /* default to no saving */
        public long rate = 0; /* default to no saving */

        @Override
        public String toString() {

            return "factoryCost:" + factoryCost + " (base)mobCost:" + mobCost + " (with mobs)upgradeCost:" + upgradeCost + " efficiency:" + efficiency + " rate:" + rate;
        }
    }

}
