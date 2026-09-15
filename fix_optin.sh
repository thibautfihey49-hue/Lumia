#!/data/data/com.termux/files/usr/bin/bash
cd Lumia
git pull origin main

# Ajoute OptIn sur FluidAppIcon aussi
sed -i 's/@Composable\nfun FluidAppIcon/@OptIn(ExperimentalFoundationApi::class)\n@Composable\nfun FluidAppIcon/' app/src/main/java/com/lumia/os/LumiaLauncherActivity.kt

git add app/src/main/java/com/lumia/os/LumiaLauncherActivity.kt
git commit -m "Fix experimental foundation: add OptIn to FluidAppIcon"
git push origin main
echo "✅ OPTIN FIX PUSHÉ"
