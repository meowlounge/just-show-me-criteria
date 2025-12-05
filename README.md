# 🎯 Just Show Me Criteria

A focused **Fabric client mod** that pins any advancement you choose and keeps its remaining criteria visible on your HUD.

---

## 📦 Usage Notice

This is a **client-only Fabric mod** built for **Minecraft 1.21.10+**.
Install it alongside Fabric Loader, Fabric API, and Fabric Language Kotlin just like any other mod; no server component is required and it works in both singleplayer and multiplayer because it only reads the advancement data already synced to your client.
All settings live in `config/jsmc.json` and can also be edited through **Mod Menu + Cloth Config**.

---

## 🧭 Overview

**Just Show Me Criteria** adds a lightweight HUD card that shows the unfinished criteria of whichever advancement you pin.
Use `/jsmc select <namespace:path>` (client command) or the config screen to change the tracked advancement at any time.
The overlay updates automatically every tick, can disappear once the advancement is complete, and shows friendly error text if the configured ID is invalid or unavailable.

---

## ✨ Features

* Targets **Fabric Loader 0.17.3+** with Fabric API 0.136.0 on **Minecraft 1.21.10**.
* Purely client-side, so it works on any world or server without requiring installation elsewhere.
* Tracks any advancement and remembers your choice (`config/jsmc.json`), defaulting to `minecraft:story/mine_diamond`.
* Optional "hide when completed" mode plus a **1-20 max visible criteria** slider to control density.
* Custom HUD placement with anchor, X/Y offsets, and **50%-300% scale** controls.
* Style knobs for header/body/status colors and adjustable panel background opacity.
* Bundled Mod Menu (Cloth Config) integration so you never need to open the JSON file.
* Efficient advancement suggestions and caching so `/jsmc select` tab-completes instantly even on busy servers.

---

## ⌨️ Commands

| Command | Description |
| ------- | ----------- |
| `/jsmc select <advancement_id>` | Pins the specified advancement and refreshes the HUD with its remaining criteria. |

> This is a **client-side command** registered through Fabric API; it supports tab suggestions for every advancement the server exposes.

---

## 📄 License

Licensed under the [MIT License](LICENSE.txt).
