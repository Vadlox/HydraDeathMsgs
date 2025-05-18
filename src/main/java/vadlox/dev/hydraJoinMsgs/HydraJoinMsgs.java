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
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import me.clip.placeholderapi.PlaceholderAPI;

public class HydraJoinMsgs extends JavaPlugin implements Listener, TabCompleter {
    private static final String REPO_URL = "https://api.github.com/repos/Vadlox/HydraJoinMsgs/releases/latest";

    @Override
    public void onEnable() {
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
        String current = getDescription().getVersion();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(REPO_URL).openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) sb.append(line);
                    in.close();
                    String latest = sb.toString().split("\"tag_name\":\"")[1].split("\"")[0];
                    if (isNewer(latest, current)) {
                        String msg = ChatColor.YELLOW + "[HydraJoinMsgs] New version " + latest + " available! https://github.com/Vadlox/HydraJoinMsgs/releases";
                        getServer().getOnlinePlayers().stream()
                                .filter(p -> p.hasPermission("hydrajoinmsgs.admin"))
                                .forEach(p -> p.sendMessage(msg));
                        getLogger().info(msg);
                    }
                } catch (Exception e) {
                    getLogger().warning("Failed to check updates: " + e.getMessage());
                }
            }
        }.runTaskAsynchronously(this);
    }

    private boolean isNewer(String latest, String current) {
        List<String> l = Arrays.asList(latest.split("\\."));
        List<String> c = Arrays.asList(current.split("\\."));
        int len = Math.max(l.size(), c.size());
        for (int i = 0; i < len; i++) {
            int lv = i < l.size() ? Integer.parseInt(l.get(i).replaceAll("\\D", "")) : 0;
            int cv = i < c.size() ? Integer.parseInt(c.get(i).replaceAll("\\D", "")) : 0;
            if (lv > cv) return true;
            if (lv < cv) return false;
        }
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("hydrajoinmsgs.admin") || sender.isOp()) {
                    reloadConfig();
                    sender.sendMessage(ChatColor.GREEN + "HydraJoinMsgs config reloaded.");
                } else sender.sendMessage(ChatColor.RED + "No permission.");
                return true;
            }
            if (args[0].equalsIgnoreCase("checkupdate")) {
                if (sender.hasPermission("hydrajoinmsgs.admin") || sender.isOp()) {
                    String current = getDescription().getVersion();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            try {
                                HttpURLConnection conn = (HttpURLConnection) new URL(REPO_URL).openConnection();
                                conn.setRequestMethod("GET");
                                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                StringBuilder sb = new StringBuilder();
                                String line;
                                while ((line = in.readLine()) != null) sb.append(line);
                                in.close();
                                String latest = sb.toString().split("\"tag_name\":\"")[1].split("\"")[0];
                                if (isNewer(latest, current)) {
                                    sender.sendMessage(ChatColor.YELLOW + "New version " + latest + " available: https://github.com/Vadlox/HydraJoinMsgs/releases/tag/" + latest);
                                } else {
                                    sender.sendMessage(ChatColor.GREEN + "You are on the latest version (" + current + ").");
                                }
                            } catch (Exception e) {
                                sender.sendMessage(ChatColor.RED + "Update check failed: " + e.getMessage());
                            }
                        }
                    }.runTaskAsynchronously(this);
                } else sender.sendMessage(ChatColor.RED + "No permission.");
                return true;
            }
        }
        sender.sendMessage(ChatColor.RED + "Usage: /hydrajoinmsgs <reload|checkupdate>");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1 && (sender.hasPermission("hydrajoinmsgs.admin") || sender.isOp())) {
            return Arrays.asList("reload", "checkupdate");
        }
        return Collections.emptyList();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        String msg = getConfig().getString("join-message", "&aWelcome, %player%! Enjoy your stay!").replace("%player%", p.getName());
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) msg = PlaceholderAPI.setPlaceholders(p, msg);
        event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        String msg = getConfig().getString("leave-message", "&c%player% has left the server. Goodbye!").replace("%player%", p.getName());
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) msg = PlaceholderAPI.setPlaceholders(p, msg);
        event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }
}
