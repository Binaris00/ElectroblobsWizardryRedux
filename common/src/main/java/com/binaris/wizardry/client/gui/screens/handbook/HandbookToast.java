package com.binaris.wizardry.client.gui.screens.handbook;

import com.binaris.wizardry.api.client.util.DrawingUtils;
import com.binaris.wizardry.setup.registries.EBItems;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HandbookToast implements Toast {
    private final Section section;

    public HandbookToast(Section section) {
        this.section = section;
    }

    @Override
    public @NotNull Visibility render(GuiGraphics guiGraphics, ToastComponent toastComponent, long delta) {
        guiGraphics.blit(TEXTURE, 0, 0, 0, 32, 160, 32);
        Font font = toastComponent.getMinecraft().font;

        boolean firstPart = delta < 1500L;

        int a = firstPart ? Mth.floor(Mth.clamp((float) (1500L - delta) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864
                : Mth.floor(Mth.clamp((float) (delta - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;

        Component title = firstPart ? Component.translatable("handbook.toast.title") : Component.literal(section.title);

        int c = firstPart ? -11534256 : -16777216;
        List<String> list = DrawingUtils.listFormattedStringToWidth(font, title.getString(), 125);
        int lineHeight = 16 - list.size() * font.lineHeight / 2;

        for (String line : list) {
            guiGraphics.drawString(font, line, 30, lineHeight, c | a, false);
            lineHeight += font.lineHeight;
        }

        guiGraphics.pose().pushPose();
        guiGraphics.renderFakeItem(new ItemStack(EBItems.WIZARD_HANDBOOK.get()), 8, 8);
        guiGraphics.pose().popPose();

        return delta >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }
}
