package dev.prodbyeagle.pinnedAdvancements.mixin;

import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AdvancementWidget.class)
public interface AdvancementWidgetAccessor {

    @Accessor("advancement")
    PlacedAdvancement pinnedAdvancements_getAdvancement();
}
