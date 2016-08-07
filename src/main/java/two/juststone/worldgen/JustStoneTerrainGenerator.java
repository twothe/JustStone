/*
 * Copyright (c) by Stefan Feldbinder aka Two
 */
package two.juststone.worldgen;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraftforge.common.MinecraftForge;
import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.CAVE;
import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.MINESHAFT;
import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.RAVINE;
import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.SCATTERED_FEATURE;
import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.STRONGHOLD;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.DUNGEON;
import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAKE;
import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAVA;
import net.minecraftforge.event.terraingen.TerrainGen;
import two.juststone.JustStone;

/**
 *
 * @author Two
 */
public class JustStoneTerrainGenerator implements IChunkProvider {

  /**
   * Reference to the World object.
   */
  private final World worldObj;
  private final MapGenBase caveGenerator;
  /**
   * Holds Stronghold Generator
   */
  private final MapGenStronghold strongholdGenerator;
  /**
   * Holds Mineshaft Generator
   */
  private final MapGenMineshaft mineshaftGenerator;
  private final MapGenScatteredFeature scatteredFeatureGenerator;
  /**
   * Holds ravine generator
   */
  private final MapGenBase ravineGenerator;
  protected final Random seedRandom;
  protected final WorldBaseValues baseValues;
  protected final Block[] defaultChunkData;
  protected final byte[] defaultBiomeData;

  public JustStoneTerrainGenerator(final World world, final long worldSeed, final int defaultBiomeID) {
    this.baseValues = new WorldBaseValues(JustStone.groundLevel, world.provider.getHeight());
    this.seedRandom = new Random(worldSeed);

    caveGenerator = JustStone.generateCaves ? TerrainGen.getModdedMapGen(new MapGenCaves(), CAVE) : null;
    strongholdGenerator = (MapGenStronghold) TerrainGen.getModdedMapGen(new MapGenStronghold(), STRONGHOLD);
    mineshaftGenerator = JustStone.generateMines ? (MapGenMineshaft) TerrainGen.getModdedMapGen(new MapGenMineshaft(), MINESHAFT) : null;
    scatteredFeatureGenerator = (MapGenScatteredFeature) TerrainGen.getModdedMapGen(new MapGenScatteredFeature(), SCATTERED_FEATURE);
    ravineGenerator = JustStone.generateRavines ? TerrainGen.getModdedMapGen(new MapGenRavine(), RAVINE) : null;

    worldObj = world;
    this.defaultChunkData = new Block[WorldBaseValues.CHUNK_SIZE_XZ * baseValues.worldHeight];
    this.defaultBiomeData = new byte[WorldBaseValues.CHUNK_SIZE_XZ];
    fillDefaultBlockData();
  }

  private void fillDefaultBlockData() {
    Arrays.fill(defaultBiomeData, (byte) (JustStone.defaultBiomeID & 0xFF));

    int dataPos;
    for (int x = 0; x < WorldBaseValues.CHUNK_SIZE_X; ++x) {
      for (int z = 0; z < WorldBaseValues.CHUNK_SIZE_Z; ++z) {
        dataPos = (x + z * WorldBaseValues.CHUNK_SIZE_X) * this.baseValues.worldHeight;
        if (this.baseValues.groundLevel > 0) {
          Arrays.fill(this.defaultChunkData, dataPos, dataPos + this.baseValues.groundLevel, Blocks.stone);
        }
        if (JustStone.bedrockBottom) {
          this.defaultChunkData[dataPos] = Blocks.bedrock;
        }
        if (JustStone.bedrockTop) {
          this.defaultChunkData[dataPos + this.baseValues.worldHeight - 1] = Blocks.bedrock;
        }
      }
    }
  }

  /**
   * Will return back a chunk, if it doesn't exist and its not a MP client it
   * will generates all the blocks for the specified chunk from the map seed and
   * chunk seed
   */
  @Override
  public Chunk provideChunk(final int chunkX, final int chunkZ) {
    final Block[] chunkData = Arrays.copyOf(defaultChunkData, defaultChunkData.length);
    generateCavesAndRavines(chunkX, chunkZ, chunkData);

    final JustStoneChunk chunk = new JustStoneChunk(worldObj, chunkData, chunkX, chunkZ);
    chunk.setBiomeArray(Arrays.copyOf(defaultBiomeData, defaultBiomeData.length));
    chunk.generateSkylightMap();

    return chunk;
  }

  protected void ensureBedrock(final Block[] chunkData) {
    int dataPos;
    for (int x = 0; x < WorldBaseValues.CHUNK_SIZE_X; ++x) {
      for (int z = 0; z < WorldBaseValues.CHUNK_SIZE_Z; ++z) {
        dataPos = x + z * WorldBaseValues.CHUNK_SIZE_X;
        if (JustStone.bedrockBottom) {
          chunkData[dataPos] = Blocks.bedrock;
        }
        if (JustStone.bedrockTop) {
          chunkData[dataPos + this.baseValues.worldHeight - 1] = Blocks.bedrock;
        }
      }
    }
  }

  /**
   * Checks to see if a chunk exists at x, y
   */
  @Override
  public boolean chunkExists(int par1, int par2) {
    return true;
  }

