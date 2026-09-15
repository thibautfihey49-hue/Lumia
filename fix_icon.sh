#!/data/data/com.termux/files/usr/bin/bash
cd Lumia
git pull origin main

mkdir -p app/src/main/res/mipmap-hdpi app/src/main/res/mipmap-mdpi app/src/main/res/mipmap-xhdpi app/src/main/res/mipmap-xxhdpi
mkdir -p app/src/main/res/drawable
mkdir -p app/src/main/res/xml

# --- Icône simple Lumia ---
cat > app/src/main/res/drawable/ic_launcher_foreground.xml <<'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="108dp" android:height="108dp" android:viewportWidth="108" android:viewportHeight="108">
    <path android:fillColor="#000000" android:pathData="M0,0h108v108h-108z"/>
    <path android:fillColor="#FFFFFF" android:pathData="M54,24L24,54L54,84L84,54Z" android:strokeWidth="2"/>
    <path android:fillColor="#00FF88" android:pathData="M54,36L36,54L54,72L72,54Z"/>
</vector>
EOF

cat > app/src/main/res/mipmap/ic_launcher.xml <<'EOF'
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@android:color/black"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
EOF

cat > app/src/main/res/mipmap/ic_launcher_round.xml <<'EOF'
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@android:color/black"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
EOF

# Méthode XML pour clavier
cat > app/src/main/res/xml/method.xml <<'EOF'
<?xml version="1.0" encoding="utf-8"?>
<input-method xmlns:android="http://schemas.android.com/apk/res/android">
    <subtype android:imeSubtypeMode="keyboard" android:label="Lumia Keyboard"/>
</input-method>
EOF

# Fix Manifest avec icône qui existe maintenant
cat > app/src/main/AndroidManifest.xml <<'EOF'
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android" xmlns:tools="http://schemas.android.com/tools">
    <uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" tools:ignore="QueryAllPackagesPermission" />
    <application
        android:name=".LumiaApp"
        android:allowBackup="false"
        android:icon="@mipmap/ic_launcher"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:label="Lumia"
        android:theme="@android:style/Theme.Material.Light.NoActionBar">
        <activity android:name=".LumiaLauncherActivity" android:exported="true" android:launchMode="singleTask">
            <intent-filter><action android:name="android.intent.action.MAIN" /><category android:name="android.intent.category.HOME" /><category android:name="android.intent.category.DEFAULT" /></intent-filter>
        </activity>
        <activity android:name=".control.LumiaControlActivity" android:exported="true" android:label="Lumia Control"><intent-filter><action android:name="android.intent.action.MAIN" /><category android:name="android.intent.category.LAUNCHER" /></intent-filter></activity>
        <activity android:name=".gallery.GalleryActivity" android:exported="true" android:label="Lumia Gallery"><intent-filter><action android:name="android.intent.action.MAIN" /><category android:name="android.intent.category.LAUNCHER" /></intent-filter></activity>
        <activity android:name=".files.FilesActivity" android:exported="true" android:label="Lumia Files"><intent-filter><action android:name="android.intent.action.MAIN" /><category android:name="android.intent.category.LAUNCHER" /></intent-filter></activity>
        <activity android:name=".guard.GuardActivity" android:exported="true" android:label="Lumia Guard"><intent-filter><action android:name="android.intent.action.MAIN" /><category android:name="android.intent.category.LAUNCHER" /></intent-filter></activity>
        <activity android:name=".lock.LockActivity" android:exported="true" android:label="Lumia Lock"><intent-filter><action android:name="android.intent.action.MAIN" /><category android:name="android.intent.category.LAUNCHER" /></intent-filter></activity>
        <activity android:name=".feed.FeedActivity" android:exported="true" android:label="Lumia Feed"><intent-filter><action android:name="android.intent.action.MAIN" /><category android:name="android.intent.category.LAUNCHER" /></intent-filter></activity>
        <service android:name=".keyboard.LumiaKeyboard" android:label="Lumia Keyboard" android:permission="android.permission.BIND_INPUT_METHOD" android:exported="true"><intent-filter><action android:name="android.view.InputMethod" /></intent-filter><meta-data android:name="android.view.im" android:resource="@xml/method" /></service>
    </application>
</manifest>
EOF

git add .
git commit -m "Fix ic_launcher not found: create adaptive icon + method.xml"
git push origin main
echo "✅ ICON FIX PUSHÉ"
