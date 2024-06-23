package net.superkat.bonzibuddy.network.packets;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;

public record BonziAirplaneC2S(int bonziBuddyId) implements CustomPayload {
    public static final CustomPayload.Id<BonziAirplaneC2S> ID = new CustomPayload.Id<>(Identifier.of(BonziBUDDY.MOD_ID, "bonzi_airplane_c2s"));
    public static final PacketCodec<RegistryByteBuf, BonziAirplaneC2S> CODEC = CustomPayload.codecOf(BonziAirplaneC2S::write, BonziAirplaneC2S::new);

    public BonziAirplaneC2S(RegistryByteBuf buf) {
        this(buf.readInt());
    }

    public void write(RegistryByteBuf buf) {
        buf.writeInt(this.bonziBuddyId);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
