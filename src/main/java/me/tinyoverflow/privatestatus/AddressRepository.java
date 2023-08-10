package me.tinyoverflow.privatestatus;

import me.tinyoverflow.privatestatus.events.AddAddressEvent;
import org.bukkit.OfflinePlayer;

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

    @SuppressWarnings("unchecked")
    public Map<InetAddress, LocalDateTime> getAll()
    {
        return (Map<InetAddress, LocalDateTime>) storage.clone();
    }

    public void remove(InetAddress address)
    {
        storage.remove(address);
    }
}
