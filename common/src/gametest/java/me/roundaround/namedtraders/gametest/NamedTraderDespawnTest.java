package me.roundaround.namedtraders.gametest;

import me.roundaround.allay.api.gametest.ServerGameTest;
import me.roundaround.trove.gametest.ServerTest;
import me.roundaround.trove.gametest.ServerTestContext;
import net.minecraft.world.entity.animal.equine.TraderLlama;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;

import static me.roundaround.namedtraders.gametest.DespawnTestSupport.DELAY;
import static me.roundaround.namedtraders.gametest.DespawnTestSupport.check;
import static me.roundaround.namedtraders.gametest.DespawnTestSupport.expectAlive;
import static me.roundaround.namedtraders.gametest.DespawnTestSupport.prepare;
import static me.roundaround.namedtraders.gametest.DespawnTestSupport.spawnLlama;
import static me.roundaround.namedtraders.gametest.DespawnTestSupport.spawnTrader;
import static me.roundaround.namedtraders.gametest.DespawnTestSupport.waitForDespawn;

/**
 * A named wandering trader outlives its despawn timer, and the timer itself stays frozen, while
 * an unnamed trader on the same timer despawns.
 */
@ServerGameTest
public class NamedTraderDespawnTest implements ServerTest {
  @Override
  public void runTest(ServerTestContext context) {
    prepare(context);
    WanderingTrader named = spawnTrader(context, 0, true);
    WanderingTrader unnamed = spawnTrader(context, 1, false);

    waitForDespawn(context, unnamed, "the unnamed trader");
    context.waitTicks(DELAY * 2);

    expectAlive(context, named, "the named trader");
    int delay = context.computeOnServer((server) -> named.getDespawnDelay());
    check(delay == DELAY, "the named trader's despawn timer kept counting: " + DELAY + " -> " + delay);
  }
}
