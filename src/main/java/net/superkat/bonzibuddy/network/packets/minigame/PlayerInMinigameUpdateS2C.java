package net.superkat.bonzibuddy.network.packets.minigame;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;

public record PlayerInMinigameUpdateS2C(boolean inMinigame) implements CustomPayload {
    public static final CustomPayload.Id<PlayerInMinigameUpdateS2C> ID = new Id<>(Identifier.of(BonziBUDDY.MOD_ID, "player_in_minigame_s2c"));
    public static final PacketCodec<RegistryByteBuf, PlayerInMinigameUpdateS2C> CODEC = CustomPayload.codecOf(PlayerInMinigameUpdateS2C::write, PlayerInMinigameUpdateS2C::new);

    public PlayerInMinigameUpdateS2C(RegistryByteBuf buf) {
        this(buf.readBoolean());
    }

    public void write(RegistryByteBuf buf) {
        buf.writeBoolean(this.inMinigame);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
