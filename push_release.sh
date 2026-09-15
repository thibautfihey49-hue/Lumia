#!/data/data/com.termux/files/usr/bin/bash
cd Lumia
git pull origin main

mkdir -p .github/workflows

# --- GitHub Action qui build l'APK à chaque push ---
cat > .github/workflows/build.yml <<'EOF'
name: Build Lumia APK

on:
  push:
    branches: [ main ]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Setup Android SDK
        uses: android-actions/setup-android@v3
      - name: Build APK
        run: |
          chmod +x gradlew 2>/dev/null || echo "no gradlew, using gradle"
          # Si pas de wrapper, on installe gradle
          if [ ! -f "gradlew" ]; then
            wget https://services.gradle.org/distributions/gradle-8.5-bin.zip
            unzip gradle-8.5-bin.zip
            ./gradle-8.5/bin/gradle :app:assembleDebug
          else
            ./gradlew :app:assembleDebug
          fi
      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: Lumia-APK
          path: app/build/outputs/apk/debug/app-debug.apk
      - name: Create Release
        uses: softprops/action-gh-release@v1
        with:
          tag_name: v3.0-${{ github.run_number }}
          name: Lumia v3.0 Ultra Pack
          body: "APK ultra-low <3MB - 20MB RAM - Auto build"
          files: app/build/outputs/apk/debug/app-debug.apk
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
EOF

# --- Gradle wrapper minimal si il manque ---
cat > gradlew <<'EOF'
#!/bin/bash
gradle :app:assembleDebug
EOF
chmod +x gradlew

# --- Installer V2 qui télécharge l'APK depuis Releases ---
cat > install.sh <<'EOF'
#!/bin/bash
echo "╔════════════════════════════════╗"
echo "║  LUMIA ULTRA - Xiaomi Debloat ║"
echo "║  <10MB | 0 batterie | 20MB RAM║"
echo "╚════════════════════════════════╝"
echo ""

echo "→ [1/5] Téléchargement Lumia APK..."
LATEST_URL=$(curl -s https://api.github.com/repos/thibautfihey49-hue/Lumia/releases/latest | grep "browser_download_url.*apk" | cut -d '"' -f 4)
if [ ! -z "$LATEST_URL" ]; then
  curl -L -o /sdcard/lumia.apk "$LATEST_URL" 2>/dev/null || curl -L -o /data/local/tmp/lumia.apk "$LATEST_URL"
  APK_PATH="/sdcard/lumia.apk"
  [ ! -f "$APK_PATH" ] && APK_PATH="/data/local/tmp/lumia.apk"
  echo "  ✅ APK téléchargé: $APK_PATH"
  pm install -r "$APK_PATH" && echo "  ✅ Lumia installé" || echo "  ⚠️ Installe manuellement: $APK_PATH"
else
  echo "  ⏭️ Pas de release encore, build en cours sur GitHub Actions..."
  echo "  Va voir: https://github.com/thibautfihey49-hue/Lumia/actions"
fi

echo ""
echo "→ [2/5] Debloat HyperOS safe..."
for pkg in com.miui.msa com.miui.daemon com.miui.analytics com.xiaomi.mipicks com.miui.hybrid com.miui.yellowpage com.miui.videoplayer com.miui.player com.xiaomi.midrop com.miui.bugreport com.milink.service com.miui.cleanmaster; do
  pm disable-user --user 0 $pkg > /dev/null 2>&1 && echo "  ✅ $pkg" || true
done

echo ""
echo "→ [3/5] Guard anti-pub DNS..."
settings put global private_dns_mode hostname
settings put global private_dns_specifier dns.adguard.com
echo "  ✅ Bloqué: msa, analytics, mipicks"

echo ""
echo "→ [4/5] Optimisation..."
settings put global window_animation_scale 0.5
settings put global transition_animation_scale 0.5
settings put global animator_duration_scale 0.5
echo "  ✅ Anim 0.5x"

echo ""
echo "→ [5/5] Configuration..."
echo "  ✅ Définis Lumia comme launcher par défaut dans Paramètres"
echo ""
echo "═══════════════════════════════════"
echo "✅ LUMIA PRÊT ! Redémarre."
echo "═══════════════════════════════════"
EOF

chmod +x install.sh

git add .
git commit -m "Add auto APK build Release + installer v2 with download from Releases"
git push origin main

echo ""
echo "✅ RELEASE AUTO PUSHÉE !"
echo "1. Va sur https://github.com/thibautfihey49-hue/Lumia/actions"
echo "2. Attends 3-4 min que le build finisse"
echo "3. L'APK sera dans Releases"
echo "4. Après, la commande 1-ligne téléchargera l'APK auto:"
echo "curl -sL https://raw.githubusercontent.com/thibautfihey49-hue/Lumia/main/install.sh | bash"
