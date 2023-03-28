package com.mores;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import snw.jkook.command.JKookCommand;
import snw.jkook.message.component.card.CardBuilder;
import snw.jkook.message.component.card.MultipleCardComponent;
import snw.jkook.message.component.card.Size;
import snw.jkook.message.component.card.Theme;
import snw.jkook.message.component.card.element.PlainTextElement;
import snw.jkook.message.component.card.module.HeaderModule;
import snw.jkook.message.component.card.module.SectionModule;
import snw.jkook.plugin.BasePlugin;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DaMeng extends BasePlugin {

    @Override
    public void onEnable() {
        getLogger().info("大萌BOT已成功加载");

        new JKookCommand("2042")
                .addOptionalArgument(String.class, "None")
                .executesUser((sender, arguments, message) -> {
                    String senderName = sender.getName();
                    if (arguments.length >= 1 & arguments[0] == "None") {
                        message.reply(senderName + "你好，请问我有什么可以帮助你的？");
                    } else {
                        String url = "https://battlefieldtracker.com/bf2042/profile/origin/" + arguments[0] + "/overview";
                        Map<String, String> data = new HashMap<>();
                        CloseableHttpClient httpClient = HttpClients.custom().setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3").build();
                        HttpGet request = new HttpGet(url);
                        try (CloseableHttpResponse response = httpClient.execute(request)) {
                            if (response.getStatusLine().getStatusCode() != 200) {
                                message.reply("响应超时");
                            } else {
                                try (InputStream inputStream = response.getEntity().getContent()) {
                                    Document doc = Jsoup.parse(inputStream, null, url);
                                    Element stats = doc.selectFirst("div[class=stats-container]");
                                    if (stats == null) {
                                        message.reply("不存在ID为" + arguments[0] + "的玩家");
                                        return;
                                    }
                                    Elements rows = stats.select("div[class=row]");

                                    for (Element row : rows) {
                                        String category = row.selectFirst("div[class=category]").text();
                                        String value = row.selectFirst("div[class=value]").text();
                                        data.put(category, value);
                                    }
                                    MultipleCardComponent cardComponent = new CardBuilder()
                                            .setTheme(Theme.PRIMARY)
                                            .setSize(Size.LG)
                                            .addModule(new HeaderModule(new PlainTextElement((String) arguments[0], false)))
                                            .addModule(new SectionModule(new PlainTextElement("击杀数：" + data.get("Kills")), null, null))
                                            .addModule(new SectionModule(new PlainTextElement("死亡数：" + data.get("Deaths")), null, null))
                                            .addModule(new SectionModule(new PlainTextElement("KD：" + data.get("K/D Ratio")), null, null))
                                            .build();
                                    message.reply(cardComponent);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).register(this);
    }
}