/*
 * Copyright (c) by Stefan Feldbinder aka Two
 */
package two.juststone;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.DimensionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;
import two.juststone.spawngen.ISpawnGenerator;
import two.juststone.spawngen.TreePlatformGenerator;

/**
 *
 * @author Two
 */
@Mod(modid = JustStone.MOD_ID, name = JustStone.MOD_NAME, version = JustStone.MOD_VERSION)
public class JustStone {

  public static final String MOD_NAME = "Just Stone";
  public static final String MOD_ID = "juststone";
  public static final String MOD_VERSION = "1710.1.0";
  /* Global logger that uses string format type logging */
  public static final Logger log = LogManager.getLogger(JustStone.class.getSimpleName(), new StringFormatterMessageFactory());

  @Mod.Instance("JustStone")
  public static JustStone instance;
  public static final Config config = new Config();
  public static int defaultBiomeID = 1;
  public static boolean bedrockTop = true;
  public static boolean bedrockBottom = true;
  public static boolean generateCaves = true;
  public static boolean generateRavines = true;
  public static boolean generateMines = true;
  public static int groundLevel = 255;
  public static ISpawnGenerator spawnGenerator;

  @Mod.EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    config.initialize(event.getSuggestedConfigurationFile());
  }

  @Mod.EventHandler
  public void load(FMLInitializationEvent event) {
    config.load();
    JustStone.defaultBiomeID = config.getMiscInteger("Default Biome ID", 1, "The biome ID to use for world generation, defaults to Plains.\nChange this only if you need a certain biome in your world to exist.");
    if (JustStone.defaultBiomeID <= 0) {
      JustStone.defaultBiomeID = 1;
    } else if (JustStone.defaultBiomeID > 255) {
      JustStone.defaultBiomeID = 1;
    }
    JustStone.bedrockTop = config.getMiscBoolean("Bedrock on top", true, "Whether or not to add bedrock on top of the world. Will prevent spawning of animals if set to true.");
    JustStone.bedrockBottom = config.getMiscBoolean("Bedrock at bottom", true, "Whether or not to add bedrock at the bottom of the world.");
    JustStone.groundLevel = config.getMiscInteger("Ground level", 255, "Up to which height shall the world be filled with stone?\n0 means the world is empty, 255 (max) means the world is filled up to the top (which also prevents spawning of animals).");
    if (JustStone.groundLevel <= 0) {
      JustStone.groundLevel = 0;
    } else if (JustStone.groundLevel > 255) {
      JustStone.groundLevel = 255;
    }
    JustStone.generateCaves = config.getMiscBoolean("Generate caves", true, "Whether or not to generate caves.");
    JustStone.generateRavines = config.getMiscBoolean("Generate ravines", true, "Whether or not to generate ravines.");
    JustStone.generateMines = config.getMiscBoolean("Generate mines", true, "Whether or not to generate mines.");

    switch (config.getMiscInteger("Spawn Type", 0, "Type of spawn point to generate.\n  0 - (default) 7x7x7 area with tree, grass and torches.")) {
      case 0:
      default:
        JustStone.spawnGenerator = new TreePlatformGenerator();
        break;
    }
    config.save();

    if (!DimensionManager.registerProviderType(0, JustStoneWorldProvider.class, true)) {
      DimensionManager.unregisterProviderType(0);
      if (!DimensionManager.registerProviderType(0, JustStoneWorldProvider.class, true)) {
        log.error("Unable to replace overworld provider! Cannot define spawn point!");
      }
    }
  }

  @Mod.EventHandler
  public void postInit(FMLPostInitializationEvent event) {
  }
}
