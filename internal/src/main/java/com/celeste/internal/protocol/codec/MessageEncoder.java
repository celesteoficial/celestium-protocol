package com.celeste.internal.protocol.codec;

import com.celeste.internal.controllers.ChannelController;
import com.celeste.internal.exceptions.PacketException;
import com.celeste.internal.packets.AbstractPacket;
import com.celeste.internal.packets.PacketContent;
import com.celeste.internal.protocol.utils.ProtocolBuffer;
import com.celeste.internal.registry.Protocol;
import io.grpc.netty.shaded.io.netty.buffer.ByteBuf;
import io.grpc.netty.shaded.io.netty.channel.ChannelHandlerContext;
import io.grpc.netty.shaded.io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class MessageEncoder extends MessageToByteEncoder<PacketContent> {

  private final ChannelController controller;

  @Override @SuppressWarnings("unchecked")
  protected void encode(ChannelHandlerContext channelHandlerContext, PacketContent packetContent, ByteBuf byteBuf) {
    final AbstractPacket packet = Protocol.INSTANCE.getPacketOutbound(controller.getState(), packetContent.getClass());
    if (packet == null) {
      throw new PacketException("A packet with unidentified id has been tried to sent. Name: " + packetContent.getClass().getSimpleName());
    }

    final ProtocolBuffer buffer = new ProtocolBuffer(byteBuf);

    buffer.writeVarInt(packet.getOutboundId());
    packet.write(buffer, packetContent);
  }

}
