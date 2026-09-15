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
