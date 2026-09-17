package me.roundaround.namedtraders.gametest;

import me.roundaround.allay.api.gametest.ServerGameTest;
import me.roundaround.trove.gametest.ServerTest;
import me.roundaround.trove.gametest.ServerTestContext;
import net.minecraft.world.entity.animal.equine.TraderLlama;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;

import static me.roundaround.namedtraders.gametest.DespawnTestSupport.DELAY;
import static me.roundaround.namedtraders.gametest.DespawnTestSupport.expectAlive;
import static me.roundaround.namedtraders.gametest.DespawnTestSupport.prepare;
import static me.roundaround.namedtraders.gametest.DespawnTestSupport.spawnLlama;
import static me.roundaround.namedtraders.gametest.DespawnTestSupport.spawnTrader;
import static me.roundaround.namedtraders.gametest.DespawnTestSupport.waitForDespawn;

/**
 * A leashed llama follows its trader's timer rather than its own, so the pairings matter: a named
 * llama must survive its unnamed trader despawning, and an unnamed llama stays as long as the
 * named trader holding it does (that trader's timer is frozen, so the llama's never runs out).
 */
@ServerGameTest
public class LeashedLlamaDespawnTest implements ServerTest {
  @Override
  public void runTest(ServerTestContext context) {
    prepare(context);
    WanderingTrader unnamedTrader = spawnTrader(context, 0, false);
    TraderLlama namedLlama = spawnLlama(context, 1, true);
    WanderingTrader namedTrader = spawnTrader(context, 3, true);
    TraderLlama unnamedLlama = spawnLlama(context, 4, false);
    WanderingTrader controlTrader = spawnTrader(context, 6, false);
    TraderLlama controlLlama = spawnLlama(context, 7, false);
    context.runOnServer((server) -> {
      namedLlama.setLeashedTo(unnamedTrader, true);
      unnamedLlama.setLeashedTo(namedTrader, true);
      controlLlama.setLeashedTo(controlTrader, true);
    });

    waitForDespawn(context, controlLlama, "the unnamed llama leashed to an unnamed trader");
    waitForDespawn(context, unnamedTrader, "the unnamed trader holding the named llama");
    context.waitTicks(DELAY * 2);

    expectAlive(context, namedLlama, "the named llama whose trader despawned");
    expectAlive(context, namedTrader, "the named trader");
    expectAlive(context, unnamedLlama, "the unnamed llama leashed to a named trader");
  }
}
