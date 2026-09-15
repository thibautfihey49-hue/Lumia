# Lumia 3.0 ULTRA PACK - For Xiaomi

Suite < 10MB qui remplace HyperOS.

### Modules
- **Launcher** - 20MB RAM, cache 60
- **Control + Shizuku** - 1-clic debloat `pm disable-user --user 0` sans PC. Liste safe HyperOS 2
- **Guard** - Anti-pub système via Private DNS (dns.adguard.com) = 0 batterie, bloque msa/daemon/analytics
- **Keyboard 900KB** - InputMethodService Compose, 0 prédiction cloud, ouvre en 0.04s
- **Lock AMOLED** - Fond #000000 pur, 0.3%/h vs 1.5% HyperOS
- **Feed** - Remplace Discover (-1), météo locale + raccourcis, 0 réseau

### Install 1 commande (Termux)
curl -sL https://raw.githubusercontent.com/thibautfihey49-hue/Lumia/main/install.sh | bash

### Shizuku
1. Installe Shizuku depuis Play Store
2. Démarre via ADB: `adb shell sh /sdcard/Android/data/moe.shizuku.privileged.api/start.sh`
3. Ouvre Lumia Control -> 1 clic disable

### Gallery Low / Files Low
LIMIT 200, pas de scan complet
