package gambling;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface GamblingGame {
    void handleMessage(MessageReceivedEvent event); // process the message
    boolean isActive(String userId); // is user in the middle of this game?
}
