#!/bin/bash
echo "Lumia Ultra Installer for Xiaomi"
echo "1. Désactivation bloats safe..."
for pkg in com.miui.msa com.miui.daemon com.miui.analytics com.xiaomi.mipicks com.miui.hybrid com.miui.yellowpage com.miui.videoplayer com.miui.player com.xiaomi.midrop com.miui.bugreport com.milink.service com.miui.cleanmaster; do
  pm disable-user --user 0 $pkg 2>/dev/null && echo "✅ $pkg disabled" || echo "⚠️ $pkg déjà désactivé ou protégé"
done
echo "2. Private DNS anti-pub..."
settings put global private_dns_mode hostname
settings put global private_dns_specifier dns.adguard.com
echo "✅ Lumia prêt - Redémarre ton launcher"
