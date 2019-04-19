package com.github.fernthedev.server;

import com.github.fernthedev.server.backend.auth.AuthenticationManager;
import com.github.fernthedev.packets.*;
import com.github.fernthedev.packets.latency.PongPacket;
import com.github.fernthedev.packets.message.MessagePacket;
import com.github.fernthedev.server.backend.CommandMessageParser;
import com.github.fernthedev.server.event.chat.ChatEvent;
import com.github.fernthedev.universal.StaticHandler;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class EventListener {

    private Server server;

    private ClientPlayer clientPlayer;

    public EventListener(Server server, ClientPlayer clientPlayer) {
        this.server = server;
        this.clientPlayer = clientPlayer;
    }

    public void received(Packet p) {

        //Packet p = (Packet) EncryptionHandler.decrypt(pe, clientPlayer.getServerKey());


        // Server.getLogger().info(clientPlayer + " is the sender of packet");

        if (p instanceof TestConnectPacket) {
            TestConnectPacket packet = (TestConnectPacket) p;
            Server.getLogger().info("Connected packet: " + packet.getMessage());
        } else if (p instanceof PongPacket) {
            clientPlayer.endTime = System.nanoTime();

            clientPlayer.setDelayTime((clientPlayer.endTime - clientPlayer.startTime) / 1000000);

        } else if (p instanceof MessagePacket) {
            MessagePacket messagePacket = (MessagePacket) p;

            ChatEvent chatEvent = new ChatEvent(clientPlayer, messagePacket.getMessage(), messagePacket.isCommand(), true);
            CommandMessageParser.handleEvent(chatEvent);

            /*if(!chatEvent.isCancelled()) {
                Server.sendMessage("[" + clientPlayer.getDeviceName() + "] :" + messagePacket.getMessage());
            }*/
        } else if (p instanceof AutoCompletePacket) {
            AutoCompletePacket packet = (AutoCompletePacket) p;
            List<LightCandidate> candidates = server.getAutoCompleteHandler().handleLine(packet.getWords());

            packet.setCandidateList(candidates);
            clientPlayer.sendObject(packet);
        }

    }

    private Object decrypt(SealedObject sealedObject) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        SecretKeySpec sks = new SecretKeySpec(clientPlayer.getClientKey().getBytes(), StaticHandler.getCipherTransformation());
        Cipher cipher = Cipher.getInstance(StaticHandler.getCipherTransformation());
        cipher.init(Cipher.DECRYPT_MODE, sks);

        try {
            return sealedObject.getObject(cipher);
        } catch (ClassNotFoundException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void handleConnect(ConnectedPacket packet) {
        //Server.getLogger().info("Connected packet received from " + clientPlayer.getAdress());
        int id = 1;

        if (PlayerHandler.players.size() > 0) {
            /*while(id < PlayerHandler.players.size()) {
                id++;
            }*/
            int lastId = 0;

            for (int i = 0; i < PlayerHandler.players.size(); i++) {
                if (PlayerHandler.players.get(i) == null) {
                    id = lastId + 1;
                } else {
                    lastId = PlayerHandler.players.get(i).getId();
                }
            }
        }

        if (!isAlphaNumeric(packet.getName())) {
            disconnectIllegalName(packet, "Name requires alphanumeric characters only");
            return;
        }


        for (ClientPlayer player : PlayerHandler.players.values()) {
            if (player.getDeviceName().equalsIgnoreCase(packet.getName())) {
                disconnectIllegalName(packet, "Name already in use");
                return;
            }
        }


        /*
        Server.getLogger().debug(server.getBanManager() + " is result");

        if(server.getBanManager().isBanned(clientPlayer)) {
            Server.getLogger().debug("Player is banned " + clientPlayer.channel.remoteAddress());
            clientPlayer.sendObject(new MessagePacket("Your have been banned."),false);
            clientPlayer.close();
            return;
        }*/

        clientPlayer.setClientUUID(packet.getUuid(), packet.getPrivateKey());


        //Server.getLogger().info("Players: " + PlayerHandler.players.size());


        clientPlayer.setDeviceName(packet.getName());
        clientPlayer.setId(id);
        clientPlayer.os = packet.getOS();


        PlayerHandler.players.put(clientPlayer.getId(), clientPlayer);

        //  Server.getLogger().info("Password required: " + server.getSettingsManager().getSettings().isPasswordRequiredForLogin());

        if (server.getSettingsManager().getSettings().isPasswordRequiredForLogin()) {
            boolean authenticated = AuthenticationManager.authenticate(clientPlayer);
            if (!authenticated) {
                clientPlayer.sendObject(new MessagePacket("Unable to authenticate"));
                clientPlayer.close();
                return;
            }
        }

        clientPlayer.registered = true;

        Server.getLogger().info(clientPlayer.getDeviceName() + " has connected to the server [" + clientPlayer.os + "]");
        clientPlayer.sendObject(new RegisterPacket());
        Server.getLogger().debug("NAME:ID " + clientPlayer.getDeviceName() + ":" + clientPlayer.getId());
        Server.getLogger().debug(PlayerHandler.players.get(clientPlayer.getId()).getDeviceName() + " the name." + PlayerHandler.players.get(clientPlayer.getId()).getId() + " the id");

    }

    public boolean isAlphaNumeric(String name) {
        return StringUtils.isAlphanumericSpace(name.replace('-', ' '));
    }

    private void disconnectIllegalName(ConnectedPacket packet, String message) {
        Server.getLogger().info(clientPlayer + " was disconnected for illegal name. Name: " + packet.getName() + " Reason: " + message + " ID " + clientPlayer.getId());
        clientPlayer.sendObject(new IllegalConnection("You have been disconnected for illegal name. Name: " + packet.getName() + " Reason: " + message), false);
        clientPlayer.close();
    }
}