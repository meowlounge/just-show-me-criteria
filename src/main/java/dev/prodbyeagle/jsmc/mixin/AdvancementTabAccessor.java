package dev.prodbyeagle.jsmc.mixin;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Collection;
import java.util.Map;

@Mixin(AdvancementTab.class)
public interface AdvancementTabAccessor {

	@Accessor("originX")
	double jsmc$getOriginX();

	@Accessor("originY")
	double jsmc$getOriginY();

	@Accessor("widgets")
	Map<AdvancementEntry, AdvancementWidget> jsmc$getWidgetMap();

	default Collection<AdvancementWidget> jsmc$getWidgets() {
		return this.jsmc$getWidgetMap().values();
	}
}
