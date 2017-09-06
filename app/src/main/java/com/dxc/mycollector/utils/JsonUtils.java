/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector.utils;

import com.dxc.mycollector.logs.Logger;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * @desc 验证是否是合法的Json串
 * Created by Gospel on 2017/9/6 14:54
 * DXC technology
 */

public class JsonUtils {
    public static boolean isBadJson(String json) {
        return !isGoodJson(json);
    }

    public static boolean isGoodJson(String json) {
        try {
            new JsonParser().parse(json);
            if (json.indexOf("data") > -1) {
                Logger.i("JsonUtils", "JSON 是否合法验证: 合法的JSON字符串。【" + json + "】");
                return true;
            } else {
                Logger.e("JsonUtils", "JSON 是否合法验证: 非法的JSON字符串。" + json);
                return false;
            }
        } catch (JsonParseException e) {
            Logger.e("JsonUtils", "JSON 是否合法验证: 非法的JSON字符串。【" + json + "】");
            return false;
        }
    }
}
