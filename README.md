# Lumia - Ultra Low Suite for Xiaomi

Suite ultra légère < 10MB total pour remplacer HyperOS.

## Modules
- **Launcher (3MB)** - LruCache 60, 0 anim, single IO load
- **Lumia Control** - Génère script `pm disable-user --user 0` sans-root. Safe list basée sur debloat_xiaomi_android. Pas de `com.miui.powerkeeper` par défaut.
- **Gallery Low (2MB)** - MediaStore LIMIT 200, pas de scan 20k photos
- **Files Low (1.5MB)** - File API direct, 0 lib

## Utilisation Control sans-root
1. Ouvre Lumia Control dans l'app
2. Clique "Générer script ADB ultra-low"
3. Colle dans Termux: `su` n'est PAS nécessaire, Termux peut faire `pm disable-user --user 0 com.miui.msa` si tu as donné l'accès via `adb shell pm grant` ou via Shizuku.

Ou depuis PC: `adb shell pm disable-user --user 0 com.miui.msa`

Restaurer: `pm enable --user 0 com.miui.msa`
