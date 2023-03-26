package com.mores;

import snw.jkook.command.JKookCommand;
import snw.jkook.plugin.BasePlugin;

public class DaMeng extends BasePlugin {

    @Override
    public void onEnable() {
        getLogger().info("大萌BOT已成功加载");

        new JKookCommand("你好")
                .executesUser((sender, arguments, message) -> {
                    String senderName = sender.getName();
                    message.reply(senderName + "你好，请问我有什么可以帮助你的？");
                })
                .register(this);
    }
}