package vadlox.dev.hydraJoinMsgs;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.time.Year;
import java.util.Collections;
import java.util.List;

public class HydraJoinMsgs extends JavaPlugin implements Listener, TabCompleter {
    @Override
    public void onEnable() {
        // Save default config on first run
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("hydrajoinmsgs").setExecutor(this);
        getCommand("hydrajoinmsgs").setTabCompleter(this);
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!(sender instanceof Player) || sender.hasPermission("hydrajoinmsgs.admin") || sender.isOp()) {
                reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "HydraJoinMsgs config reloaded.");
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }
        }
        sender.sendMessage(ChatColor.RED + "Usage: /hydrajoinmsgs reload");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && (sender.hasPermission("hydrajoinmsgs.admin") || sender.isOp())) {
            return Collections.singletonList("reload");
        }
        return Collections.emptyList();
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
