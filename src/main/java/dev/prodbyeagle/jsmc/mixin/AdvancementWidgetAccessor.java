package dev.prodbyeagle.jsmc.mixin;

import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AdvancementWidget.class)
public interface AdvancementWidgetAccessor {
	@Accessor("x") int jsmc$getX();
	@Accessor("y") int jsmc$getY();

	@Accessor("advancement")
	PlacedAdvancement jsmc$getAdvancement();
}
