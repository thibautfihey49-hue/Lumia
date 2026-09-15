#!/data/data/com.termux/files/usr/bin/bash
cd Lumia
git pull origin main

mkdir -p app/src/main/res/mipmap-anydpi-v26
mkdir -p app/src/main/res/drawable
mkdir -p app/src/main/res/xml

# --- Icône adaptive qui ne peut pas manquer ---
cat > app/src/main/res/drawable/ic_launcher_fg.xml <<'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="108dp" android:height="108dp" android:viewportWidth="108" android:viewportHeight="108">
    <path android:fillColor="#000000" android:pathData="M0,0h108v108h-108z"/>
    <path android:fillColor="#FFFFFF" android:pathData="M54,18L18,54L54,90L90,54Z"/>
    <path android:fillColor="#00FF88" android:pathData="M54,32L32,54L54,76L76,54Z"/>
</vector>
EOF

cat > app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml <<'EOF'
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@android:color/black"/>
    <foreground android:drawable="@drawable/ic_launcher_fg"/>
</adaptive-icon>
EOF

cat > app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml <<'EOF'
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@android:color/black"/>
    <foreground android:drawable="@drawable/ic_launcher_fg"/>
</adaptive-icon>
EOF

# Fallback png 1x1 transparent si AAPT râle encore
echo "[STRIPPED 92 bytes]" | base64 -d > app/src/main/res/mipmap-hdpi/ic_launcher.png
cp app/src/main/res/mipmap-hdpi/ic_launcher.png app/src/main/res/mipmap-hdpi/ic_launcher_round.png
mkdir -p app/src/main/res/mipmap-mdpi app/src/main/res/mipmap-xhdpi app/src/main/res/mipmap-xxhdpi app/src/main/res/mipmap-xxxhdpi
cp app/src/main/res/mipmap-hdpi/ic_launcher.png app/src/main/res/mipmap-mdpi/ic_launcher.png
cp app/src/main/res/mipmap-hdpi/ic_launcher.png app/src/main/res/mipmap-xhdpi/ic_launcher.png
cp app/src/main/res/mipmap-hdpi/ic_launcher.png app/src/main/res/mipmap-xxhdpi/ic_launcher.png
cp app/src/main/res/mipmap-hdpi/ic_launcher.png app/src/main/res/mipmap-xxxhdpi/ic_launcher.png
cp app/src/main/res/mipmap-hdpi/ic_launcher.png app/src/main/res/mipmap-mdpi/ic_launcher_round.png
cp app/src/main/res/mipmap-hdpi/ic_launcher.png app/src/main/res/mipmap-xhdpi/ic_launcher_round.png
cp app/src/main/res/mipmap-hdpi/ic_launcher.png app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png
cp app/src/main/res/mipmap-hdpi/ic_launcher.png app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png

cat > app/src/main/res/xml/method.xml <<'EOF'
<?xml version="1.0" encoding="utf-8"?>
<input-method xmlns:android="http://schemas.android.com/apk/res/android"><subtype android:imeSubtypeMode="keyboard" android:label="Lumia Keyboard"/></input-method>
EOF

git add -f app/src/main/res/
git commit -m "Fix ic_launcher: create anydpi adaptive + png fallbacks for all densities"
git push origin main
echo "✅ ICON FIX 2 PUSHÉ"
