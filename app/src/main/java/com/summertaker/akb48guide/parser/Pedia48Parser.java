package com.summertaker.akb48guide.parser;

import android.util.Log;

import com.summertaker.akb48guide.common.Config;
import com.summertaker.akb48guide.data.GroupData;
import com.summertaker.akb48guide.data.MemberData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Pedia48Parser extends BaseParser {

    private String mTag = "##### " + this.getClass().getSimpleName();

    private String mBaseUrl = "http://48pedia.org";

    public void parseList(String response, GroupData groupData, ArrayList<MemberData> memberDatas) {
        /*
        <table class="sortable wikitable" style="text-align:center;">
        <tr>
            <th>現</th>
            <th class="unsortable" style="min-width:30px;"></th>
            <th> 名前</th>
            <th> ニックネーム</th>
            <th> 生年月日</th>
            <th> 出身地</th>
            <th> 期</th>
            <th> 昇格日</th>
            <th class="unsortable"> 備考</th>
        </tr>
        <tr id="teamA">
            <td data-sort-value="001" class="bgcolor bgcolor-A"> A</td>
            <td>
                <a href="/%E3%83%95%E3%82%A1%E3%82%A4%E3%83%AB:2016%E5%B9%B4AKB48%E3%83%97%E3%83%AD%E3%83%95%E3%82%A3%E3%83%BC%E3%83%AB_%E5%85%A5%E5%B1%B1%E6%9D%8F%E5%A5%88.jpg" class="image">
                    <img alt="2016年AKB48プロフィール 入山杏奈.jpg" src="/images/thumb/6/6c/2016%E5%B9%B4AKB48%E3%83%97%E3%83%AD%E3%83%95%E3%82%A3%E3%83%BC%E3%83%AB_%E5%85%A5%E5%B1%B1%E6%9D%8F%E5%A5%88.jpg/50px-2016%E5%B9%B4AKB48%E3%83%97%E3%83%AD%E3%83%95%E3%82%A3%E3%83%BC%E3%83%AB_%E5%85%A5%E5%B1%B1%E6%9D%8F%E5%A5%88.jpg" width="50" height="63" srcset="/images/thumb/6/6c/2016%E5%B9%B4AKB48%E3%83%97%E3%83%AD%E3%83%95%E3%82%A3%E3%83%BC%E3%83%AB_%E5%85%A5%E5%B1%B1%E6%9D%8F%E5%A5%88.jpg/75px-2016%E5%B9%B4AKB48%E3%83%97%E3%83%AD%E3%83%95%E3%82%A3%E3%83%BC%E3%83%AB_%E5%85%A5%E5%B1%B1%E6%9D%8F%E5%A5%88.jpg 1.5x, /images/thumb/6/6c/2016%E5%B9%B4AKB48%E3%83%97%E3%83%AD%E3%83%95%E3%82%A3%E3%83%BC%E3%83%AB_%E5%85%A5%E5%B1%B1%E6%9D%8F%E5%A5%88.jpg/100px-2016%E5%B9%B4AKB48%E3%83%97%E3%83%AD%E3%83%95%E3%82%A3%E3%83%BC%E3%83%AB_%E5%85%A5%E5%B1%B1%E6%9D%8F%E5%A5%88.jpg 2x" />
                </a>
            </td>
            <td>
                <ruby data-sort-value="いりやま あんな">
                    <rb>
                        <a href="/%E5%85%A5%E5%B1%B1%E6%9D%8F%E5%A5%88" title="入山杏奈" class="カテゴリ-AKB48のメンバー カテゴリ-チームAのメンバー">
                            入山杏奈
                        </a>
                    </rb>
                    <rp>（</rp><rt>いりやま あんな</rt><rp>）</rp>
                </ruby>
            </td>
            <td> あんにん</td>
            <td> <span data-sort-value="19951203">1995年12月3日</span> <span style="white-space:nowrap;">（20歳）</span></td>
            <td> <span data-sort-value="12">千葉県</span></td>
            <td> <span data-sort-value="201003"> <span style="white-space:nowrap;">10期</span></span></td>
            <td> <span data-sort-value="20110723">2011年7月23日</span></td>
            <td style="text-align:left;"> 太田プロダクション所属</td>
        </tr>
        */
        response = clean(response);
        //Log.e(mTag, response);

        Document doc = Jsoup.parse(response);

        Element table = doc.select(".wikitable").first();
        if (table == null) {
            return;
        }
        //Log.e(mTag, table.text().substring(0, 200));

        int rowCount = 0;

        for (Element tr : table.select("tr")) {
            if (rowCount < 1) {
                rowCount++;
                continue;
            }

            Elements tds = tr.select("td");
            if (tds.size() < 9) {
                continue;
            }

            String team = "";
            String name = "";
            String furigana = "";
            String nickname = "";
            String birthday = "";
            String hometown = "";
            String generation = "";
            String thumbnailUrl = "";

            Element el;
            Element td;

            int idx = 0;

            td = tds.get(idx++);
            team = td.text().trim();

            td = tds.get(idx++);
            el = td.select("a").first().select("img").first();
            if (el != null) {
                //Log.e(mTag, "== " + el.toString());
                thumbnailUrl = el.attr("src").replace("/thumb/", "/");
                String[] array = thumbnailUrl.split("/50px-");
                thumbnailUrl = "http://48pedia.org" + array[0];
                //Log.e(mTag, "thumbnailUrl: " + thumbnailUrl);
            }
            td = tds.get(idx++);
            //if (groupData.getId().equals(Config.GROUP_ID_JKT48)) {
                name = td.select("rb").first().text().trim();
                furigana = td.select("rt").first().text().trim();
            //} else {
            //    furigana = td.select("ruby").first().attr("data-sort-value");
            //    name = td.select("a").first().text().trim();
            //}

            if (groupData.getId().equals(Config.GROUP_ID_SNH48)) {
                idx++;
            }

            td = tds.get(idx++);
            nickname = td.text().trim();

            td = tds.get(idx++);
            el = td.select("span").first();
            birthday = el.attr("data-sort-value");

            td = tds.get(idx++);
            hometown = td.text();

            td = tds.get(idx++);
            generation = td.text();

            //Log.e(mTag, name + " / " + furigana + " / " + nickname + " / " +  birthday + " / " + hometown + " / " + generation + " / " + thumbnailUrl);

            MemberData memberData = new MemberData();
            memberData.setGroupId(groupData.getId());
            memberData.setGroupName(groupData.getName());
            memberData.setTeamName(team);
            memberData.setName(name);
            memberData.setFurigana(furigana);
            memberData.setNickname(nickname);
            memberData.setBirthday(birthday);
            memberData.setHometown(hometown);
            memberData.setGeneration(generation);
            memberData.setThumbnailUrl(thumbnailUrl);

            memberDatas.add(memberData);

            rowCount++;
        }
    }
}
