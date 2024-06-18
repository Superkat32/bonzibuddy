package net.superkat.bonzibuddy.network.packets.minigame;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;

import java.util.UUID;

public record BonziBossBarUpdateS2C(UUID hudUuid, float percent, BonziBoss bonziBoss) implements CustomPayload {
    public static final CustomPayload.Id<BonziBossBarUpdateS2C> ID = new Id<>(Identifier.of(BonziBUDDY.MOD_ID, "bonzi_boss_bar_update_s2c"));
    public static final PacketCodec<RegistryByteBuf, BonziBossBarUpdateS2C> CODEC = CustomPayload.codecOf(BonziBossBarUpdateS2C::write, BonziBossBarUpdateS2C::new);

    public BonziBossBarUpdateS2C(RegistryByteBuf buf) {
        this(buf.readUuid(), buf.readFloat(), buf.readEnumConstant(BonziBoss.class));
    }

    public void write(RegistryByteBuf buf) {
        buf.writeUuid(hudUuid);
        buf.writeFloat(percent);
        buf.writeEnumConstant(bonziBoss);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static enum BonziBoss {
        RED,
        GREEN,
        BLUE
    }
}
