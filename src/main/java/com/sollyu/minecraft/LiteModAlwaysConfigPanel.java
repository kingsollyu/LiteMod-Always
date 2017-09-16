package com.sollyu.minecraft;

import com.mumfrey.liteloader.client.gui.GuiCheckbox;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.AbstractConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

import java.awt.*;
import java.net.URI;

/**
 * 配置界面
 */
public class LiteModAlwaysConfigPanel extends AbstractConfigPanel {

    public static final int SPACING     = 8;
    public static final int ITEM_HEIGHT = 20;

    private LiteModAlways liteModAlways = null;

    // 自动攻击GUI
    private GuiCheckbox     attackGuiCheckbox     = null;
    private ConfigTextField attackConfigTextField = null;

    // 自动跳跃GUI
    private GuiCheckbox     jumpGuiCheckbox         = null;
    private GuiCheckbox     jumpByAttackGuiCheckbox = null;
    private ConfigTextField jumpConfigTextField     = null;

    // 下载新版本GUI
    private GuiButton checkUpdateButton = null;

    @Override
    protected void addOptions(ConfigPanelHost host) {
        liteModAlways = host.getMod();

        int id = 0;

        try {
            attackGuiCheckbox     = this.addControl(new GuiCheckbox(id++, 20, (id - 1) * SPACING + 5, I18n.format("always.panel.attack.check")), checkboxConfigOptionListener);
            attackConfigTextField = this.addTextField(id++, attackGuiCheckbox.getButtonWidth() + 25, attackGuiCheckbox.yPosition, 40, 12 /* LiteLoader的CheckBox默认高度为12 */);

            jumpGuiCheckbox         = this.addControl(new GuiCheckbox(id++, 20, (id - 1) * SPACING + 5, I18n.format("always.panel.jump.check")), checkboxConfigOptionListener);
            jumpByAttackGuiCheckbox = this.addControl(new GuiCheckbox(id++, jumpGuiCheckbox.getButtonWidth() + 20, jumpGuiCheckbox.yPosition, I18n.format("always.panel.jump.attack.check")), checkboxConfigOptionListener);
            jumpConfigTextField     = this.addTextField(id++, jumpByAttackGuiCheckbox.xPosition + jumpByAttackGuiCheckbox.getButtonWidth() + 5, jumpByAttackGuiCheckbox.yPosition, 50, 12 /* LiteLoader的CheckBox默认高度为12 */);

            if (getLiteModAlways().getOnlineVersionJson() != null && !getLiteModAlways().getOnlineVersionJson().getAsJsonObject("promos").get(BuildConfig.MC_VERSION + "-latest").getAsString().equals(BuildConfig.MOD_VERSION)) {
                checkUpdateButton = this.addControl(new GuiButton(id++, 20, (id - 1) * SPACING, 200, ITEM_HEIGHT, I18n.format("always.panel.update.button", getLiteModAlways().getOnlineVersionJson().getAsJsonObject("promos").get(BuildConfig.MC_VERSION + "-recommended").getAsString())), updateButtonConfigOptionListener);
            }

            attackGuiCheckbox.checked = getLiteModAlways().isEnableAttack();
            attackConfigTextField.setText(String.valueOf(getLiteModAlways().getAlwaysAttackInterval()));

            jumpGuiCheckbox.checked = getLiteModAlways().isEnableJump();
            jumpByAttackGuiCheckbox.checked = getLiteModAlways().isEnableJumpByAttack();
            jumpConfigTextField.setText(String.valueOf(getLiteModAlways().getAlwaysJumpInterval()));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getPanelTitle() {
        return I18n.format("always.app.name");
    }

    @Override
    public void onPanelHidden() {
        getLiteModAlways().setAlwaysAttackInterval(Long.valueOf(attackConfigTextField.getText()));
        getLiteModAlways().setEnableAttack(attackGuiCheckbox.checked);

        getLiteModAlways().setEnableJump(jumpGuiCheckbox.checked);
        getLiteModAlways().setEnableJumpByAttack(jumpByAttackGuiCheckbox.checked);
        getLiteModAlways().setAlwaysJumpInterval(Long.valueOf(jumpConfigTextField.getText()));

        LiteLoader.getInstance().writeConfig(getLiteModAlways());
    }

    private ConfigOptionListener<GuiCheckbox> checkboxConfigOptionListener = new ConfigOptionListener<GuiCheckbox>() {
        @Override
        public void actionPerformed(GuiCheckbox control) {
            control.checked = !control.checked;
        }
    };

    private ConfigOptionListener<GuiButton> updateButtonConfigOptionListener = new ConfigOptionListener<GuiButton>()
    {
        @Override
        public void actionPerformed(GuiButton control)
        {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/kingsollyu/LiteMod-Always/releases/latest"));
            } catch (Throwable e) {
                LiteLoaderLogger.warning(e, "open browser error");
            }
        }
    };

    private LiteModAlways getLiteModAlways() {
        return liteModAlways;
    }
}
