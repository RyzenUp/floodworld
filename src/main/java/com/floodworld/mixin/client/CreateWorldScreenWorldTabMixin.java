package com.floodworld.mixin.client;

import com.floodworld.client.ClothConfigScreenFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$WorldTab")
public abstract class CreateWorldScreenWorldTabMixin {

    @Inject(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/worldselection/SwitchGrid;builder(I)Lnet/minecraft/client/gui/screens/worldselection/SwitchGrid$Builder;"
        ),
        locals = LocalCapture.CAPTURE_FAILHARD,
        require = 0
    )
    private void floodworld$addConfigButton(CreateWorldScreen outer, CallbackInfo ci, GridLayout.RowHelper rowHelper) {
        rowHelper.addChild(
            Button.builder(Component.literal("FloodWorld Settings"), button -> {
                Minecraft minecraft = Minecraft.getInstance();
                minecraft.setScreen(ClothConfigScreenFactory.build(minecraft.screen));
            }).width(310).build(),
            2
        );
    }
}
