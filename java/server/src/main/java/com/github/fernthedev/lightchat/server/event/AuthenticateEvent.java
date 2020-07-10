package com.github.fernthedev.lightchat.server.event;

import com.github.fernthedev.lightchat.core.api.event.api.Cancellable;
import com.github.fernthedev.lightchat.core.api.event.api.Event;
import com.github.fernthedev.lightchat.core.api.event.api.HandlerList;
import com.github.fernthedev.lightchat.server.security.AuthenticationManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Called when client has successfully established a secure and valid connection
 */
@RequiredArgsConstructor
public class AuthenticateEvent extends Event implements Cancellable {
    private boolean cancel = false;
    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final AuthenticationManager.PlayerInfo playerInfo;

    public AuthenticateEvent(AuthenticationManager.PlayerInfo playerInfo, boolean async) {
        super(async);
        this.playerInfo = playerInfo;
    }


    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancel;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
