package vadlox.dev.hydraJoinMsgs;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import java.time.Year;

public class HydraJoinMsgs extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        // Save default config on first run
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        String enableMessage = ChatColor.GREEN + "\n" +
                "  ██╗  ██╗██████╗  \n" +
                "  ██║  ██║██╔══██╗  HydraJoinMsgs (Plugin Successfully Enabled)\n" +
                "  ███████║██████╔╝  Copyright Vadlox " + Year.now().getValue() + "\n" +
                "  ██╔══██║██╔═══╝   Github: https://github.com/vadlox\n" +
                "  ██║  ██║██║       Portfolio: https://vadlox.hydraquest.net\n" +
                "  ╚═╝  ╚═╝╚═╝\n";
        getLogger().info(enableMessage);
    }

    @Override
    public void onDisable() {
        String disableMessage = ChatColor.RED + "\n" +
                "  ██╗  ██╗██████╗  \n" +
                "  ██║  ██║██╔══██╗  HydraJoinMsgs (Plugin Successfully Disabled)\n" +
                "  ███████║██████╔╝  Copyright Vadlox " + Year.now().getValue() + "\n" +
                "  ██╔══██║██╔═══╝   Github: https://github.com/vadlox\n" +
                "  ██║  ██║██║       Portfolio: https://vadlox.hydraquest.net\n" +
                "  ╚═╝  ╚═╝╚═╝\n";
        getLogger().info(disableMessage);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String joinMessage = getConfig().getString("join-message", "&aWelcome, %player%! Enjoy your stay!").replace("%player%", event.getPlayer().getName());
        event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', joinMessage));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String leaveMessage = getConfig().getString("leave-message", "&c%player% has left the server. Goodbye!").replace("%player%", event.getPlayer().getName());
        event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', leaveMessage));
    }
}
