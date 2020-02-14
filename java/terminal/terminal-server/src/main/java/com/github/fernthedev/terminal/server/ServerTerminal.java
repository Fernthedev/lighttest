package com.github.fernthedev.terminal.server;

import com.github.fernthedev.config.common.Config;
import com.github.fernthedev.config.gson.GsonConfig;
import com.github.fernthedev.core.StaticHandler;
import com.github.fernthedev.fernutils.thread.ThreadUtils;
import com.github.fernthedev.light.LightManager;
import com.github.fernthedev.light.exceptions.NoPi4JLibsFoundException;
import com.github.fernthedev.server.Console;
import com.github.fernthedev.server.SenderInterface;
import com.github.fernthedev.server.Server;
import com.github.fernthedev.server.event.chat.ServerPlugin;
import com.github.fernthedev.server.settings.ServerSettings;
import com.github.fernthedev.terminal.core.CommonUtil;
import com.github.fernthedev.terminal.core.ConsoleHandler;
import com.github.fernthedev.terminal.core.TermCore;
import com.github.fernthedev.terminal.core.packets.MessagePacket;
import com.github.fernthedev.terminal.server.backend.AuthenticationManager;
import com.github.fernthedev.terminal.server.backend.AutoCompleteHandler;
import com.github.fernthedev.terminal.server.backend.BanManager;
import com.github.fernthedev.terminal.server.backend.ClientAutoCompleteHandler;
import com.github.fernthedev.terminal.server.command.AuthCommand;
import com.github.fernthedev.terminal.server.command.Command;
import com.github.fernthedev.terminal.server.command.LightCommand;
import com.github.fernthedev.terminal.server.command.SettingsCommand;
import lombok.Getter;
import org.fusesource.jansi.AnsiConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ServerTerminal {

    @Getter
    private static Config<ServerSettings> settingsManager;

    @Getter
    private static ServerCommandHandler commandHandler;

    private static Server server;

    @Getter
    private static BanManager banManager = new BanManager();

    @Getter
    private static CommandMessageParser commandMessageParser;


    @Getter
    private static AuthenticationManager authenticationManager;

    @Getter
    private static ClientAutoCompleteHandler autoCompleteHandler;


    @Getter
    private static List<Command> commandList = new ArrayList<>();


    private static Logger logger = LoggerFactory.getLogger(ServerTerminal.class);

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        java.util.logging.Logger.getLogger("io.netty").setLevel(java.util.logging.Level.OFF);
        StaticHandler.setupLoggers();


        //  Logger.getLogger("io.netty").setLevel(java.util.logging.Level.OFF);

        int port = -1;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.equalsIgnoreCase("-port")) {
                try {
                    port = Integer.parseInt(args[i + 1]);
                    if (port < 0) {
                        logger.error("-port cannot be less than 0");
                        port = -1;
                    } else logger.info("Using port {}", args[i + +1]);
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    logger.error("-port is not a number");
                    port = -1;
                }
            }

            if (arg.equalsIgnoreCase("-lightmanager")) {
                StaticHandler.setLight(true);
            }

            if (arg.equalsIgnoreCase("-debug")) {
                StaticHandler.setDebug(true);
                logger.debug("Debug enabled");
            }
        }


        CommonUtil.startSelfInCmd(args);


        settingsManager = new GsonConfig<>(new ServerSettings(), new File(getCurrentPath(), "settings.json"));
        settingsManager.save();

        port = settingsManager.getConfigData().getPort();


        server = new Server(port);
        server.addPacketHandler(new TerminalPacketHandler(server));
        server.setSettingsManager(settingsManager);
        commandHandler = new ServerCommandHandler(server);



        StaticHandler.setCore(new ServerTermCore(server));
        CommonUtil.registerTerminalPackets();

        new Thread(() -> {
            logger.info("Type Command: (try help)");
            new ConsoleHandler((TermCore) StaticHandler.getCore(), new AutoCompleteHandler(server)).start();
        }, "ConsoleHandler").start();

        commandMessageParser = new CommandMessageParser(server);
        server.getPluginManager().registerEvents(commandMessageParser, new ServerPlugin());

        registerCommand(new SettingsCommand(server));

        ThreadUtils.runAsync(() -> {
            authenticationManager = new AuthenticationManager(server);
            server.getPluginManager().registerEvents(authenticationManager, new ServerPlugin());
        });


        registerCommand(new AuthCommand("changepassword", server));

        autoCompleteHandler = new ClientAutoCompleteHandler(server);

        ThreadUtils.runAsync(() -> {
            if (StaticHandler.OS.equalsIgnoreCase("Linux") || StaticHandler.OS.contains("Linux") || StaticHandler.isLight()) {
                logger.info("Running LightManager (Note this is for raspberry pies only)");

                Thread lightThread = new Thread(() -> {

                    try {
                        LightManager.init();
                        registerCommand(new LightCommand(server));
                    } catch (IllegalArgumentException | ExceptionInInitializerError | NoPi4JLibsFoundException e) {
                        logger.error("Unable to load Pi4J Libraries. To load stacktrace, add -debug flag. Message: {}", e.getMessage());
                        if (StaticHandler.isDebug()) {
                            e.printStackTrace();
                            registerCommand(new LightCommand(server));
                        }
                    }

                }, "LightThread");
//                registerCommand(new LightCommand(this));
                lightThread.start();
            } else {
                logger.info("Detected system is not linux. LightManager will not run (manual run with -lightmanager arg)");
            }
        });

        new Thread(server, "ServerMainThread").start();
    }

    public static Command registerCommand(Command command) {
        commandList.add(command);
        return command;
    }

    public static List<Command> getCommands() {
        return commandList;
    }

    public static String getCurrentPath() {
        return Paths.get("").toAbsolutePath().toString();
    }

    public static void broadcast(String message) {
        Server.getLogger().info(message);
        server.sendObjectToAllPlayers(new MessagePacket(message));
    }

    public static void sendMessage(SenderInterface senderInterface, String message) {
        if (senderInterface instanceof Console) logger.info(message);
        else senderInterface.sendPacket(new MessagePacket(message));
    }
}