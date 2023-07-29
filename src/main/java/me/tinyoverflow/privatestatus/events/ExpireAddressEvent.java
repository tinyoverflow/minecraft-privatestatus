package me.tinyoverflow.privatestatus.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event will be fired when an address should be checked for expiration.
 * If this event gets cancelled, the address will not be expired.
 */
public class ExpireAddressEvent extends Event implements Cancellable
{
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final OfflinePlayer player;
    private boolean cancelled = false;

    public ExpireAddressEvent(OfflinePlayer player)
    {
        this.player = player;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList()
    {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers()
    {
        return HANDLER_LIST;
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

    @SuppressWarnings("unused")
    public OfflinePlayer getPlayer()
    {
        return player;
    }
}
