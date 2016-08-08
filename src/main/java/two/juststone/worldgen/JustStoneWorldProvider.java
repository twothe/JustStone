/*
 *  Created by Stefan Feldbinder
 */
package two.juststone.worldgen;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.chunk.IChunkProvider;
import two.juststone.JustStone;
import two.juststone.spawngen.ISpawnGenerator;

/**
 *
 * @author Stefan Feldbinder<sfeldbin@googlemail.com>
 */
public class JustStoneWorldProvider extends WorldProviderSurface {

  public JustStoneWorldProvider() {
    this.hasNoSky = JustStone.bedrockTop || (JustStone.groundLevel >= 255);
  }

  @Override
  public ChunkCoordinates getRandomizedSpawnPoint() {
    final ChunkCoordinates chunkcoordinates = new ChunkCoordinates(this.worldObj.getSpawnPoint());
    JustStoneWorldData worldData = (JustStoneWorldData) this.worldObj.perWorldStorage.loadData(JustStoneWorldData.class, JustStoneWorldData.STR_JUST_WORLD_DATA);
    if (worldData == null) {
      worldData = new JustStoneWorldData();
    }
    if (worldData.isSpawnCreated() == false) {
      while ((chunkcoordinates.posY >= ISpawnGenerator.SPAWN_Y_MIN) && this.worldObj.isAirBlock(chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ)) {
        chunkcoordinates.posY -= 1;
      }
      JustStone.spawnGenerator.generateSpawnArea(this.worldObj, chunkcoordinates);
      worldData.setSpawnCreated(true);
      this.worldObj.perWorldStorage.setData(JustStoneWorldData.STR_JUST_WORLD_DATA, worldData);
      this.worldObj.perWorldStorage.saveAllData();
    }
    return chunkcoordinates;
  }

  @Override
  public IChunkProvider createChunkGenerator() {
    return new JustStoneTerrainGenerator(this.worldObj, this.worldObj.getSeed(), JustStone.defaultBiomeID);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean getWorldHasVoidParticles() {
    return false;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public float getCloudHeight() {
    return 256.0f;
  }
}
