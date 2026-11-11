# Changelog

## [1.1.0] - 2026-11-11

The winter update.

### Added
- Christmas loading screen: a snowy clearing at sunset behind the Mojang logo, with a lit Christmas tree and presents, a log cabin with a smoking chimney, snowmen, lanterns, a frozen pond and snowy mountains. Built in Minecraft, turned into pixel art, with light snow falling over it.
- Your own loading screen: drag a PNG onto the Dark Mode settings screen (or put it in `config/darkmode/loading.png`) and pick "Your image". It fills the window like a wallpaper.
- Winter theme: icy blue dark screens with snow gently falling behind every menu.

### Changed
- Installed without Mycel, the game no longer refuses to start. Dark Mode stays off and tells you in chat that Mycel is missing, with a download link. An outdated Mycel is still reported by the loader.

## [1.0.2] - 2026-11-04

### Changed
- Built for Mycel 1.6.1: chat messages get the new MyCelium look, and Mycel can share anonymous stats if you agree. What is sent: https://wiki.mycelium-gg.org/privacy#mycel-stats

## [1.0.1] - 2026-11-03

### Changed
- Built for Mycel 1.5.2, NeoForge 26.3.0.101 (the first stable NeoForge for 26.3), NeoForge 26.1.2.126, Forge 66.1.2 and Fabric API 0.165.0.
- A screen texture Dark Mode cannot recolor now stays as it was instead of stopping the game, and is noted once in the log.

### Added
- Optional anonymous stats and error reports through FastStats (Minecraft 26.2 and 26.3). Nothing is sent until you agree on the screen shown at first start; change it any time in `config/faststats/config.properties`. What is sent: https://wiki.mycelium-gg.org/privacy#mod-stats

## [1.0.0] - 2026-11-01

### Added
- Dark menus, inventories and other screens, including screens from other mods.
- A dark Mojang loading screen in black, charcoal, midnight or plum.
- Five themes and a brightness slider.
- Options to include the HUD, leave out mod screens, skip specific mods, darken drawn backgrounds and brighten dark text.
- A settings screen, opened with a button in the Options menu.
- German, Spanish, French, Brazilian Portuguese, Russian and Simplified Chinese.
