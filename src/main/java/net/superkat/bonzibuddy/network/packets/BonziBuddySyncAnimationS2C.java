package net.superkat.bonzibuddy.network.packets;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;

public record BonziBuddySyncAnimationS2C(int bonziBuddyId, int bonziAnimationNumber) implements CustomPayload {
    public static final CustomPayload.Id<BonziBuddySyncAnimationS2C> ID = new Id<>(Identifier.of(BonziBUDDY.MOD_ID, "bonzi_buddy_sync_anim_s2c"));
    public static final PacketCodec<RegistryByteBuf, BonziBuddySyncAnimationS2C> CODEC = CustomPayload.codecOf(BonziBuddySyncAnimationS2C::write, BonziBuddySyncAnimationS2C::new);

    public BonziBuddySyncAnimationS2C(RegistryByteBuf buf) {
        this(buf.readInt(), buf.readInt());
    }

    public void write(RegistryByteBuf buf) {
        buf.writeInt(this.bonziBuddyId);
        buf.writeInt(this.bonziAnimationNumber);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
