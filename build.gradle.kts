plugins {
  id("me.roundaround.allay")
}

allay {
  displayName.set("Named Traders")
  description.set("Naming wandering traders or their llamas prevents them from despawning.")
  authors.set(listOf("Roundaround"))
  license.set("MIT")
  homepage.set("https://modrinth.com/mod/named-traders")
  repository.set("https://github.com/Roundaround/mc-named-traders")
  issues.set("https://github.com/Roundaround/mc-named-traders/issues")
  logoFile.set("assets/namedtraders/banner.png")

  gametest {
    // Acknowledge the Minecraft EULA for the throwaway worlds the headless
    // server game test spins up.
    eula.set(true)
  }

  modrinth {
    projectId.set("named-traders")
  }

  curseforge {
    projectId.set(1571246)
  }

  release {
    versionType.set("release")
    sourcesJar.set(true)
  }
}
