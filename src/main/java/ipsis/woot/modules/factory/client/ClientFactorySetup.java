package ipsis.woot.modules.factory.client;

import io.netty.buffer.ByteBuf;
import ipsis.woot.modules.factory.MobParam;
import ipsis.woot.modules.factory.Perk;
import ipsis.woot.modules.factory.Tier;
import ipsis.woot.util.FakeMob;
import ipsis.woot.util.oss.NetworkTools;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientFactorySetup {

    public Tier tier = Tier.TIER_1;
    public List<FakeMob> controllerMobs = new ArrayList<>();
    public List<Perk> perks = new ArrayList<>();
    public HashMap<FakeMob, MobParam> mobParams = new HashMap<>();
    public HashMap<FakeMob, Mob> mobInfo = new HashMap<>();
    public int cellCapacity = 0;
    public int looting = 0;
    public int recipeTicks = 0;
    public int recipeFluid = 0;

    public static class Mob {
        public List<ItemStack> itemIngredients = new ArrayList<>();
        public List<FluidStack> fluidIngredients = new ArrayList<>();
        public List<ItemStack> drops = new ArrayList<>();
    }

    private ClientFactorySetup() {}

    public static ClientFactorySetup fromBytes(ByteBuf buf) {

        ClientFactorySetup factorySetup = new ClientFactorySetup();

        factorySetup.tier = Tier.byIndex(buf.readInt());
        factorySetup.cellCapacity = buf.readInt();
        buf.readInt(); /// fluid amount
        factorySetup.looting = buf.readInt();

        factorySetup.recipeTicks = buf.readInt();
        factorySetup.recipeFluid = buf.readInt();

        int mobCount = buf.readInt();
        for (int x = 0; x < mobCount; x++) {
            String mobString = NetworkTools.readString(buf);
            FakeMob fakeMob = new FakeMob(mobString);
            MobParam mobParam = new MobParam();
            mobParam.baseSpawnTicks = buf.readInt();
            mobParam.baseMassCount = buf.readInt();
            mobParam.baseFluidCost = buf.readInt();
            mobParam.perkRateValue = buf.readInt();
            mobParam.perkEfficiencyValue = buf.readInt();
            mobParam.perkMassValue = buf.readInt();
            mobParam.perkXpValue = buf.readInt();
            factorySetup.controllerMobs.add(fakeMob);
            factorySetup.mobParams.put(fakeMob, mobParam);

            Mob mob = new Mob();
            int itemCount = buf.readInt();
            for (int y = 0; y < itemCount; y++)
                mob.itemIngredients.add(NetworkTools.readItemStack(buf));

            int fluidCount = buf.readInt();
            for (int y = 0; y < fluidCount; y++) {
                //mob.fluidIngredients.add(NetworkTools.readItemStack(buf));
            }

            int drops = buf.readInt();
            for (int y = 0; y < drops; y++)
                mob.drops.add(NetworkTools.readItemStack(buf));

            factorySetup.mobInfo.put(fakeMob, mob);
        }

        int perkCount = buf.readInt();
        for (int x = 0; x < perkCount; x++)
            factorySetup.perks.add(Perk.getPerks(buf.readInt()));

        return factorySetup;
    }
}