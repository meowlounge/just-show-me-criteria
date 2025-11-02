package dev.prodbyeagle.pinnedAdvancements.mixin;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AdvancementsScreen.class)
public interface AdvancementsScreenAccessor {

    @Accessor("selectedTab")
    AdvancementTab pinnedAdvancements_getSelectedTab();

    @Accessor("tabs")
    Map<AdvancementEntry, AdvancementTab> pinnedAdvancements_getTabs();
}
