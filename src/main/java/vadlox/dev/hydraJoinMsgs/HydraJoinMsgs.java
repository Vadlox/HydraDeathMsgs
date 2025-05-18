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
import org.bukkit.scheduler.BukkitRunnable;
import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import me.clip.placeholderapi.PlaceholderAPI;

public class HydraJoinMsgs extends JavaPlugin implements Listener, TabCompleter {
    private static final String REPO_URL = "https://api.github.com/repos/Vadlox/HydraJoinMsgs/releases/latest";

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
        checkForUpdates();
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

    private void checkForUpdates() {
        String currentVersion = getDescription().getVersion();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(REPO_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    String latestVersion = response.toString().split("\"tag_name\":\"")[1].split("\"")[0];
                    if (!currentVersion.equals(latestVersion)) {
                        for (Player player : getServer().getOnlinePlayers()) {
                            if (player.hasPermission("hydrajoinmsgs.admin")) {
                                player.sendMessage(ChatColor.YELLOW + "[HydraJoinMsgs] A new version (" + latestVersion + ") is available! Download it at: https://github.com/Vadlox/HydraJoinMsgs/releases");
                            }
                        }
                        getLogger().info(ChatColor.YELLOW + "A new version (" + latestVersion + ") is available! Download it at: https://github.com/Vadlox/HydraJoinMsgs/releases");
                    }
                } catch (Exception e) {
                    getLogger().warning(ChatColor.RED + "Failed to check for updates: " + e.getMessage());
                }
            }
        }.runTaskAsynchronously(this);
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
        Player player = event.getPlayer();
        String joinMessage = getConfig().getString("join-message", "&aWelcome, %player%! Enjoy your stay!").replace("%player%", player.getName());
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            joinMessage = PlaceholderAPI.setPlaceholders(player, joinMessage);
        }
        event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', joinMessage));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String leaveMessage = getConfig().getString("leave-message", "&c%player% has left the server. Goodbye!").replace("%player%", player.getName());
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            leaveMessage = PlaceholderAPI.setPlaceholders(player, leaveMessage);
        }
        event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', leaveMessage));
    }
}
