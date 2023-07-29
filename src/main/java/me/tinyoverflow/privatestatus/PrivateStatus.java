package me.tinyoverflow.privatestatus;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import me.tinyoverflow.privatestatus.events.ExpireAddressEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class PrivateStatus extends JavaPlugin implements Listener
{
    private final String CONFIG_EXPIRATION_DAYS = "expiration-days";
    private final String CONFIG_KNOWN_ADDRESSES = "known-addresses";

    private AddressRepository repository;

    @Override
    public void onLoad()
    {
        // Initialize repository.
        repository = new AddressRepository(getLogger());

        // Set sensible configuration defaults.
        getConfig().options().copyDefaults(true);

        getConfig().addDefault(CONFIG_EXPIRATION_DAYS, 3);
        getConfig().setComments(CONFIG_EXPIRATION_DAYS, List.of("The amount of days after which addresses will expire. Minimum: 1."));

        getConfig().addDefault(CONFIG_KNOWN_ADDRESSES, new HashMap<String, Object>());
        getConfig().setComments(CONFIG_KNOWN_ADDRESSES, List.of("A list of already known addresses."));

        saveConfig();
    }

    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(this, this);

        // Ensure the expiration days setting is at least 1.
        if (getConfig().getInt(CONFIG_EXPIRATION_DAYS) < 1) {
            getConfig().set(CONFIG_EXPIRATION_DAYS, 3);
        }

        // Load addresses from configuration file, if the section exists.
        ConfigurationSection configurationSection = getConfig().getConfigurationSection(CONFIG_KNOWN_ADDRESSES);
        if (configurationSection != null) {
            repository.fromMap(configurationSection.getValues(false));
        }
    }

    @Override
    public void onDisable()
    {
        // Save configuration to disk.
        getConfig().set(CONFIG_KNOWN_ADDRESSES, repository.toMap());
        saveConfig();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        InetSocketAddress socketAddress = player.getAddress();
        if (socketAddress == null) {
            getLogger().log(Level.WARNING, "Could not retrieve IP address of player " + player.getName());
            return;
        }

        InetAddress address = socketAddress.getAddress();
        if (address.isAnyLocalAddress()) {
            getLogger().log(Level.INFO, "Player " + player.getName() + " joined with local address. Ignoring.");
            return;
        }

        repository.add(player, address);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerListPingEvent(PaperServerListPingEvent event)
    {
        if (!repository.hasAddress(event.getAddress())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onExpireAddressEvent(ExpireAddressEvent event)
    {
        int expirationDays = getConfig().getInt(CONFIG_EXPIRATION_DAYS);
        long expirationDeadline = new Date().getTime() - expirationDays * 86400000L;

        event.setCancelled(event.getPlayer().getLastLogin() >= expirationDeadline);
    }
}
