package net.superkat.bonzibuddy.network.packets.minigame;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;

public record RequestReturnToRespawnC2S() implements CustomPayload {
    public static final CustomPayload.Id<RequestReturnToRespawnC2S> ID = new Id<>(Identifier.of(BonziBUDDY.MOD_ID, "request_return_to_respawn_c2s"));
    public static final PacketCodec<RegistryByteBuf, RequestReturnToRespawnC2S> CODEC = CustomPayload.codecOf(RequestReturnToRespawnC2S::write, RequestReturnToRespawnC2S::new);

    public RequestReturnToRespawnC2S(RegistryByteBuf buf) {
        this();
    }

    public void write(RegistryByteBuf buf) {
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
