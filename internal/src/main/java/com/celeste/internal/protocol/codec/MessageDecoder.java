package com.celeste.internal.protocol.codec;

import com.celeste.internal.controller.ChannelController;
import com.celeste.internal.exception.PacketException;
import com.celeste.internal.packets.Packet;
import com.celeste.internal.packets.PacketContent;
import com.celeste.internal.protocol.util.ProtocolBuffer;
import com.celeste.internal.util.Logging;
import io.grpc.netty.shaded.io.netty.buffer.ByteBuf;
import io.grpc.netty.shaded.io.netty.channel.ChannelHandlerContext;
import io.grpc.netty.shaded.io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import lombok.AllArgsConstructor;

/**
 * <p>Packet decoder for the main Netty pipeline on the bootstrap.
 *
 * <p>It receives the packets from the server and
 * sends a formatted {@link Packet} to the
 * {@link ChannelController}
 */
@AllArgsConstructor
public final class MessageDecoder extends ByteToMessageDecoder {

  private final ChannelController controller;

  protected void decode(final ChannelHandlerContext context, final ByteBuf bytebuf, final List<Object> list) {
    final ProtocolBuffer buffer = new ProtocolBuffer(bytebuf);
    int packetId = buffer.readVarInt();

    final Packet<?> packet = controller.getProtocol().getPacket(controller.getState(), packetId);
    if (packet == null) throw new PacketException("A packet with unidentified id has been received: " + packetId);

    final PacketContent content = packet.read(buffer);
    list.add(content);
  }

  @Override
  public void exceptionCaught(final ChannelHandlerContext context, final Throwable cause) {
    Logging.LOGGER.atWarning().log("A exception was caught at the MessageDecoder");
    cause.printStackTrace();
  }

}
