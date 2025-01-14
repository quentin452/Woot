package ipsis.woot.configuration;

import ipsis.woot.util.WootMobName;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class MobHealthManager implements IMobCost {

    private Map<String, Integer> mobHealthMap = new HashMap<>();
    private Map<String, Boolean> animalMap = new HashMap<>();

    private String makeKey(WootMobName wootMobName) {

        return wootMobName.getName();
    }

    /**
     * IMobCost
     */
    @Override
    public int getMobSpawnCost(@Nonnull World world, @Nonnull WootMobName wootMobName) {

        int cost = 65535;
        if (wootMobName.isValid()) {
            String key = makeKey(wootMobName);
            if (mobHealthMap.containsKey(key)) {
                cost = mobHealthMap.get(key);
            } else {
                Entity entity = EntityList.createEntityByIDFromName(wootMobName.getResourceLocation(), world);
                if (entity != null && entity instanceof EntityLiving) {
                    cost = (int)((EntityLiving)entity).getMaxHealth();
                    mobHealthMap.put(key, cost);
                    if (entity instanceof EntityAnimal)
                        animalMap.put(key, true);
                    else
                        animalMap.put(key, false);
                }
            }
        }
        return cost;
    }

    public boolean isAnimal(@Nonnull World world, @Nonnull WootMobName wootMobName) {

        boolean animal = false;
        if (wootMobName.isValid()) {
            String key = makeKey(wootMobName);
            if (animalMap.containsKey(key))
                animal = animalMap.get(key);
            else {
                getMobSpawnCost(world, wootMobName);
                if (animalMap.containsKey(key))
                    animal = animalMap.get(key);
            }
        }

        return animal;
    }
}
