package com.sollyu.minecraft;

import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Keyboard;

import java.io.File;


public class LiteModAlways implements Tickable, Configurable {

    private static final KeyBinding swapKeyBinding = new KeyBinding("key.always.attack.toggle", Keyboard.KEY_F9, "key.categories.litemods");

    // 当前开关状态
    private boolean attackEnabled = false;

    // 上一次执行操作的时间
    private Long attackLastExecuteTime = -1L;   // 上一次执行攻击的时间
    private Long attackLastJumpTime    = -1L;   // 上一次跳跃时间

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
            if (!inGame || minecraft.player == null) {
                return;
            }

            // 控制键被按下
            if (LiteModAlways.swapKeyBinding.isPressed()) {
                attackEnabled = !attackEnabled;
                minecraft.player.sendChatMessage(String.format("/t %s always attack :%s", minecraft.player.getName(), attackEnabled ? "enable" : "disabled"));
            }

            // 使用时间差计算是否执行操作
            // 尽量减少计算，所以分了两个if
            if (isAttackEnabled()) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - attackLastExecuteTime > 900 && minecraft.objectMouseOver.entityHit != null) {
                    minecraft.playerController.attackEntity(minecraft.player, minecraft.objectMouseOver.entityHit);
                    minecraft.player.swingArm(EnumHand.MAIN_HAND);
                    // KeyBinding.onTick(minecraft.gameSettings.keyBindAttack.getKeyCode());
                    attackLastExecuteTime = currentTime;
                    attackLastJumpTime    = currentTime;
                }

                // 当5秒后没有对象攻击时就自己跳一下，防止AKF
                if (currentTime - attackLastJumpTime > 15 * 1000) {
                    attackLastJumpTime    = currentTime;
                    minecraft.player.jump();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            // TODO: java.lang.IllegalStateException: Can't overwrite cause with java.lang.NullPointerException
            //LiteLoaderLogger.warning(e, e.getMessage());
        }
    }

    @Override
    public String getVersion() {
        return "1.0.3";
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
