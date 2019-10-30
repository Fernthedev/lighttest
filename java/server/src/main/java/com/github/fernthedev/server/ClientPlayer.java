package com.github.fernthedev.server;

import com.github.fernthedev.packets.MessagePacket;
import com.github.fernthedev.packets.Packet;
import com.github.fernthedev.packets.latency.PingPacket;
import com.github.fernthedev.server.command.CommandSender;
import com.github.fernthedev.universal.encryption.UnencryptedPacketWrapper;
import com.github.fernthedev.universal.encryption.util.RSAEncryptionUtil;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.util.UUID;

import static com.github.fernthedev.server.PlayerHandler.socketList;

public class ClientPlayer implements CommandSender {
    private boolean connected;

    public boolean registered = false;

    @Getter
    private EventListener eventListener;

    public String os;

    @Getter
    private UUID uuid;


    /**
     * The keypair encryption
     * Used in initial connection establishment
     */
    @Getter
    private KeyPair tempKeyPair;

    @Getter
    private SecretKey secretKey;

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
        this.tempKeyPair = null;
    }

//    /**
//     * Set the client
//     * @param uuid The uuid the client generated
//     * @param privateKey The private key from the client
//     */
//    public void setClientUUID(UUID uuid,String privateKey) {
//        this.clientUUID = uuid;
//
//        this.clientKey = EncryptionHandler.decrypt(privateKey,serverKey);
//        encryptCipher = registerEncryptCipher(clientKey);
//    }



    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Channel channel;

    private String deviceName;

    @Getter
    @Setter
    private int id = -1;

    @Setter
    @Getter
    private long delayTime = -1;

    @Getter
    private Cipher encryptCipher;

    @Getter
    private Cipher decryptCipher;

    public String getDeviceName() {
        return deviceName;
    }

    public boolean isConnected() {
        return connected;
    }

    public ClientPlayer(Server server,Channel channel, UUID uuid) {
        this.channel = channel;
        this.uuid = uuid;

        tempKeyPair = RSAEncryptionUtil.generateKeyPairs();

//        try {
//            encryptCipher = EncryptionUtil.generateEncryptCipher(tempKeyPair.getPublic());
//        } catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//
////        serverKey = EncryptionHandler.makeSHA256Hash(uuid.toString());
////        decryptCipher = registerDecryptCipher(serverKey);
//        try {
//            assert tempKeyPair != null;
//            decryptCipher = EncryptionUtil.generateDecryptCipher(tempKeyPair.getPrivate());
//        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
//            e.printStackTrace();
//        }



        eventListener = new EventListener(server, this);
    }

    @Deprecated
    public void setKeyPair(KeyPair keyPair) {
        throw new IllegalStateException("This is deprecated. Kept only in case.");
//        this.tempKeyPair = null;
//
//        try {
//            encryptCipher = EncryptionUtil.generateEncryptCipher(keyPair.getPublic());
//            decryptCipher = EncryptionUtil.generateDecryptCipher(keyPair.getPrivate());
//            Server.getLogger().info("Registered key set with client's key set.");
//        } catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
    }


    /**
     * PingPong delay
     */
    public long startTime;
    public long endTime;

    void setLastPacket(Packet packet) {
        if (packet instanceof Packet) {
        }
    }

    public void sendObject(@NonNull Packet packet, boolean encrypt) {
            /*
            // Length is 16 byte
            SecretKeySpec sks = new SecretKeySpec(serverKey.getBytes(), StaticHandler.getCipherTransformation());

            // Create cipher
            Cipher cipher = Cipher.getInstance(StaticHandler.getCipherTransformation());
            cipher.init(Cipher.ENCRYPT_MODE, sks);*/
        if (encrypt) {
//            SealedObject sealedObject = encryptObject(packet); ////////////////

            channel.writeAndFlush(packet);
        } else {
            channel.writeAndFlush(new UnencryptedPacketWrapper(packet));
        }


    }
//
//    public Cipher registerDecryptCipher(String key) {
//        try {
//            byte[] salt = new byte[16];
//
//            SecretKeyFactory factory = SecretKeyFactory.getInstance(StaticHandler.getKeyFactoryString());
//            KeySpec spec = new PBEKeySpec(key.toCharArray(), salt, 65536, 256);
//            SecretKey tmp = factory.generateSecret(spec);
//            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), StaticHandler.getKeySpecTransformation());
//            Cipher cipher = Cipher.getInstance(StaticHandler.getObjecrCipherTrans());
//
//            cipher.init(Cipher.DECRYPT_MODE, secret);
//            decryptCipher = cipher;
//            return cipher;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public Cipher registerEncryptCipher(String password) {
//        try {
//            byte[] salt = new byte[16];
//
//            SecretKeyFactory factory = SecretKeyFactory.getInstance(StaticHandler.getKeyFactoryString());
//            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
//            SecretKey tmp = factory.generateSecret(spec);
//            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), StaticHandler.getKeySpecTransformation());
//
//            Cipher cipher = Cipher.getInstance(StaticHandler.getObjecrCipherTrans());
//            cipher.init(Cipher.ENCRYPT_MODE, secret);
//            return cipher;
//        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public SealedObject encryptObject(Serializable object) {
        try {
            if (encryptCipher == null)
                throw new IllegalArgumentException("Register cipher with registerEncryptCipher() first");

            return new SealedObject(object, encryptCipher);
        } catch (IOException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object decryptObject(SealedObject sealedObject) {
        try {
            if (decryptCipher == null)
                throw new IllegalArgumentException("Register cipher with registerDecryptCipher() first");

            return sealedObject.getObject(decryptCipher);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void sendObject(Packet packet) {
        sendObject(packet,true);
    }

    public void close() {

        //DISCONNECT FROM SERVER
        Server.getLogger().info("Closing player {}", this::toString);

        if (channel != null) {

            channel.close();


            socketList.remove(channel);
        }

        connected = false;
        PlayerHandler.players.remove(getId());

        //serverSocket.close();
    }


    @Override
    public String toString() {


        return "[" + getAddress() + "] [" + deviceName + "|" + id +"]";
    }


    public String getAddress() {
        if (channel.remoteAddress() == null) {
            return "unknown";
        }

        InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();

        return address.getAddress().toString();
    }

    public void ping() {
        startTime = System.nanoTime();
        sendObject(new PingPacket(),false);
    }

    public static void pingAll() {
        for(ClientPlayer clientPlayer : socketList.values()) {
            clientPlayer.ping();
        }
    }

    @Override
    public void sendPacket(Packet packet) {
        sendObject(packet);
    }

    @Override
    public void sendMessage(String message) {
        sendPacket(new MessagePacket(message));
    }

    @Override
    public String getName() {
        return deviceName;
    }
}
