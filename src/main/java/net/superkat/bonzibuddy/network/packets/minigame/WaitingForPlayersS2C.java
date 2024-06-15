package net.superkat.bonzibuddy.network.packets.minigame;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;

public record WaitingForPlayersS2C() implements CustomPayload {
    public static final CustomPayload.Id<WaitingForPlayersS2C> ID = new Id<>(Identifier.of(BonziBUDDY.MOD_ID, "waiting_for_players_s2c"));
    public static final PacketCodec<RegistryByteBuf, WaitingForPlayersS2C> CODEC = CustomPayload.codecOf(WaitingForPlayersS2C::write, WaitingForPlayersS2C::new);

    public WaitingForPlayersS2C(RegistryByteBuf buf) {
        this();
    }

    public void write(RegistryByteBuf buf) {
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
