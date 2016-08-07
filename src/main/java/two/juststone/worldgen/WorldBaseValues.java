/**
 * @author Two
 */
package two.juststone.worldgen;

public class WorldBaseValues {

  /* The length of each side of a chunk */
  public static final int CHUNK_SIZE_X = 16;
  public static final int CHUNK_SIZE_Z = 16;
  /* The 2D area of a chunk, which is equal to the map sizes */
  public static final int CHUNK_SIZE_XZ = CHUNK_SIZE_X * CHUNK_SIZE_Z;

  /* The height of the first block above ocean water.<br>
   * This means that groundLevel - 1 is either a water block (ocean) or a non-water block (shore). */
  public final int groundLevel;

  public final int worldHeight;

  /**
   * Created by the JustStone mod during player login.
   */
  public WorldBaseValues(final int groundLevel, final int worldHeight) {
    this.groundLevel = groundLevel;
    this.worldHeight = worldHeight;
  }
}
