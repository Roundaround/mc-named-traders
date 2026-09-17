package me.roundaround.namedtraders.gametest;

import me.roundaround.trove.gametest.GameTestAssertionException;
import me.roundaround.trove.gametest.ServerTestContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.equine.TraderLlama;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.level.ChunkPos;

import java.util.function.BooleanSupplier;

/** Shared helpers for the Named Traders {@code @ServerGameTest} suite. */
final class DespawnTestSupport {
  /** Ticks left on a test mob's despawn timer: the vanilla 40 minutes compressed to a moment. */
  static final int DELAY = 3;

  private static final BlockPos ARENA = new BlockPos(1, 200, 8);
  private static final int TIMEOUT_TICKS = 100;

  private DespawnTestSupport() {
  }

  /** Force-load the arena chunk, since a player-less dedicated server keeps nothing loaded. */
  static void prepare(ServerTestContext context) {
    ChunkPos chunk = ChunkPos.containing(ARENA);
    context.onCleanup(() -> context.runOnServer(
        (server) -> server.overworld().setChunkForced(chunk.x(), chunk.z(), false)));
    context.runOnServer((server) -> server.overworld().setChunkForced(chunk.x(), chunk.z(), true));
    waitUntil(context, () -> context.overworld().isPositionEntityTicking(ARENA),
        "arena chunk never became entity-ticking");
  }

  static WanderingTrader spawnTrader(ServerTestContext context, int slot, boolean named) {
    WanderingTrader trader = spawn(context, EntityTypes.WANDERING_TRADER, slot, named);
    context.runOnServer((server) -> trader.setDespawnDelay(DELAY));
    return trader;
  }

  static TraderLlama spawnLlama(ServerTestContext context, int slot, boolean named) {
    TraderLlama llama = spawn(context, EntityTypes.TRADER_LLAMA, slot, named);
    context.runOnServer((server) -> llama.setDespawnDelay(DELAY));
    return llama;
  }

  /**
   * Names via {@code setCustomName} only. A real name tag also sets persistence, which vanilla's
   * llama check already honours, so this is the case that isolates the mod.
   */
  private static <T extends Mob> T spawn(ServerTestContext context, EntityType<T> type, int slot, boolean named) {
    T mob = context.computeOnServer((server) -> {
      ServerLevel level = server.overworld();
      T entity = type.create(level, EntitySpawnReason.COMMAND);
      if (entity == null) {
        throw new GameTestAssertionException("could not create a test " + type);
      }
      entity.setPos(ARENA.getX() + 0.5 + slot * 1.5, ARENA.getY(), ARENA.getZ() + 0.5);
      if (!level.isPositionEntityTicking(entity.blockPosition())) {
        throw new GameTestAssertionException("slot " + slot + " is outside the force-loaded arena chunk");
      }
      entity.setNoAi(true);
      entity.setNoGravity(true);
      entity.setSilent(true);
      if (named) {
        entity.setCustomName(Component.literal("Test Subject"));
      }
      if (!level.addFreshEntity(entity)) {
        throw new GameTestAssertionException("could not add a test " + type + " to the overworld");
      }
      return entity;
    });
    context.onCleanup(() -> context.runOnServer((server) -> mob.discard()));
    return mob;
  }

  /** Block until the unnamed control has despawned, which proves the window outlasted the timer. */
  static void waitForDespawn(ServerTestContext context, Entity control, String what) {
    waitUntil(context, control::isRemoved, what + " never despawned, so the test window proves nothing");
  }

  static void expectAlive(ServerTestContext context, Entity entity, String what) {
    if (context.computeOnServer((server) -> entity.isRemoved())) {
      throw new GameTestAssertionException(what + " despawned");
    }
  }

  static void check(boolean condition, String message) {
    if (!condition) {
      throw new GameTestAssertionException(message);
    }
  }

  private static void waitUntil(ServerTestContext context, BooleanSupplier condition, String failure) {
    for (int i = 0; i < TIMEOUT_TICKS; i++) {
      if (context.computeOnServer((server) -> condition.getAsBoolean())) {
        return;
      }
      context.waitTicks(1);
    }
    throw new GameTestAssertionException(failure);
  }
}
