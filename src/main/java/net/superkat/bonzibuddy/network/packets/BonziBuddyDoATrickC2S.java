package net.superkat.bonzibuddy.network.packets;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;

public record BonziBuddyDoATrickC2S(int bonziBuddyId) implements CustomPayload {
    public static final CustomPayload.Id<BonziBuddyDoATrickC2S> ID = new Id<>(Identifier.of(BonziBUDDY.MOD_ID, "bonzi_buddy_do_a_trick_c2s"));
    public static final PacketCodec<RegistryByteBuf, BonziBuddyDoATrickC2S> CODEC = CustomPayload.codecOf(BonziBuddyDoATrickC2S::write, BonziBuddyDoATrickC2S::new);

    public BonziBuddyDoATrickC2S(RegistryByteBuf buf) {
        this(buf.readInt());
    }

    public void write(RegistryByteBuf buf) {
        buf.writeInt(this.bonziBuddyId);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
