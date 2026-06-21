package me.roundaround.namedtraders.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import me.roundaround.trove.gametest.GameTestAssertionException;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.equine.TraderLlama;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;

/**
 * Boot a world and summon a named wandering trader and a named trader llama — the
 * exact state that drives this mod's despawn-prevention mixins ({@code hasCustomName}).
 * Asserts the custom names replicate to the client without the named-entity render
 * path crashing.
 */
@ClientGameTest
public class NamedTradersClientUsageTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().creative().stopTime(true).create()) {
      world.teleport(0.5, 65.0, 0.5);
      context.waitTicks(2);

      WanderingTrader trader = world.summon(
          EntityTypes.WANDERING_TRADER, new BlockPos(2, 65, 0), "{CustomName:'\"Bartholomew\"'}");
      TraderLlama llama = world.summon(
          EntityTypes.TRADER_LLAMA, new BlockPos(2, 65, 2), "{CustomName:'\"Pack Mule\"'}");
      context.waitTicks(5);

      if (!trader.hasCustomName()) {
        throw new GameTestAssertionException("wandering trader's custom name did not replicate to the client");
      }
      if (!llama.hasCustomName()) {
        throw new GameTestAssertionException("trader llama's custom name did not replicate to the client");
      }
    }
  }
}
