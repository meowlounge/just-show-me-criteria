package dev.prodbyeagle.pinnedAdvancements.mixin;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AdvancementTab.class)
public interface AdvancementTabAccessor {

    @Accessor("widgets")
    Map<AdvancementEntry, AdvancementWidget> pinnedAdvancements_getWidgets();

    @Accessor("originX")
    double pinnedAdvancements_getOriginX();

    @Accessor("originY")
    double pinnedAdvancements_getOriginY();
}
