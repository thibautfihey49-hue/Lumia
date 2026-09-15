package com.lumia.os.control

data class BloatApp(val pkg: String, val name: String, val safe: Boolean, val desc: String)

object BloatList {
    // Liste SAFE testée HyperOS 2 / MIUI 14 - on utilise disable-user pas uninstall
    val all = listOf(
        BloatApp("com.miui.msa", "MSA - Pubs système", true, "Source principale des pubs dans HyperOS"),
        BloatApp("com.miui.daemon", "Analytics Daemon", true, "Telemetry en arrière-plan 24/7"),
        BloatApp("com.miui.analytics", "Analytics", true, "Envoi data"),
        BloatApp("com.xiaomi.mipicks", "Mi Picks", true, "Store pub"),
        BloatApp("com.miui.miservice", "Mi Service", true, "Service pub"),
        BloatApp("com.xiaomi.joyose", "Joyose - Drain batterie", false, "Drain batterie mais peut casser jeux - safe avec warning"),
        BloatApp("com.miui.powerkeeper", "Powerkeeper", false, "Gestion batterie Xiaomi - ATTENTION ne pas disable si tu veux 120W"),
        BloatApp("com.miui.yellowpage", "Yellow Pages", true, "Spam appel"),
        BloatApp("com.miui.videoplayer", "Mi Video", true, "Remplacé par Lumia Gallery"),
        BloatApp("com.miui.player", "Mi Music", true, "Lourd"),
        BloatApp("com.xiaomi.midrop", "Mi Drop", true, "Obsolète"),
        BloatApp("com.miui.compass", "Compass", true, "Inutile"),
        BloatApp("com.miui.notes", "Mi Notes", true, "Si tu utilises pas"),
        BloatApp("com.android.browser", "Mi Browser", true, "Pub + tracking"),
        BloatApp("com.miui.bugreport", "BugReport", true, "Log en fond"),
        BloatApp("com.milink.service", "Mi Link", true, "Cast"),
        BloatApp("com.miui.cleanmaster", "Cleaner", true, "Fausse app de nettoyage"),
        BloatApp("com.miui.hybrid", "Quick Apps", true, "Mini apps pub")
    )
}
