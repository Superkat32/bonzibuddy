package net.superkat.bonzibuddy.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;
import net.superkat.bonzibuddy.BonziBUDDY;

import java.util.Optional;

public class ConstantSpreadStructurePlacement extends StructurePlacement {
    public static final MapCodec<ConstantSpreadStructurePlacement> CODEC = RecordCodecBuilder.<ConstantSpreadStructurePlacement>mapCodec(
            instance ->buildCodec(instance)
                    .<Integer, Integer>and(
                            instance.group(
                                    Codec.intRange(0, 4096).fieldOf("spacing_x").forGetter(ConstantSpreadStructurePlacement::getSpacingX),
                                    Codec.intRange(0, 4096).fieldOf("spacing_z").forGetter(ConstantSpreadStructurePlacement::getSpacingZ)
                            )
                    )
                    .apply(instance, ConstantSpreadStructurePlacement::new)
    );

    private final int spacingX;
    private final int spacingZ;
    public ConstantSpreadStructurePlacement(
            Vec3i locateOffset,
            StructurePlacement.FrequencyReductionMethod frequencyReductionMethod,
            float frequency, int salt, Optional<ExclusionZone> exclusionZone,
            int spacingX, int spacingZ) {
        super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone);
        this.spacingX = spacingX;
        this.spacingZ = spacingZ;
    }
    public ConstantSpreadStructurePlacement(int spacingX, int spacingZ, int salt) {
        this(Vec3i.ZERO, FrequencyReductionMethod.DEFAULT, 1.0f, salt, Optional.empty(), spacingX, spacingZ);
    }

    @Override
    protected boolean isStartChunk(StructurePlacementCalculator calculator, int chunkX, int chunkZ) {
        return chunkX % spacingX == 0 && chunkZ % spacingZ == 0;
    }

    @Override
    public StructurePlacementType<?> getType() {
        return BonziBUDDY.CONSTANT_SPREAD_PLACEMENT_TYPE;
    }

    public int getSpacingX() {
        return this.spacingX;
    }

    public int getSpacingZ() {
        return spacingZ;
    }

}
