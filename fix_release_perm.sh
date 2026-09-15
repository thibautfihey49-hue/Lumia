#!/data/data/com.termux/files/usr/bin/bash
cd Lumia
git pull origin main

cat > .github/workflows/build.yml <<'EOF'
name: Build Lumia APK
on:
  push:
    branches: [ main ]
  workflow_dispatch:

permissions:
  contents: write

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Setup Gradle
        uses: gradle/actions/setup-gradle@v3
        with:
          gradle-version: 8.2
      - name: Build APK
        run: gradle :app:assembleDebug
      - name: Upload APK artifact
        uses: actions/upload-artifact@v4
        with:
          name: Lumia-APK
          path: app/build/outputs/apk/debug/app-debug.apk
      - name: Release APK
        uses: softprops/action-gh-release@v2
        with:
          tag_name: v3.0-${{ github.run_number }}
          name: Lumia Ultra v3.0-${{ github.run_number }}
          files: app/build/outputs/apk/debug/app-debug.apk
          generate_release_notes: true
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
EOF

git add .github/workflows/build.yml
git commit -m "Fix 403 release: add permissions contents write + use gh-release v2"
git push origin main
echo "✅ FIX PERM PUSHÉ - Le prochain build va créer la Release"
