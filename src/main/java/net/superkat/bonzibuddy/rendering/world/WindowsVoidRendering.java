package net.superkat.bonzibuddy.rendering.world;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import net.superkat.bonzibuddy.BonziBUDDY;
import org.joml.Matrix4f;

public class WindowsVoidRendering {
    public static Identifier ERRORBOX1 = Identifier.of(BonziBUDDY.MOD_ID, "textures/environment/errorskybox1.png");
    public static Identifier ERRORBOX2 = Identifier.of(BonziBUDDY.MOD_ID, "textures/environment/errorskybox2.png");
    public static void renderErrorBoxes(Matrix4f matrix4f) {
        //Error boxes are hard coded to ensure syncing between clients - at least for now
        renderErrorBox(matrix4f,7f, 0, 90f, true);
        renderErrorBox(matrix4f,7f, -20f, 90f, false);

        for (int i = 0; i < 7; i++) {
            renderErrorBox(matrix4f, 4f, -40f - i * 2, 80f + i * 3, true);
        }
    }

    private static void renderErrorBox(Matrix4f matrix4f, float size, float yaw, float pitch, boolean styleOne) {
        //Error texture is 128 x 64 pixels, so a 2:1 scale
        float width = size * 2f;
        float height = size;
        renderSkyObject(matrix4f, width, height, yaw, pitch, false, styleOne ? ERRORBOX1 : ERRORBOX2);
    }

    public static void renderSkyObject(Matrix4f rotationMatrix, float width, float height, float yaw, float pitch, boolean adjustWithSky, Identifier texture) {
        //Prep for rendering on the sky
        World world = MinecraftClient.getInstance().world;
        Tessellator tessellator = Tessellator.getInstance();
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.multiplyPositionMatrix(rotationMatrix);

        //Rotate to sky
        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));
        if(adjustWithSky) { //this might work? I don't think we'll need this at all though
            float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false);
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(tickDelta) * 360f));
        }
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pitch));

        //Set colors and texture
        Matrix4f posMatrix = matrixStack.peek().getPositionMatrix();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, texture);

        //Actually render the texture
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(posMatrix, -width, 100.0F, -height).texture(1.0f, 0.0f);
        bufferBuilder.vertex(posMatrix, width, 100.0F, -height).texture(0.0f, 0.0f);
        bufferBuilder.vertex(posMatrix, width, 100.0F, height).texture(0.0f, 1.0f);
        bufferBuilder.vertex(posMatrix, -width, 100.0F, height).texture(1.0f, 1.0f);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        //Reset adjusted settings
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.pop();
    }

}
