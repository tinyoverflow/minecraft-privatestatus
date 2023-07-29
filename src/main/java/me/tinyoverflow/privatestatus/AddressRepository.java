package me.tinyoverflow.privatestatus;

import me.tinyoverflow.privatestatus.events.AddAddressEvent;
import me.tinyoverflow.privatestatus.events.ExpireAddressEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Logger;

public class AddressRepository
{
    private final Logger logger;
    private final HashMap<OfflinePlayer, InetAddress> storage = new HashMap<>();

    public AddressRepository(Logger logger)
    {
        this.logger = logger;
    }

    /**
     * Loads data from a map consisting of the UUID string as the key and the base64 encoded address as the value.
     *
     * @param configMap
     */
    public void fromMap(@NotNull Map<String, Object> configMap)
    {
        // Make sure we work with a fresh map.
        storage.clear();

        // Process each and every item inside the section.
        for (Map.Entry<String, Object> entry : configMap.entrySet()) {
            UUID uuid = UUID.fromString(entry.getKey());
            String encodedAddress = (String) entry.getValue();

            // Fire the expiration event to allow other listeners to intercept the expiration logic.
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            ExpireAddressEvent event = new ExpireAddressEvent(offlinePlayer);
            if (event.callEvent()) {
                logger.info("Skipping expired address for " + uuid);
                continue;
            }

            // Decode the stored address and add it to the repository.
            try {
                InetAddress inetAddress = InetAddress.getByAddress(Base64.getDecoder().decode(encodedAddress));
                storage.put(offlinePlayer, inetAddress);
            } catch (UnknownHostException e) {
                logger.warning("Unknown host found in config. Skipping: " + encodedAddress);
            }
        }

        // Notify the user about the loading state.
        logger.info("Loaded " + storage.size() + " addresses from configuration.");
    }

    /**
     * Returns the data as a string map.
     *
     * @return The map with the UUID string as the key and the base64 encoded address as the value.
     */
    public Map<String, String> toMap()
    {
        Map<String, String> addressList = new HashMap<>();

        for (Map.Entry<OfflinePlayer, InetAddress> entry : storage.entrySet()) {
            UUID uuid = entry.getKey().getUniqueId();
            InetAddress inetAddress = entry.getValue();

            addressList.put(uuid.toString(), Base64.getEncoder().encodeToString(inetAddress.getAddress()));
        }

        return addressList;
    }

    /**
     * Checks whether the given address is known.
     *
     * @param inetAddress The address to check for.
     * @return {@code true} if the address is known, {@code false} otherwise.
     */
    public boolean hasAddress(InetAddress inetAddress)
    {
        return storage.containsValue(inetAddress);
    }

    /**
     * Remembers a player and the address that belongs to it.
     *
     * @param offlinePlayer The player to remember.
     * @param inetAddress   The address to associate with that player.
     */
    public void add(OfflinePlayer offlinePlayer, InetAddress inetAddress)
    {
        AddAddressEvent event = new AddAddressEvent(offlinePlayer, inetAddress);
        if (event.callEvent()) {
            storage.put(offlinePlayer, inetAddress);
        }
    }
}
