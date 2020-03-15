﻿using com.github.fernthedev.lightchat.core.packets;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Text;

namespace com.github.fernthedev.lightchat.core.encryption
{

    public interface IAcceptablePacketTypes
    {

    }

    public class PacketWrapper<T>
    {
        protected bool ENCRYPT = false;

        public bool isEncrypted => ENCRYPT;

        private string jsonObject;
        private int packetId;

        public string getJsonObject => jsonObject;
        public int getPacketId => packetId;

        [NonSerialized]
        private T _jsonObjectInstance;

        public T getJsonObjectInstance => _jsonObjectInstance;

        public string packetIdentifier { get; }

        public PacketWrapper(T jsonObject, string packetIdentifier, int packetId)
        {
            this.packetIdentifier = packetIdentifier;
            this._jsonObjectInstance = jsonObject;
            this.jsonObject = StaticHandler.defaultJsonHandler.toJson(jsonObject);
            this.packetId = packetId;
            
        }
    }

    public class EncryptedPacketWrapper : PacketWrapper<EncryptedBytes>
    {
        public EncryptedPacketWrapper(EncryptedBytes jsonObject, Packet packet, int packetId) : base(jsonObject, packet?.PacketName, packetId)
        {
            ENCRYPT = true;
            if (PacketRegistry.checkIfRegistered(packet) == PacketRegistry.RegisteredReturnValues.NOT_IN_REGISTRY)
            {
                throw new InvalidOperationException("The packet trying to be wrapped is not registered. \"" + packet.GetType().FullName + "\"");
            }
        }
    }

    public class UnencryptedPacketWrapper : PacketWrapper<Packet>, IAcceptablePacketTypes
    {
        public UnencryptedPacketWrapper(Packet packet, int packetId) : base(packet, packet?.PacketName, packetId)
        {
            ENCRYPT = false;

            if (PacketRegistry.checkIfRegistered(packet) == PacketRegistry.RegisteredReturnValues.NOT_IN_REGISTRY)
            {
                throw new InvalidOperationException("The packet trying to be wrapped is not registered. \"" + packet.GetType().FullName + "\"");
            }

        }
    }
}