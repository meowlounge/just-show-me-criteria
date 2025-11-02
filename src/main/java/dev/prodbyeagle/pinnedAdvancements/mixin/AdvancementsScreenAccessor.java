package dev.prodbyeagle.pinnedAdvancements.mixin;

import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AdvancementsScreen.class)
public interface AdvancementsScreenAccessor {

    @Accessor("selectedTab")
    AdvancementTab pinnedAdvancements_getSelectedTab();
}
