package com.sollyu.minecraft;

import com.google.gson.annotations.Expose;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.ExposableOptions;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.io.File;

@ExposableOptions(strategy = ConfigStrategy.Versioned, filename = "AlwaysAttack.json")
public class LiteModAlwaysAttack implements Tickable, Configurable {

    private static final KeyBinding swapKeyBinding   = new KeyBinding("key.always.attack.switch", Keyboard.KEY_F9, "key.categories.litemods");

    @Expose
    private boolean enabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void onTick(final Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        try {
            if (!inGame || minecraft.currentScreen != null || !Minecraft.isGuiEnabled()) {
                return;
            }

            minecraft.player.sendChatMessage("" + System.currentTimeMillis());
            if (LiteModAlwaysAttack.swapKeyBinding.isPressed()) {
                // LiteLoader.getInstance().writeConfig(this);
                //minecraft.player.jump();
                //minecraft.player.sendChatMessage("asdfasdf");

                enabled = !enabled;

                KeyBinding.onTick(minecraft.gameSettings.keyBindAttack.getKeyCode());

//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        minecraft.player.sendChatMessage("start");
//                        while (isEnabled()) {
//                            try {
//                                if (minecraft.currentScreen == null) {
//                                    minecraft.player.sendChatMessage("doing");
//
//
//                                    minecraft.player.doesEntityNotTriggerPressurePlate()
//                                    // minecraft.playerController.attackEntity(minecraft.player, minecraft.objectMouseOver.entityHit);
//                                    Thread.sleep(500);
//                                }
//                            } catch (InterruptedException e) {
//                                LiteLoaderLogger.severe(e, e.getMessage());
//                            }
//                        }
//                        minecraft.player.sendChatMessage("stop");
//                    }
//                }).start();
            }
        } catch (Throwable e) {
            LiteLoaderLogger.severe(e, e.getMessage());
        }
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public void init(File configPath) {
        LiteLoader.getInput().registerKeyBinding(swapKeyBinding);
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {

    }

    @Override
    public String getName() {
        return "AlwaysAttack";
    }


    @Override
    public Class<? extends ConfigPanel> getConfigPanelClass() {
        return LiteModAlwaysAttackConfigPanel.class;
    }
}
