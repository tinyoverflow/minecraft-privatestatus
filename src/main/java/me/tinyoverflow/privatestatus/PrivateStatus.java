package me.tinyoverflow.privatestatus;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import me.tinyoverflow.privatestatus.jobs.PruneExpiredAddressesJob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class PrivateStatus extends JavaPlugin implements Listener
{
    private final String CONFIG_EXPIRATION_MINUTES = "expiration-minutes";
    private final String CONFIG_KNOWN_ADDRESSES = "known-addresses";

    private AddressRepository repository;

    @Override
    public void onLoad()
    {
        // Initialize repository.
        repository = new AddressRepository(getLogger());

        // Set sensible configuration defaults.
        getConfig().options().copyDefaults(true);

        getConfig().addDefault(CONFIG_EXPIRATION_MINUTES, 3);
        getConfig().setComments(
                CONFIG_EXPIRATION_MINUTES,
                List.of("The amount of minutes after which addresses will expire. Minimum: 10.")
        );

        getConfig().addDefault(CONFIG_KNOWN_ADDRESSES, new HashMap<String, Object>());
        getConfig().setComments(CONFIG_KNOWN_ADDRESSES, List.of("A list of already known addresses."));

        saveConfig();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(this, this);

        // Ensure the expiration minutes setting is at least 10.
        if (getConfig().getInt(CONFIG_EXPIRATION_MINUTES) < 10) {
            getConfig().set(CONFIG_EXPIRATION_MINUTES, 10);
        }

        // Load addresses from configuration file, if the section exists.
        List<?> data = getConfig().getList(CONFIG_KNOWN_ADDRESSES, new ArrayList<>());
        repository.fromList((List<Map<String, Object>>) data);
        //        ConfigurationSection configurationSection = getConfig().getConfigurationSection(CONFIG_KNOWN_ADDRESSES);
        //        if (configurationSection != null) {
        //            repository.fromMap(configurationSection.getValues(false));
        //        }

        // Initialize Metrics
        PrivateStatusMetrics metrics = new PrivateStatusMetrics(this, 19291);
        metrics.setExpirationMinutesMetric(getConfig().getInt(CONFIG_EXPIRATION_MINUTES));

        // Run prune job every 10 minutes.
        getServer().getScheduler().scheduleSyncRepeatingTask(
                this,
                new PruneExpiredAddressesJob(getLogger(), repository),
                0,
                10 * 60 * 20
        );
    }

    @Override
    public void onDisable()
    {
        // Save configuration to disk.
        getConfig().set(CONFIG_KNOWN_ADDRESSES, repository.toList());
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

        LocalDateTime expiration = LocalDateTime.now().plusMinutes(getConfig().getInt(CONFIG_EXPIRATION_MINUTES));
        repository.add(player, address, expiration);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerListPingEvent(PaperServerListPingEvent event)
    {
        if (!repository.hasAddress(event.getAddress())) {
            event.setCancelled(true);
        }
    }
}
