package me.tinyoverflow.privatestatus.jobs;

import me.tinyoverflow.privatestatus.AddressRepository;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Map;
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

        Map<InetAddress, LocalDateTime> addresses = repository.getAll();
        for (Map.Entry<InetAddress, LocalDateTime> entry : addresses.entrySet()) {
            InetAddress address = entry.getKey();
            LocalDateTime expiration = entry.getValue();

            if (expiration.isBefore(LocalDateTime.now())) {
                repository.remove(address);
                removedAddresses++;
            }
        }

        logger.fine("Pruning completed. Removed " + removedAddresses + " expired addresses.");
    }
}
