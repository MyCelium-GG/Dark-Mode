# Dark Mode

Turns every screen in Minecraft dark, including the Mojang loading screen and screens from other mods.

- Menus, inventories, chests, furnaces, anvils, books and the creative menu all go dark.
- Screens from other mods go dark too, with no extra resource pack per mod.
- The red Mojang loading screen turns black, or another color you pick.
- Items, hearts, logos and other colored art keep their colors.
- Client-side. Servers do not need it.

Requires [Mycel](https://www.curseforge.com/minecraft/mc-mods/mycel). Fabric, Forge and NeoForge, Minecraft 26.1 to 26.3.

## Settings

Open them with the **Dark Mode** button in the top left of the Options menu.

| Setting | Default | What it does |
|---|---|---|
| Dark Mode | On | Turns the mod on or off |
| Theme | Neutral | Neutral, Midnight, Forest, Plum or Ember |
| Brightness | 30% | How bright dark screens are |
| Loading screen | Black | Black, Charcoal, Midnight, Plum or Red (normal) |
| Mod screens | On | Also darken screens from other mods |
| HUD | Off | Also darken the hotbar and other parts of the HUD |
| Drawn backgrounds | On | Darken light backgrounds that screens draw in code |
| Readable text | On | Brighten dark text so it stays readable |
| Mods to leave alone | Empty | Mod ids whose screens stay as they are |

Color changes apply when you close the settings, with a short resource reload.

## Good to know

- **Forge and NeoForge:** their first loading window opens before any mod runs, so it is still red the very first time you start with this mod. From the next start on it is black. That window only knows black or red, so custom colors start once the game's own loading screen appears.
- **Monochrome Logo:** the mod turns on this vanilla option so that first window goes dark. Pick Red or turn the mod off and it switches back. If you remove the mod, the option stays on until you change it in Accessibility Settings.
- **Other mods:** screens are found by their texture names, and a mod that names them unusually may stay light. A screen that looks wrong can go in **Mods to leave alone**.
- A resource pack that retextures screens gets darkened on top of its own colors.

## Build

```
./gradlew build            # Minecraft 26.3
./gradlew build -Pmc=26.2
./gradlew build -Pmc=26.1
```

MIT licensed. Part of [MyCelium](https://mycelium-gg.org).
