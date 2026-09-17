package me.roundaround.namedtraders.gametest;

import me.roundaround.allay.api.gametest.ServerGameTest;
import me.roundaround.trove.gametest.ServerTest;
import me.roundaround.trove.gametest.ServerTestContext;
import net.minecraft.world.entity.animal.equine.TraderLlama;

import static me.roundaround.namedtraders.gametest.DespawnTestSupport.expectAlive;
import static me.roundaround.namedtraders.gametest.DespawnTestSupport.prepare;
import static me.roundaround.namedtraders.gametest.DespawnTestSupport.spawnLlama;
import static me.roundaround.namedtraders.gametest.DespawnTestSupport.waitForDespawn;

import static me.roundaround.namedtraders.gametest.DespawnTestSupport.DELAY;

/** A named trader llama outlives its despawn timer while an unnamed one on the same timer despawns. */
@ServerGameTest
public class NamedLlamaDespawnTest implements ServerTest {
  @Override
  public void runTest(ServerTestContext context) {
    prepare(context);
    TraderLlama named = spawnLlama(context, 0, true);
    TraderLlama unnamed = spawnLlama(context, 1, false);

    waitForDespawn(context, unnamed, "the unnamed llama");
    context.waitTicks(DELAY * 2);

    expectAlive(context, named, "the named llama");
  }
}
