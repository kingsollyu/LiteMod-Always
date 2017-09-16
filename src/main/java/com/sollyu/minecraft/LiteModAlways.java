package com.sollyu.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.ExposableOptions;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.net.URL;

@SuppressWarnings("WeakerAccess")
@ExposableOptions(strategy = ConfigStrategy.Versioned, filename="always.json")
public class LiteModAlways implements Tickable, Configurable, Runnable {

    private static final KeyBinding swapKeyBinding = new KeyBinding("key.always.attack.toggle", Keyboard.KEY_F9, "key.categories.litemods");

    // 当前开关状态
    private boolean enableAttack       = false;
    private boolean enableJump         = false;
    private boolean enableJumpByAttack = true;
    private boolean enableShowUpdate   = false;

    private JsonObject onlineVersionJson = null;

    // 上一次执行操作的时间
    private Long attackLastExecuteTime = -1L;   // 上一次执行攻击的时间
    private Long attackLastJumpTime    = -1L;   // 上一次跳跃时间

    @Expose private Long alwaysJumpInterval   = 3 * 60L;    // 在没有攻击的情况下，多久跳跃一次
    @Expose private Long alwaysAttackInterval = 1500L;      // 自动攻击间隔

    @Override
    public void init(File configPath) {
        LiteLoader.getInput().registerKeyBinding(swapKeyBinding);
        new Thread(this).start();
    }

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        try {
            if (!inGame || minecraft.player == null) {
                return;
            }

            // 是否已经显示过更新
            if (isEnableShowUpdate() && getOnlineVersionJson() != null) {
                setEnableShowUpdate(false);
                minecraft.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("always.message.update", getOnlineVersionJson().getAsJsonObject("promos").get(BuildConfig.MC_VERSION + "-recommended")));
            }

            // 控制键被按下
            if (LiteModAlways.swapKeyBinding.isPressed()) {
                enableAttack = !enableAttack;
                enableJump = enableAttack;
                minecraft.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("always.message.attack.toggle", I18n.format(isEnableAttack() ? "always.string.enable" : "always.string.disabled")));
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
            if (isEnableJump() && (currentTime - attackLastJumpTime) > alwaysJumpInterval * 1000) {
                attackLastJumpTime = currentTime;
                minecraft.player.jump();
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getVersion() {
        return BuildConfig.MOD_VERSION;
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {

    }

    @Override
    public String getName() {
        return BuildConfig.MOD_ID;
    }

    @Override
    public Class<? extends ConfigPanel> getConfigPanelClass() {
        return LiteModAlwaysConfigPanel.class;
    }

    @Override
    public void run() {
        try {
            URL               versionUrl        = new URL("http://raw.githubusercontent.com/kingsollyu/LiteMod-Always/develop/online/last.version.json");
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            HttpResponse      httpResponse      = defaultHttpClient.execute(new HttpGet(versionUrl.toURI()));
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                setOnlineVersionJson(new JsonParser().parse(EntityUtils.toString(httpResponse.getEntity())).getAsJsonObject());
                if (!getOnlineVersionJson().getAsJsonObject("promos").get(BuildConfig.MC_VERSION + "-latest").getAsString().equals(BuildConfig.MOD_VERSION)) {
                    setEnableShowUpdate(true);
                }
            }else {
                LiteLoaderLogger.warning("%s check update error: %d", BuildConfig.MOD_ID, httpResponse.getStatusLine().getStatusCode());
            }
        } catch (Throwable e) {
            LiteLoaderLogger.warning(e, "");
        }
    }

    // get & set
    public boolean isEnableShowUpdate() {
        return enableShowUpdate;
    }

    public void setEnableShowUpdate(boolean enableShowUpdate) {
        this.enableShowUpdate = enableShowUpdate;
    }

    public JsonObject getOnlineVersionJson() {
        return onlineVersionJson;
    }

    public void setOnlineVersionJson(JsonObject onlineVersionJson) {
        this.onlineVersionJson = onlineVersionJson;
    }

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
