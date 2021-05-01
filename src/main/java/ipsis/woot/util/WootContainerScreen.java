package ipsis.woot.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class WootContainerScreen<T extends Container> extends ContainerScreen<T> {

    public WootContainerScreen(T container, PlayerInventory playerInventory, ITextComponent name) {
        super(container, playerInventory, name);
    }

    /**
     * x1, y1 is the bottom right of the energy bar
     */
    public void renderEnergyBar(MatrixStack matrixStack, int x1, int y1, int height, int width, int curr, int max) {
        int filled = 0;
        if (max > 0)
            filled = curr * 100 / max;
        filled = MathHelper.clamp(filled, 0, 100);
        int h = filled * height / 100;
        fill(matrixStack,
                leftPos + x1,
             topPos + y1 - h + 1,
             leftPos + x1 + width,
             topPos + y1 + 1, 0xffff0000);
    }

    /**
     * x1, y1 is the bottom right of the fluid tank
     */
    public void renderFluidTank(int x1, int y1, int height, int width, int curr, int max, FluidStack fluidStack)  {
        int filled = 0;
        if (max > 0)
            filled = curr * 100 / max;
        filled = MathHelper.clamp(filled, 0, 100);
        int h = filled * height / 100;
        drawFluid(leftPos + x1, topPos + y1 - h + 1, fluidStack, width,  h);
    }

    public void renderFluidTank(MatrixStack matrixStack, int x1, int y1, int height, int width, int max, FluidStack fluidStack)  {
        int filled = 0;
        if (max > 0)
            filled = fluidStack.getAmount() * 100 / max;
        filled = MathHelper.clamp(filled, 0, 100);
        int h = filled * height / 100;
        drawFluid(leftPos + x1, topPos + y1 - h + 1, fluidStack, width,  h);
    }

    public void renderHorizontalBar(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int curr, int max, int color) {
        int filled = 0;
        if (max > 0)
            filled = curr * max / 100;
        filled = MathHelper.clamp(filled, 0, 100);
        int l = filled * (x2 - x1) / 100;
        fill(matrixStack, leftPos + x1, topPos + y2,
                leftPos + x2 + l, topPos + y2, color);
    }

    public void renderHorizontalGauge(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int curr, int max, int color) {
        fill(matrixStack, leftPos + x1, topPos + y1, leftPos + x2, topPos + y2, color);

        if (max > 0) {
            int p = curr * (x2 - x1) / max;
            for (int i = 0; i < p; i++)
                vLine(
                        matrixStack,
                        leftPos + x1 + 1 + i,
                        topPos + y1,
                        topPos + y2 - 1,
                        i % 2 == 0 ? color : 0xff000000);
        }
    }

    public void renderFluidTankTooltip(MatrixStack matrixStack, int mouseX, int mouseY, FluidStack fluidStack, int capacity) {
        List<ITextComponent> tooltip = new ArrayList<>();
        if (!fluidStack.isEmpty()) {
            tooltip.add(fluidStack.getDisplayName());
            tooltip.add(new StringTextComponent(String.format("%d/%d mb", fluidStack.getAmount(), capacity)));
        } else {
            tooltip.add(new StringTextComponent(String.format("0/%d mb", capacity)));
        }
        renderComponentTooltip(matrixStack, tooltip, mouseX, mouseY);
    }

    public void renderEnergyTooltip(MatrixStack matrixStack, int mouseX, int mouseY, int curr, int capacity, int rate) {
        List<ITextComponent> tooltip = Arrays.asList(
                new StringTextComponent(String.format("%d/%d RF", curr, capacity)),
                new StringTextComponent( String.format("%d RF/tick", rate)));
        renderComponentTooltip(matrixStack, tooltip, mouseX, mouseY);
    }

    public void drawFluid(int x, int y, FluidStack fluid, int width, int height) {

        if (fluid == null)
            return;

        Minecraft.getInstance().getTextureManager().bind(AtlasTexture.LOCATION_BLOCKS);
        int color = fluid.getFluid().getAttributes().getColor(fluid);
        setGLColorFromInt(color);
        ResourceLocation resourceLocation = fluid.getFluid().getAttributes().getStillTexture();
        TextureAtlasSprite textureAtlasSprite = Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(resourceLocation);
        drawTiledTexture(x, y, textureAtlasSprite, width, height);
    }

    private void setGLColorFromInt(int color) {
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;
        float alpha = (float)(color >> 24 & 255) / 255.0F;
        GlStateManager._color4f(red, green, blue, alpha);
    }

    private void drawTiledTexture(int x, int y, TextureAtlasSprite icon, int width, int height) {
        int i;
        int j;
        int drawHeight;
        int drawWidth;

        for (i = 0; i < width; i += 16) {
            for (j = 0; j < height; j += 16) {
                drawWidth = Math.min(width - i, 16);
                drawHeight = Math.min(height - j, 16);
                drawScaledTexturedModelRectFromIcon(x + i, y + j, icon, drawWidth, drawHeight);
            }
        }
        GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void drawScaledTexturedModelRectFromIcon(int x, int y, TextureAtlasSprite icon, int width, int height) {

        if (icon == null) {
            return;
        }
        float minU = icon.getU0();
        float maxU = icon.getU1();
        float minV = icon.getV0();
        float maxV = icon.getV1();

        BufferBuilder buffer = Tessellator.getInstance().getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.vertex(x, y + height, this.itemRenderer.blitOffset).uv(minU, minV + (maxV - minV) * height / 16F).endVertex();
        buffer.vertex(x + width, y + height, this.itemRenderer.blitOffset).uv(minU + (maxU - minU) * width / 16F, minV + (maxV - minV) * height / 16F).endVertex();
        buffer.vertex(x + width, y, this.itemRenderer.blitOffset).uv(minU + (maxU - minU) * width / 16F, minV).endVertex();
        buffer.vertex(x, y, this.itemRenderer.blitOffset).uv(minU, minV).endVertex();
        Tessellator.getInstance().end();
    }
}
