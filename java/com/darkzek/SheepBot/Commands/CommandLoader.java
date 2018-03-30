package main.java.com.darkzek.SheepBot.Commands;

import main.java.com.darkzek.SheepBot.ChickenBot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by darkzek on 28/02/18.
 */
public class CommandLoader {

    public static List<Command> commands = new ArrayList<>();

    public static void Load() {
        commands.add(new GNULinux());
        commands.add(new Help());
        commands.add(new Purge());
        commands.add(new Allegedly());
        commands.add(new LMGTFY());
        commands.add(new Search());
        commands.add(new UnixPorn());

        ChickenBot.Log("Successfully loaded all plugins");
    }
}