package com.github.fernthedev.client;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.UUID;


public class Client {
    public boolean registered;
    protected Scanner scanner;
    public boolean running = false;


    protected static Logger logger;

    public int port;
    public String host;

    @Getter
    @Setter
    @NonNull
    protected String serverKey;

    @Getter
    @Setter
    @NonNull
    protected String privateKey;



    @Getter
    protected UUID uuid;

    public String name;
    public static WaitForCommand waitForCommand;
    public static Thread waitThread;
    private static CLogger cLogger;

    public static Thread currentThread;

    protected ClientThread clientThread;

    protected boolean closeConsole = true;

    public boolean isCloseConsole() {
        return closeConsole;
    }


    public Client(String host, int port) {
        this.port = port;
        this.host = host;
        this.scanner = Main.scanner;

        registerLogger();


        try {
            name = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            getLogger().logError(e.getMessage(), e.getCause());
            clientThread.close();
        }

        clientThread = new ClientThread(this);

        waitForCommand = new WaitForCommand(this);



        currentThread = new Thread(clientThread,"MainThread");

    }

    protected void getProperties() {
        System.getProperties().list(System.out);
    }

    public void initialize() {
        logger.info("Initializing");
        name = null;
        clientThread.connected = false;
        clientThread.connectToServer = true;
        clientThread.running = true;



        clientThread.connect();

    }

    private void registerLogger() {
        logger = Logger.getLogger(Client.class.getName());
        cLogger = new CLogger(logger);
    }

    public String getOSName() {
        return System.getProperty("os.name");
    }

    public static synchronized CLogger getLogger() {
        if(logger == null) {
            logger = Logger.getLogger(Client.class.getName());
        }

        return cLogger;
    }

    public ClientThread getClientThread() {
        return clientThread;
    }
}
