package com.darkzek.ChickenBot;

import com.darkzek.ChickenBot.Commands.CommandLoader;
import com.darkzek.ChickenBot.Configuration.GuildConfigurationManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by darkzek on 21/02/18.
 */
public class ChickenBot extends ListenerAdapter{

    //TODO: Move guild commands system to the new configuration api
    //TODO: Add better messaging system - like a language file
    //TODO: Add more units of time to RemindMe
    //TODO: Add tldr function to summarise links posted https://github.com/ceteri/textrank
    //TODO: Add better event system with automatic arg fetching

    public static JDA jda;

    public static void main(String[] args) {

        //Setup logging
        if (!runningFromIntelliJ()) {
            try {
                PrintStream out = new PrintStream(new FileOutputStream("latest.txt"));
                System.setOut(out);
                System.setErr(out);
            } catch (IOException e) {

            }

            //Setup error listener
            ExceptionTracker.registerExceptionHandler();
        }

        //Load configs
        GuildConfigurationManager.getInstance();

        //Setup account
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(Settings.getInstance().getToken());
        builder.setAutoReconnect(true);
        builder.setStatus(OnlineStatus.ONLINE);

        //Setup the command manager so it can listen to events
        builder.addEventListener(CommandManager.getInstance());

        //Load all the commands
        CommandLoader.Load();

        //Connect
        try {
            jda = builder.buildBlocking();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            CommandManager.getInstance().onShutdown();
            jda.shutdown();
        }));

        if (!runningFromIntelliJ()) {
            TellMe("Started ChickenBot with version " + new Version().getVersion());
        }

        //Add the events listner to listen for stuff
        jda.addEventListener(new EventsListener());

        jda.getPresence().setGame(Game.playing(">help | " + jda.getGuilds().size() + " servers"));

        System.out.println("Started Chicken Bot V1");
    }

    public static void TellMe(String message) {
        User darkzek = jda.getUserById("130173614702985216");

        PrivateChannel pc = darkzek.openPrivateChannel().complete();

        pc.sendMessage(message).queue();
    }

    public static boolean runningFromIntelliJ()
    {
        String classPath = System.getProperty("java.class.path");
        return classPath.contains("idea_rt.jar");
    }
}
