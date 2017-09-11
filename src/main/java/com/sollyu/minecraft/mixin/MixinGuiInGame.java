package com.sollyu.minecraft.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GuiIngame.class)
public class MixinGuiInGame extends Gui {

}
