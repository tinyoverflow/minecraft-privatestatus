package me.tinyoverflow.privatestatus;

import me.tinyoverflow.privatestatus.events.AddAddressEvent;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.Line;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class AddressRepository
{
    private final Logger logger;
    private final HashMap<InetAddress, LocalDateTime> storage = new HashMap<>();
    private final ZoneOffset zoneOffset;

    public AddressRepository(Logger logger)
    {
        this.logger = logger;
        this.zoneOffset = OffsetDateTime.now().getOffset();
    }

    /**
     * Loads data from a map consisting of the UUID string as the key and the base64 encoded address as the value.
     *
     * @param configMap Map of key => value pairs from the configuration file.
     */
    public void fromMap(@NotNull Map<String, Object> configMap)
    {
        // Make sure we work with a fresh map.
        storage.clear();

        // Process each and every item inside the section.
        for (Map.Entry<String, Object> entry : configMap.entrySet()) {
            String address = entry.getKey();
            LocalDateTime expiration = LocalDateTime.ofEpochSecond((Long) entry.getValue(), 0, zoneOffset);

            // Decode the stored address and add it to the repository.
            try {
                InetAddress inetAddress = InetAddress.getByName(address);
                storage.put(inetAddress, expiration);
            }
            catch (UnknownHostException e) {
                logger.warning("Unknown host found in config. Skipping: " + address);
            }
        }

        // Notify the user about the loading state.
        logger.info("Loaded " + storage.size() + " addresses from configuration.");
    }

    /**
     * Loads known addresses from a {@code List<Map<String, Object>>}.
     *
     * @param data List of data.
     */
    public void fromList(List<Map<String, Object>> data)
    {
        for (Map<String, Object> section : data) {
            if (section.get("ip") == null || section.get("expiration") == null) {
                continue;
            }

            String address = (String) section.get("ip");
            long expirationTimestamp = ((Integer) section.get("expiration")).longValue();

            try {
                storage.put(
                        InetAddress.getByName(address),
                        LocalDateTime.ofEpochSecond(expirationTimestamp, 0, zoneOffset)
                );
            }
            catch (UnknownHostException e) {
                logger.warning("Unknown host found in config. Skipping: " + address);
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
    public Map<String, Long> toMap()
    {
        Map<String, Long> addressList = new HashMap<>();

        for (Map.Entry<InetAddress, LocalDateTime> entry : storage.entrySet()) {
            InetAddress inetAddress = entry.getKey();
            LocalDateTime localDateTime = entry.getValue();

            addressList.put(inetAddress.getHostAddress(), localDateTime.toEpochSecond(zoneOffset));
        }

        return addressList;
    }

    public List<Map<String, Object>> toList()
    {
        List<Map<String, Object>> addressList = new ArrayList<>();

        for (Map.Entry<InetAddress, LocalDateTime> entry : storage.entrySet()) {
            InetAddress inetAddress = entry.getKey();
            LocalDateTime localDateTime = entry.getValue();

            Map<String, Object> data = new HashMap<>();
            data.put("ip", inetAddress.getHostAddress());
            data.put("expiration", localDateTime.toEpochSecond(zoneOffset));

            addressList.add(data);
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
        return storage.containsKey(inetAddress);
    }

    /**
     * Remembers a player and the address that belongs to it.
     *
     * @param inetAddress The address to associate with that player.
     */
    public void add(OfflinePlayer player, InetAddress inetAddress, LocalDateTime expiration)
    {
        AddAddressEvent event = new AddAddressEvent(player, inetAddress, expiration);
        if (event.callEvent()) {
            storage.put(inetAddress, expiration);
        }
    }

    public Map<InetAddress, LocalDateTime> getAll()
    {
        return (Map<InetAddress, LocalDateTime>) storage.clone();
    }

    public void remove(InetAddress address)
    {
        storage.remove(address);
    }
}
