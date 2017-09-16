package com.sollyu.minecraft;

import com.mumfrey.liteloader.client.gui.GuiCheckbox;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.AbstractConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;
import net.minecraft.client.resources.I18n;

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

    private GuiCheckbox     jumpGuiCheckbox         = null;
    private GuiCheckbox     jumpByAttackGuiCheckbox = null;
    private ConfigTextField jumpConfigTextField     = null;

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


//    /**
//     * 自动攻击间隔，
//     */
//    private ConfigOptionListener<GuiButton> attackGuiButtonConfigOptionListener = new ConfigOptionListener<GuiButton>() {
//        @Override
//        public void actionPerformed(GuiButton control) {
//            int attackIntervalCurrentPosition = attackIntervalDefaultList.indexOf(getLiteModAlways().getAlwaysAttackInterval()) + 1;
//            if (attackIntervalCurrentPosition >= attackIntervalDefaultList.size()) {
//                attackIntervalCurrentPosition = 0;
//            }
//            getLiteModAlways().setAlwaysAttackInterval(attackIntervalDefaultList.get(attackIntervalCurrentPosition));
//            control.displayString = "时间间隔：" + getLiteModAlways().getAlwaysAttackInterval();
//        }
//    };

    private LiteModAlways getLiteModAlways() {
        return liteModAlways;
    }
}
