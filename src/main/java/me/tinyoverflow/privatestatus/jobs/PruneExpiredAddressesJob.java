package me.tinyoverflow.privatestatus.jobs;

import me.tinyoverflow.privatestatus.AddressRepository;
import org.bukkit.OfflinePlayer;

import java.util.Set;
import java.util.logging.Logger;

public class PruneExpiredAddressesJob implements Runnable
{
    private final Logger logger;
    private final AddressRepository repository;

    public PruneExpiredAddressesJob(Logger logger, AddressRepository repository)
    {
        this.logger = logger;
        this.repository = repository;
    }

    @Override
    public void run()
    {
        logger.fine("Pruning expired addresses...");
        int removedAddresses = 0;

        Set<OfflinePlayer> offlinePlayerList = repository.getPlayers();
        for (OfflinePlayer offlinePlayer : offlinePlayerList)
        {
            if (!repository.isExpired(offlinePlayer)) continue;

            repository.remove(offlinePlayer);
            removedAddresses++;
        }

        logger.fine("Pruning completed. Removed " + removedAddresses + " expired addresses.");
    }
}
