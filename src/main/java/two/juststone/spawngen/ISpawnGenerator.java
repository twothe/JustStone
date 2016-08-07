/*
 *  Created by Stefan Feldbinder
 */
package two.juststone.spawngen;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

/**
 *
 * @author Stefan Feldbinder<sfeldbin@googlemail.com>
 */
public interface ISpawnGenerator {

  public static final int SPAWN_Y_MIN = 2;
  public static final int SPAWN_Y_MAX = 256 - 8;

  public void generateSpawnArea(final World world, final ChunkCoordinates chunkcoordinates);
}
