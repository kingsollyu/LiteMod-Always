package com.sollyu.minecraft;

import com.mumfrey.liteloader.modconfig.AbstractConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;

/**
 * 配置界面
 */
public class LiteModAlwaysConfigPanel extends AbstractConfigPanel {
    @Override
    protected void addOptions(ConfigPanelHost host) {
        addLabel(0, 0, 0, 100, 0, 0,"dasfsad");
    }

    @Override
    public String getPanelTitle() {
        return "Hi ==============";
    }

    @Override
    public void onPanelHidden() {

    }
}
