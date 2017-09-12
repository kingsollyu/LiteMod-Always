package com.sollyu.minecraft;

import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.io.File;

public class LiteModAlways implements Tickable, Configurable {

    private static final KeyBinding swapKeyBinding = new KeyBinding("key.always.attack.toggle", Keyboard.KEY_F9, "key.categories.litemods");

    // 当前开关状态
    private boolean attackEnabled = false;

    // 上一次执行操作的时间
    private Long attackLastExecuteTime = -1L;

    // get
    public boolean isAttackEnabled() {
        return attackEnabled;
    }

    @Override
    public void init(File configPath) {
        LiteLoader.getInput().registerKeyBinding(swapKeyBinding);
    }

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        try {
            if (!inGame || minecraft.currentScreen != null || !Minecraft.isGuiEnabled()) {
                return;
            }

            // 控制键被按下
            if (LiteModAlways.swapKeyBinding.isPressed()) {
                attackEnabled = !attackEnabled;
            }

            // 使用时间差计算是否执行操作
            // 尽量减少计算，所以分了两个if
            if (isAttackEnabled()) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - attackLastExecuteTime > 500) {
                    KeyBinding.onTick(minecraft.gameSettings.keyBindAttack.getKeyCode());
                    attackLastExecuteTime = currentTime;
                }
            }
        } catch (Throwable e) {
            LiteLoaderLogger.severe(e, e.getMessage());
        }
    }

    @Override
    public String getVersion() {
        return "1.0.1";
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {

    }

    @Override
    public String getName() {
        return "Always";
    }


    @Override
    public Class<? extends ConfigPanel> getConfigPanelClass() {
        return LiteModAlwaysConfigPanel.class;
    }

}
