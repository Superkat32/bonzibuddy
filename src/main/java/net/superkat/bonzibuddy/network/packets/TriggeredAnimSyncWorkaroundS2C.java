package net.superkat.bonzibuddy.network.packets;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;

public record TriggeredAnimSyncWorkaroundS2C(int entityid, String controller, String anim) implements CustomPayload {
    public static final CustomPayload.Id<TriggeredAnimSyncWorkaroundS2C> ID = new Id<>(Identifier.of(BonziBUDDY.MOD_ID, "bonzi_buddy_triggered_anim_sync_s2c"));
    public static final PacketCodec<RegistryByteBuf, TriggeredAnimSyncWorkaroundS2C> CODEC = CustomPayload.codecOf(TriggeredAnimSyncWorkaroundS2C::write, TriggeredAnimSyncWorkaroundS2C::new);

    public TriggeredAnimSyncWorkaroundS2C(RegistryByteBuf buf) {
        this(buf.readInt(), buf.readString(), buf.readString());
    }

    public void write(RegistryByteBuf buf) {
        buf.writeInt(this.entityid);
        buf.writeString(this.controller);
        buf.writeString(this.anim);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
