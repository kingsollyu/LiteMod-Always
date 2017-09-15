package com.sollyu.minecraft;

import com.mumfrey.liteloader.client.gui.GuiCheckbox;
import com.mumfrey.liteloader.modconfig.AbstractConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;
import net.minecraft.client.resources.I18n;

/**
 * 配置界面
 */
public class LiteModAlwaysConfigPanel extends AbstractConfigPanel {
    @Override
    protected void addOptions(ConfigPanelHost host) {

        this.addLabel(1, 0, 0, 200, 32, 0xFFFF55, "攻击间隔");

        try {
            this.addTextField(2, 0, 200, 200, 32 ).setText("900");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getPanelTitle() {
        return "Hi ==============" + I18n.format("app.name");
    }

    @Override
    public void onPanelHidden() {

    }
}
