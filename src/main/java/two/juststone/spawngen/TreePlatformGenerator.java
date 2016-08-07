/*
 *  Created by Stefan Feldbinder
 */
package two.juststone.spawngen;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenTrees;

/**
 *
 * @author Stefan Feldbinder<sfeldbin@googlemail.com>
 */
public class TreePlatformGenerator implements ISpawnGenerator {

  @Override
  public void generateSpawnArea(final World world, final ChunkCoordinates chunkcoordinates) {
    if (chunkcoordinates.posY < SPAWN_Y_MIN) {
      chunkcoordinates.posY = SPAWN_Y_MIN;
    } else if (chunkcoordinates.posY > SPAWN_Y_MAX) {
      chunkcoordinates.posY = SPAWN_Y_MAX;
    }
    for (int x = chunkcoordinates.posX - 3; x < chunkcoordinates.posX + 4; ++x) {
      for (int z = chunkcoordinates.posZ - 3; z < chunkcoordinates.posZ + 4; ++z) {
        for (int y = chunkcoordinates.posY; y < chunkcoordinates.posY + 8; ++y) {
          if (y == chunkcoordinates.posY) {
            final Block block = (x >= chunkcoordinates.posX - 2 && x <= chunkcoordinates.posX + 2 && z >= chunkcoordinates.posZ - 2 && z <= chunkcoordinates.posZ + 2)
                    ? Blocks.grass
                    : Blocks.stone;
            world.setBlock(x, y, z, block, 0, 3);
          } else {
            world.setBlockToAir(x, y, z);
          }
        }
      }
    }
    final WorldGenTrees treeGen = new WorldGenTrees(true);
    boolean putSapling = true;
    for (int tries = 10; tries > 0; --tries) {
      if (treeGen.generate(world, world.rand, chunkcoordinates.posX, chunkcoordinates.posY + 1, chunkcoordinates.posZ)) {
        putSapling = false;
        break;
      }
    }
    if (putSapling) { // we tried
      world.setBlock(chunkcoordinates.posX, chunkcoordinates.posY + 1, chunkcoordinates.posZ, Blocks.sapling, 8, 3);
    }
    world.setBlock(chunkcoordinates.posX - 2, chunkcoordinates.posY + 1, chunkcoordinates.posZ - 2, Blocks.torch, 0, 3);
    world.setBlock(chunkcoordinates.posX + 2, chunkcoordinates.posY + 1, chunkcoordinates.posZ - 2, Blocks.torch, 0, 3);
    world.setBlock(chunkcoordinates.posX - 2, chunkcoordinates.posY + 1, chunkcoordinates.posZ + 2, Blocks.torch, 0, 3);
    world.setBlock(chunkcoordinates.posX + 2, chunkcoordinates.posY + 1, chunkcoordinates.posZ + 2, Blocks.torch, 0, 3);

    chunkcoordinates.posY += 1;
    chunkcoordinates.posZ -= 3;
  }

}
