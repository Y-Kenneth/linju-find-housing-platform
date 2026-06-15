package com.linjufind.util;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * English → Hanzi lookup for Chinese city names.
 *
 * There is no reliable way to compute Hanzi from romanized pinyin (many
 * characters share a spelling), so the mapping is a curated dictionary.
 * Cities not in the table simply render English-only — nothing breaks.
 *
 * Registered as the Spring bean {@code cityNames} so Thymeleaf templates can
 * call it directly, e.g. {@code ${cityNames.hanzi(listing.city)}}.
 */
@Component("cityNames")
public class CityNames {

    private static final Map<String, String> HANZI = build();

    private static Map<String, String> build() {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("beijing", "北京");
        m.put("shanghai", "上海");
        m.put("guangzhou", "广州");
        m.put("shenzhen", "深圳");
        m.put("nanjing", "南京");
        m.put("changsha", "长沙");
        m.put("chengdu", "成都");
        m.put("chongqing", "重庆");
        m.put("hangzhou", "杭州");
        m.put("wuhan", "武汉");
        m.put("xian", "西安");
        m.put("xi'an", "西安");
        m.put("tianjin", "天津");
        m.put("suzhou", "苏州");
        m.put("qingdao", "青岛");
        m.put("dalian", "大连");
        m.put("xiamen", "厦门");
        m.put("kunming", "昆明");
        m.put("hefei", "合肥");
        m.put("zhengzhou", "郑州");
        m.put("jinan", "济南");
        m.put("shenyang", "沈阳");
        m.put("harbin", "哈尔滨");
        m.put("ningbo", "宁波");
        m.put("wuxi", "无锡");
        m.put("foshan", "佛山");
        m.put("dongguan", "东莞");
        m.put("fuzhou", "福州");
        m.put("nanchang", "南昌");
        m.put("guiyang", "贵阳");
        m.put("nanning", "南宁");
        m.put("lanzhou", "兰州");
        m.put("taiyuan", "太原");
        m.put("shijiazhuang", "石家庄");
        m.put("changchun", "长春");
        m.put("wenzhou", "温州");
        m.put("zhuhai", "珠海");
        m.put("yinchuan", "银川");
        m.put("hohhot", "呼和浩特");
        m.put("huhhot", "呼和浩特");
        m.put("urumqi", "乌鲁木齐");
        m.put("lhasa", "拉萨");
        m.put("haikou", "海口");
        m.put("sanya", "三亚");
        return m;
    }

    /** Hanzi for a city (case-insensitive), or {@code null} if not in the table. */
    public String hanzi(String city) {
        if (city == null) {
            return null;
        }
        return HANZI.get(city.trim().toLowerCase());
    }
}
