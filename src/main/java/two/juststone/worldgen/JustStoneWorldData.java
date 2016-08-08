/*
 *  Created by Stefan Feldbinder
 */
package two.juststone.worldgen;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

/**
 *
 * @author Stefan Feldbinder<sfeldbin@googlemail.com>
 */
public final class JustStoneWorldData extends WorldSavedData {

  public static final String STR_JUST_WORLD_DATA = "JustStoneWorldData";
  private static final String STR_JW_SPAWN_CREATED = "STR_JW_SPAWN_CREATED";
  private boolean spawnCreated = false;

  public JustStoneWorldData() {
    this(STR_JUST_WORLD_DATA);
  }

  public JustStoneWorldData(final String name) {
    super(name);
  }

  @Override
  public void readFromNBT(final NBTTagCompound tagCompound) {
    spawnCreated = tagCompound.getBoolean(STR_JW_SPAWN_CREATED);
  }

  @Override
  public void writeToNBT(final NBTTagCompound tagCompound) {
    tagCompound.setBoolean(STR_JW_SPAWN_CREATED, spawnCreated);
  }

  public boolean isSpawnCreated() {
    return spawnCreated;
  }

  public void setSpawnCreated(final boolean spawnCreated) {
    if (this.spawnCreated != spawnCreated) {
      this.spawnCreated = spawnCreated;
      this.markDirty();
    }
  }

}
