package com.sollyu.minecraft;

import com.google.gson.annotations.Expose;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.ExposableOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.input.Keyboard;

import java.io.File;

@SuppressWarnings("WeakerAccess")
@ExposableOptions(strategy = ConfigStrategy.Versioned, filename="always.json")
public class LiteModAlways implements Tickable, Configurable {

    private static final KeyBinding swapKeyBinding = new KeyBinding("key.always.attack.toggle", Keyboard.KEY_F9, "key.categories.litemods");

    // 当前开关状态
    private boolean enableAttack       = false;
    private boolean enableJump         = false;
    private boolean enableJumpByAttack = true;

    // 上一次执行操作的时间
    private Long attackLastExecuteTime = -1L;   // 上一次执行攻击的时间
    private Long attackLastJumpTime    = -1L;   // 上一次跳跃时间

    @Expose private Long alwaysJumpInterval   = 3 * 60 * 1000L; // 在没有攻击的情况下，多久跳跃一次
    @Expose private Long alwaysAttackInterval = 1500L;          // 自动攻击间隔

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
                enableAttack = !enableAttack;
                enableJump = enableAttack;
                minecraft.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(I18n.format("always.message.attack.toggle", I18n.format(isEnableAttack() ? "always.string.enable" : "always.string.disabled"))));
            }

            long currentTime = System.currentTimeMillis();

            // 使用时间差计算是否执行操作
            // 尽量减少计算，所以分了两个if
            if (isEnableAttack()) {
                if (currentTime - attackLastExecuteTime > alwaysAttackInterval && minecraft.objectMouseOver.entityHit != null) {
                    minecraft.playerController.attackEntity(minecraft.player, minecraft.objectMouseOver.entityHit);
                    minecraft.player.swingArm(EnumHand.MAIN_HAND);

                    attackLastExecuteTime = currentTime;

                    if (this.isEnableJumpByAttack()) {
                        attackLastJumpTime = currentTime;
                    }
                }
            }

            // 当5秒后没有对象攻击时就自己跳一下，防止AKF
            if ((currentTime - attackLastJumpTime) > alwaysJumpInterval) {
                attackLastJumpTime = currentTime;
                minecraft.player.jump();
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getVersion() {
        return "1.0.5";
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {

    }

    @Override
    public String getName() {
        return "always";
    }

    @Override
    public Class<? extends ConfigPanel> getConfigPanelClass() {
        return LiteModAlwaysConfigPanel.class;
    }

    // get & set
    public boolean isEnableAttack() {
        return enableAttack;
    }

    public boolean isEnableJump() {
        return enableJump;
    }

    public void setEnableJump(boolean enableJump) {
        this.enableJump = enableJump;
    }

    public boolean isEnableJumpByAttack() {
        return enableJumpByAttack;
    }

    public void setEnableJumpByAttack(boolean enableJumpByAttack) {
        this.enableJumpByAttack = enableJumpByAttack;
    }

    public void setEnableAttack(boolean enableAttack) {
        this.enableAttack = enableAttack;
    }

    public Long getAlwaysJumpInterval() {
        return alwaysJumpInterval;
    }

    public void setAlwaysJumpInterval(Long alwaysJumpInterval) {
        this.alwaysJumpInterval = alwaysJumpInterval;
    }

    public Long getAlwaysAttackInterval() {
        return alwaysAttackInterval;
    }

    public void setAlwaysAttackInterval(Long alwaysAttackInterval) {
        this.alwaysAttackInterval = alwaysAttackInterval;
    }
}
