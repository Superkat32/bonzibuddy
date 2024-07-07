package net.superkat.bonzibuddy.network.packets;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;

public record OpenBonziBuddyScreenS2C(int bonziBuddyId, boolean tripleChaosEnabled) implements CustomPayload {
    public static final CustomPayload.Id<OpenBonziBuddyScreenS2C> ID = new Id<>(Identifier.of(BonziBUDDY.MOD_ID, "open_bonzi_buddy_screen_s2c"));
    public static final PacketCodec<RegistryByteBuf, OpenBonziBuddyScreenS2C> CODEC = CustomPayload.codecOf(OpenBonziBuddyScreenS2C::write, OpenBonziBuddyScreenS2C::new);

    public OpenBonziBuddyScreenS2C(RegistryByteBuf buf) {
        this(buf.readInt(), buf.readBoolean());
    }

    public void write(RegistryByteBuf buf) {
        buf.writeInt(this.bonziBuddyId);
        buf.writeBoolean(this.tripleChaosEnabled);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
