/*
 */
package two.juststone;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * @author Two
 */
public class Config {

  protected static final String CATEGORY_ALLOWED_RECIPES = "Allowed Recipes";
  protected static final String CATEGORY_VARIOUS_SETTINGS = "Settings";
  protected static final String CATEGORY_THAUMCRAFT_SETTINGS = "Thaumcraft Compatibility";
  //--- Class ------------------------------------------------------------------
  protected static final String PREFIX_TILE = "tile."; // as in block.getUnlocalizedName()
  protected Configuration configuration;

  protected Config() {
  }

  protected void initialize(final File configFile) {
    configuration = new Configuration(configFile);
  }

  protected void load() {
    configuration.load();
  }

  protected void save() {
    configuration.save();
  }

  public boolean isCraftingEnabled(final String key) {
    return isCraftingEnabled(key, true);
  }

  public boolean isCraftingEnabled(final String key, final boolean defaultValue) {
    final Property property = configuration.get(CATEGORY_ALLOWED_RECIPES, key, defaultValue);
    return property.getBoolean(defaultValue);
  }

  public int getMiscInteger(final String key, final int defaultValue) {
    return getMiscInteger(key, defaultValue, null);
  }

  public int getMiscInteger(final String key, final int defaultValue, final String comment) {
    final Property property = configuration.get(CATEGORY_VARIOUS_SETTINGS, key, defaultValue, comment);
    return property.getInt(defaultValue);
  }

  public double getMiscDouble(final String key, final double defaultValue) {
    return getMiscDouble(key, defaultValue, null);
  }

  public double getMiscDouble(final String key, final double defaultValue, final String comment) {
    final Property property = configuration.get(CATEGORY_VARIOUS_SETTINGS, key, defaultValue, comment);
    return property.getDouble(defaultValue);
  }

  public boolean getMiscBoolean(final String key, final boolean defaultValue) {
    return getMiscBoolean(key, defaultValue, null);
  }

  public boolean getMiscBoolean(final String key, final boolean defaultValue, final String comment) {
    final Property property = configuration.get(CATEGORY_VARIOUS_SETTINGS, key, defaultValue, comment);
    return property.getBoolean(defaultValue);
  }

  public List<String> getMiscStrings(final String key, final List<String> defaultValue) {
    return getMiscStrings(key, defaultValue, null);
  }

  public List<String> getMiscStrings(final String key, final List<String> defaultValue, final String comment) {
    final Property property = configuration.get(CATEGORY_VARIOUS_SETTINGS, key, defaultValue.toArray(new String[defaultValue.size()]), comment);
    return Arrays.asList(property.getStringList());
  }

  public Configuration getConfiguration() {
    return configuration;
  }

}
