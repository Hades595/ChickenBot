package main.java.com.darkzek.SheepBot;

import main.java.com.darkzek.SheepBot.Commands.Command;
import main.java.com.darkzek.SheepBot.Enums.MessageType;
import main.java.com.darkzek.SheepBot.Enums.TriggerType;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import sun.plugin2.message.Message;

import javax.xml.bind.Marshaller;
import java.util.Set;

/**
 * Created by darkzek on 28/02/18.
 */
public class Trigger {
    TriggerType type;
    String arg;
    Command command;
    boolean includeBots = false;
    boolean ignoreCase = false;
    public MessageType messageType = MessageType.GUILD;

    public Trigger(Command command, TriggerType type) {
        this.type = type;
        this.command = command;
        Setup();
    }

    public Trigger(Command command, TriggerType type, String argument) {
        this.type = type;
        arg = argument;
        this.command = command;
        Setup();
    }

    public void IncludeBots(boolean includeBots) {
        this.includeBots = includeBots;
    }

    public void SetIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
        if (ignoreCase) {
            arg = arg.toLowerCase();
        }
    }

    public void MessageRecieved(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();

        if (!includeBots && event.getAuthor().isBot()) {
            return;
        }

        if (type == TriggerType.MESSAGE_SENT_CONTAINS) {
            //Check if the message contains the correct phrase
            if (!message.contains(arg)) {
                return;
            }
        }

        MessageType msgType;
        if (event.getChannelType().isGuild()) {
            msgType = MessageType.GUILD;
        } else {
            msgType = MessageType.PRIVATE;
        }

        //Check if the command should even run for this type of message
        if (this.messageType != MessageType.BOTH && this.messageType != msgType) {
            return;
        }

        if (ignoreCase) {
            message = message.toLowerCase();
        }

        if (type == TriggerType.COMMAND) {
            String m = event.getMessage().getContentStripped();
            if (!event.getMessage().isMentioned(event.getJDA().getSelfUser(), net.dv8tion.jda.core.entities.Message.MentionType.USER)) {
                //Check if the message contains the correct phrase
                if (!message.startsWith(Settings.enabler)) {
                    return;
                }
                m = m.substring(1);
            } else {
                int index = m.indexOf(' ') + 1;
                if (index == -1) {
                    return;
                }
                m = m.substring(index);
            }
            if (!m.startsWith(arg)) {
                return;
            }
        }

        command.MessageRecieved(event);
    }

    public void MessageDeleted(MessageDeleteEvent event) {
        command.MessageDeleted(event);
    }

    public void Setup() {
        //Let the command manager know there's another command
        switch (type) {
            case MESSAGE_SENT: {
                CommandManager.getInstance().AddMessageListener(this);
                break;
            }
            case MESSAGE_SENT_CONTAINS: {
                CommandManager.getInstance().AddMessageListener(this);
                break;
            }
            case COMMAND: {
                CommandManager.getInstance().AddMessageListener(this);
                break;
            }
            case MESSAGE_DELETED: {
                CommandManager.getInstance().AddMessageDeletedListener(this);
                break;
            }
        }
    }
}