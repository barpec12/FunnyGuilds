package net.dzikoysk.funnyguilds.event.guild.ally;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;

public class GuildAcceptAllyInvitationEvent extends GuildAllyEvent {

    public GuildAcceptAllyInvitationEvent(EventCause eventCause, User doer, Guild guild, Guild alliedGuild) {
        super(eventCause, doer, guild, alliedGuild);
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Alliance acceptance has been cancelled by the server!";
    }

}
