package me.tinyoverflow.privatestatus.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.time.LocalDateTime;

public class AddAddressEvent extends Event implements Cancellable
{
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final OfflinePlayer offlinePlayer;
    private final InetAddress address;
    private final LocalDateTime expiration;
    private boolean cancelled = false;

    public AddAddressEvent(OfflinePlayer offlinePlayer, InetAddress address, LocalDateTime expiration)
    {
        this.offlinePlayer = offlinePlayer;
        this.address = address;
        this.expiration = expiration;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList()
    {
        return HANDLER_LIST;
    }

    @SuppressWarnings("unused")
    public OfflinePlayer getOfflinePlayer()
    {
        return offlinePlayer;
    }

    @SuppressWarnings("unused")
    public InetAddress getAddress()
    {
        return address;
    }

    @SuppressWarnings("unused")
    public LocalDateTime getExpirationDateTime() {
        return expiration;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel)
    {
        cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers()
    {
        return HANDLER_LIST;
    }
}
