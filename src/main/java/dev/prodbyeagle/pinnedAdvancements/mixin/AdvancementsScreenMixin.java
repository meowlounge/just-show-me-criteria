package dev.prodbyeagle.pinnedAdvancements.mixin;

import dev.prodbyeagle.pinnedAdvancements.client.PinnedAdvancementSelector;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementsScreen.class)
public abstract class AdvancementsScreenMixin {

    @Inject(
        method = "mouseClicked",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/Screen;mouseClicked(Lnet/minecraft/client/gui/Click;Z)Z",
            shift = At.Shift.BEFORE //Shifting like this is discouraged because it's brittle
        ),
        cancellable = true
    )
    private void pinnedAdvancements$handleRightClick(Click click, boolean area, CallbackInfoReturnable<Boolean> cir) {
        if (click.button() != 1) {
            return;
        }

        if (PinnedAdvancementSelector.INSTANCE.handleRightClick((AdvancementsScreen) (Object) this, click)) { //Non-static method 'handleRightClick(net.minecraft.client.gui.screen.advancement.@org.jetbrains.annotations.NotNull AdvancementsScreen, net.minecraft.client.gui.@org.jetbrains.annotations.NotNull Click)' cannot be referenced from a static context
            cir.setReturnValue(true);
        }
    }
}
