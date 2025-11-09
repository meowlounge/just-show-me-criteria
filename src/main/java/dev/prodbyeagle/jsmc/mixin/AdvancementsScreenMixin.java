package dev.prodbyeagle.jsmc.mixin;

import dev.prodbyeagle.jsmc.client.ShowMeCriteriaSelector;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementsScreen.class)
public abstract class AdvancementsScreenMixin {

    @Inject(method = "mouseClicked", at = @At("TAIL"), cancellable = true)
    private void jsmc$handleRightClick(Click click, boolean area, CallbackInfoReturnable<Boolean> cir) {
        if (click.button() != 1) {
            return;
        }

        if (cir.getReturnValueZ()) {
            return;
        }

        if (ShowMeCriteriaSelector.INSTANCE.handleRightClick((AdvancementsScreen) (Object) this, click)) {
            cir.setReturnValue(true);
        }
    }
}
