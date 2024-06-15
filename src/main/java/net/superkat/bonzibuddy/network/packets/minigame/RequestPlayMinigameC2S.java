package net.superkat.bonzibuddy.network.packets.minigame;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;

public record RequestPlayMinigameC2S(BonziMinigameType minigameType) implements CustomPayload {
    public static final CustomPayload.Id<RequestPlayMinigameC2S> ID = new Id<>(Identifier.of(BonziBUDDY.MOD_ID, "request_minigame_c2s"));
    public static final PacketCodec<RegistryByteBuf, RequestPlayMinigameC2S> CODEC = CustomPayload.codecOf(RequestPlayMinigameC2S::write, RequestPlayMinigameC2S::new);

    public RequestPlayMinigameC2S(RegistryByteBuf buf) {
        this(buf.readEnumConstant(BonziMinigameType.class));
    }

    public void write(RegistryByteBuf buf) {
        buf.writeEnumConstant(this.minigameType);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
