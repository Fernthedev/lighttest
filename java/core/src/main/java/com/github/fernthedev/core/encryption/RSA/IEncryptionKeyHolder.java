package com.github.fernthedev.core.encryption.RSA;

import com.github.fernthedev.core.packets.Packet;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.tuple.Pair;

import javax.crypto.SecretKey;

public interface IEncryptionKeyHolder {

//    @NonNull
//    PrivateKey getPrivateKey(ChannelHandlerContext ctx, Channel channel);
//
//    @NonNull PublicKey getPublicKey(ChannelHandlerContext ctx, Channel channel);

    SecretKey getSecretKey(ChannelHandlerContext ctx, Channel channel);

    boolean isEncryptionKeyRegistered(ChannelHandlerContext ctx, Channel channel);

    Pair<Integer, Long> getPacketId(Class<? extends Packet> clazz, ChannelHandlerContext ctx, Channel channel);
}
