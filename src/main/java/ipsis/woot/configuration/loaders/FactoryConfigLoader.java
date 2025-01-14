package ipsis.woot.configuration.loaders;

import com.google.gson.*;
import ipsis.Woot;
import ipsis.woot.configuration.EnumConfigKey;
import ipsis.woot.configuration.IWootConfiguration;
import ipsis.woot.oss.FileUtils;
import ipsis.woot.oss.LogHelper;
import ipsis.woot.reference.Files;
import ipsis.woot.util.JsonHelper;
import ipsis.woot.util.WootMobName;
import ipsis.woot.util.WootMobNameBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;

import static ipsis.woot.util.JsonHelper.getItemStack;

public class FactoryConfigLoader {

    private static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private IWootConfiguration wootConfiguration;

    public void loadConfig(IWootConfiguration wootConfiguration) {

        this.wootConfiguration = wootConfiguration;

        try {
            String configText = FileUtils.copyConfigFileFromJar(Files.FACTORY_CFG_FILENAME, false);
            JsonObject jsonObject = JsonUtils.fromJson(GSON, configText, JsonObject.class, false);
            parseConfig(jsonObject);
        } catch (Exception e) {
            LogHelper.error("Could not load factory config from " +
                FileUtils.getConfigFile(Files.FACTORY_CFG_FILENAME).getAbsolutePath());
            e.printStackTrace();
        }
    }

    private void parseConfig(JsonObject json) {

        if (json == null || json.isJsonNull())
            throw new JsonSyntaxException("Json cannot be null");

        int version = JsonUtils.getInt(json, "version", -1);
        if (version == -1 || version != 1)
            throw new JsonSyntaxException("Invalid version");

        for (JsonElement ele : JsonUtils.getJsonArray(json, "mobconfigs")) {
            parseMobConfigs(ele);
        }

        // blacklist all entities from mods
        JsonElement ele = json.get("entitymodblacklist");
        if (ele == null || !ele.isJsonArray())
            throw new JsonSyntaxException("entitymodblacklist: must a string list");

        String entityMods[] = GSON.fromJson(ele, String[].class);
        for (String mod : entityMods)
            Woot.policyRepository.addModToEntityList(mod, false);

        // blacklist specific entity
        ele = json.get("entityblacklist");
        if (ele == null || !ele.isJsonArray())
            throw new JsonSyntaxException("entityblacklist must a string list");

        String mobs[] = GSON.fromJson(ele, String[].class);
        for (String mob : mobs) {
            WootMobName wootMobName = WootMobNameBuilder.createFromConfigString(mob);
            if (wootMobName.isValid())
                Woot.policyRepository.addEntityToEntityList(wootMobName, false);
            else
                LogHelper.warn("Entity blacklist: invalid mob name " + mob);
        }

        // blacklist specific entity
        ele = json.get("entitygenerateonlylist");
        if (ele == null || !ele.isJsonArray())
            throw new JsonSyntaxException("entitygenerateonly must a string list");

        mobs = GSON.fromJson(ele, String[].class);
        for (String mob : mobs) {
            WootMobName wootMobName = WootMobNameBuilder.createFromConfigString(mob);
            if (wootMobName.isValid())
                Woot.policyRepository.addEntityToGenerateOnlyList(wootMobName);
            else
                LogHelper.warn("Generate only list: invalid mob name " + mob);
        }

        // whitelist of mobs
        ele = json.get("entitywhitelist");
        if (ele == null || !ele.isJsonArray())
            throw new JsonSyntaxException("entitywhitelist must a string list");

        mobs = GSON.fromJson(ele, String[].class);
        for (String mob : mobs) {
            WootMobName wootMobName = WootMobNameBuilder.createFromConfigString(mob);
            if (wootMobName.isValid())
                Woot.policyRepository.addEntityToEntityWhitelist(wootMobName);
            else
                LogHelper.warn("Entity whitelist: invalid mob name " + mob);
        }

        // blacklist all items from mods
        ele = json.get("itemmodblacklist");
        if (ele == null || !ele.isJsonArray())
            throw new JsonSyntaxException("itemmodblacklist: must a string list");

        String mods[] = GSON.fromJson(ele, String[].class);
        for (int i = 0; i < mods.length; i++) {
            Woot.policyRepository.addModToDropList(mods[i], false);
        }

        // blacklist specific item
        for (JsonElement ele2 : JsonUtils.getJsonArray(json, "itemblacklist")) {
            if (ele2 == null || !ele2.isJsonObject())
                throw new JsonSyntaxException("Blacklisted item must be an object");

            JsonObject itemObject = (JsonObject)ele2;
            ItemStack itemStack = getItemStack(itemObject);
            if (itemStack.isEmpty()) {
                LogHelper.info("Item blacklist: invalid item");
                continue;
            }

            Woot.policyRepository.addItemToDropList(itemStack, false);
        }
    }

    private void parseMobConfigs(JsonElement ele) {

        if (ele == null || !ele.isJsonObject())
            throw new JsonSyntaxException("Mob configs must be object");

        JsonObject json = (JsonObject)ele;

        WootMobName wootMobName = JsonHelper.getWootMobName(json);
        if (wootMobName.isValid()) {
            for (JsonElement ele2 : JsonUtils.getJsonArray(json, "configs")) {

                if (ele2 == null || !ele.isJsonObject())
                    throw new JsonSyntaxException("Mob config must be object");

                JsonObject json2 = (JsonObject) ele2;
                String tag = JsonUtils.getString(json2, "tag", "");
                int value = JsonUtils.getInt(json2, "value", -1);

                if (tag.equals(""))
                    throw new JsonSyntaxException("Tag cannot be empty");

                if (value >= 0) {
                    EnumConfigKey configKey = EnumConfigKey.get(tag);
                    if (configKey == null) {
                        LogHelper.error(wootMobName + " unknown config key " + tag);
                    } else {
                        wootConfiguration.setInteger(wootMobName, configKey, value);
                        LogHelper.info(wootMobName + "/" + configKey + "/" + value);
                    }
                } else {
                    LogHelper.error(wootMobName + " Value must be >= 0");
                }
            }
        }
    }

}