  protected void generateCavesAndRavines(final int chunkX, final int chunkZ, final Block[] chunkData) {
    if (JustStone.generateCaves) {
      caveGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, chunkData);
    }
    if (JustStone.generateRavines) {
      ravineGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, chunkData);
    }
  }

  /**
   * Populates chunk with ores etc etc
   */
  @Override
  public void populate(final IChunkProvider chunkProvider, final int chunkX, final int chunkZ) {
    BlockSand.fallInstantly = true;

    final int blockX = chunkX * 16;
    final int blockZ = chunkZ * 16;
    final BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(blockX + 16, blockZ + 16);
    this.seedRandom.setSeed(this.worldObj.getSeed());
    final long seedModX = this.seedRandom.nextLong() / 2L * 2L + 1L;
    final long seedModY = this.seedRandom.nextLong() / 2L * 2L + 1L;
    this.seedRandom.setSeed((long) chunkX * seedModX + (long) chunkZ * seedModY ^ this.worldObj.getSeed());
    boolean hasVillage = false;

    MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(chunkProvider, worldObj, seedRandom, chunkX, chunkZ, hasVillage));

    if (JustStone.generateMines) {
      this.mineshaftGenerator.generateStructuresInChunk(this.worldObj, this.seedRandom, chunkX, chunkZ);
    }
    this.strongholdGenerator.generateStructuresInChunk(this.worldObj, this.seedRandom, chunkX, chunkZ);
    this.scatteredFeatureGenerator.generateStructuresInChunk(this.worldObj, this.seedRandom, chunkX, chunkZ);

    int x;
    int y;
    int z;

    if (biomegenbase != BiomeGenBase.desert && biomegenbase != BiomeGenBase.desertHills && !hasVillage && this.seedRandom.nextInt(10) == 0
            && TerrainGen.populate(chunkProvider, worldObj, seedRandom, chunkX, chunkZ, hasVillage, LAKE)) {
      x = blockX + this.seedRandom.nextInt(16) + 8;
      y = this.seedRandom.nextInt(this.baseValues.worldHeight - 8);
      z = blockZ + this.seedRandom.nextInt(16) + 8;
      (new WorldGenLakes(Blocks.water)).generate(this.worldObj, this.seedRandom, x, y, z);
    }

    if (TerrainGen.populate(chunkProvider, worldObj, seedRandom, chunkX, chunkZ, hasVillage, LAVA)
            && !hasVillage && this.seedRandom.nextInt(16) == 0) {
      x = blockX + this.seedRandom.nextInt(16) + 8;
      y = this.seedRandom.nextInt(this.seedRandom.nextInt(this.baseValues.worldHeight - 8 - 8) + 8);
      z = blockZ + this.seedRandom.nextInt(16) + 8;

      if (y < baseValues.groundLevel || this.seedRandom.nextInt(10) == 0) {
        (new WorldGenLakes(Blocks.lava)).generate(this.worldObj, this.seedRandom, x, y, z);
      }
    }

    if (TerrainGen.populate(chunkProvider, worldObj, seedRandom, chunkX, chunkZ, hasVillage, DUNGEON)) {
      for (int tries = 0; tries < 8; ++tries) {
        x = blockX + this.seedRandom.nextInt(16) + 8;
        y = this.seedRandom.nextInt(this.baseValues.worldHeight - 8);
        z = blockZ + this.seedRandom.nextInt(16) + 8;
        (new WorldGenDungeons()).generate(this.worldObj, this.seedRandom, x, y, z);
      }
    }

    if (this.baseValues.groundLevel < this.baseValues.worldHeight - 2) {
      SpawnerAnimals.performWorldGenSpawning(this.worldObj, biomegenbase, blockX + 8, blockZ + 8, 16, 16, this.seedRandom);
    }

    MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(chunkProvider, worldObj, seedRandom, chunkX, chunkZ, hasVillage));

    BlockSand.fallInstantly = false;
  }

  /**
   * Two modes of operation: if passed true, save all Chunks in one go. If
   * passed false, save up to two chunks. Return true if all chunks have been
   * saved.
   */
  @Override
  public boolean saveChunks(boolean par1, IProgressUpdate par2IProgressUpdate) {
    return true;
  }

  /**
   * Returns if the IChunkProvider supports saving.
   */
  @Override
  public boolean canSave() {
    return true;
  }

  /**
   * Converts the instance data to a readable string.
   */
  @Override
  public String makeString() {
    return "RandomLevelSource";
  }

  /**
   * Returns a list of creatures of the specified type that can spawn at the
   * given location.
   */
  @Override
  public List getPossibleCreatures(EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4) {
    BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(par2, par4);
    return par1EnumCreatureType == EnumCreatureType.monster && this.scatteredFeatureGenerator.func_143030_a(par2, par3, par4) ? this.scatteredFeatureGenerator.getScatteredFeatureSpawnList() : biomegenbase.getSpawnableList(par1EnumCreatureType);
  }

  @Override
  public int getLoadedChunkCount() {
    return 0;
  }

  @Override
  public void recreateStructures(int chunkX, int chunkZ) {
    if (JustStone.generateMines) {
      this.mineshaftGenerator.func_151539_a(this, this.worldObj, chunkX, chunkZ, (Block[]) null);
    }
    this.strongholdGenerator.func_151539_a(this, this.worldObj, chunkX, chunkZ, (Block[]) null);
    this.scatteredFeatureGenerator.func_151539_a(this, this.worldObj, chunkX, chunkZ, (Block[]) null);
  }

  @Override
  public boolean unloadQueuedChunks() {
    return false;
  }

  @Override
  public void saveExtraData() {
  }

  @Override
  public ChunkPosition func_147416_a(World world, String type, int x, int y, int z) {
    return "Stronghold".equals(type) && this.strongholdGenerator != null ? this.strongholdGenerator.func_151545_a(world, x, y, z) : null;
  }

  /**
   * loads or generates the chunk at the chunk location specified
   */
  @Override
  public Chunk loadChunk(int par1, int par2) {
    return provideChunk(par1, par2);
  }

}
