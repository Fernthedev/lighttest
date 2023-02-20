package com.github.fernthedev.lightchat.core.encryption

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.buffer.Unpooled

data class EncryptedBytes(val data: ByteArray, val params: ByteArray, val paramAlgorithm: String) {

    companion object {
        fun decode(buf: ByteBuf): EncryptedBytes {
            val dataSize = buf.readInt()
            val data = buf.readBytes(dataSize)

            val paramsSize = buf.readInt()
            val params = buf.readBytes(paramsSize)

            val paramsAlgorithmSize = buf.readInt()
            val paramsAlgorithm = buf.readBytes(paramsAlgorithmSize).toString(Charsets.UTF_8)

            return EncryptedBytes(
                data = ByteBufUtil.getBytes(data),
                params = ByteBufUtil.getBytes(params),
                paramAlgorithm = paramsAlgorithm
            )
        }
    }

    fun encode(): ByteBuf {
        val algorithm = paramAlgorithm.toByteArray()
        val byteBuf = Unpooled.buffer(4 + data.size + 4 + params.size + 4 + algorithm.size)
        byteBuf.writeInt(data.size)
        byteBuf.writeBytes(data)

        byteBuf.writeInt(params.size)
        byteBuf.writeBytes(params)

        byteBuf.writeInt(algorithm.size)
        byteBuf.writeBytes(algorithm)

        return byteBuf
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EncryptedBytes) return false

        if (!data.contentEquals(other.data)) return false
        if (!params.contentEquals(other.params)) return false
        if (paramAlgorithm != other.paramAlgorithm) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + params.contentHashCode()
        result = 31 * result + paramAlgorithm.hashCode()
        return result
    }
}