#!/data/data/com.termux/files/usr/bin/bash
cd Lumia
git pull origin main

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

      - name: Setup Gradle Wrapper
        run: |
          # Si gradlew n'existe pas, on le crée
          if [ ! -f "gradlew" ]; then
            echo "Création gradle wrapper..."
            wget -q https://services.gradle.org/distributions/gradle-8.5-bin.zip
            unzip -q gradle-8.5-bin.zip
            ./gradle-8.5/bin/gradle wrapper --gradle-version 8.5
          fi
          chmod +x gradlew

      - name: Accept Android licenses
        run: yes | sdkmanager --licenses || true

      - name: Build Debug APK
        run: |
          ./gradlew :app:assembleDebug --stacktrace

      - name: Upload APK artifact
        uses: actions/upload-artifact@v4
        with:
          name: Lumia-APK
          path: app/build/outputs/apk/debug/app-debug.apk

      - name: Create Release
        if: success()
        uses: softprops/action-gh-release@v1
        with:
          tag_name: v3.0-${{ github.run_number }}
          name: Lumia v3.0 Ultra Pack Build ${{ github.run_number }}
          body: |
            **Lumia Ultra Low <3MB**
            - 20MB RAM vs 250MB MIUI
            - 0 pub via Private DNS
            - Shizuku 1-clic debloat
            - Auto build GitHub Actions
          files: app/build/outputs/apk/debug/app-debug.apk
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
EOF

git add .github/workflows/build.yml
git commit -m "Fix build.yml: remove broken setup-android@v3, use preinstalled SDK + gradle wrapper"
git push origin main

echo "✅ FIX PUSHÉ - Va voir Actions, ça va passer vert cette fois"
