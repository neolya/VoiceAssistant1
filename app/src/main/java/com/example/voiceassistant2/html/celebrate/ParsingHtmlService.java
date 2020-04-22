package com.example.voiceassistant2.html.celebrate;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ParsingHtmlService {
    private static String URL;

    public ParsingHtmlService ()
    {
        URL = "http://mirkosmosa.ru/holiday/2020";
    }

    public static String getHoliday(String date)  throws IOException {
        Document document = Jsoup.connect(URL).get();
        //Element body = document.body();
        String res = "Не удалось найти праздник в этот день";
        String cssQuery = "div.month_cel_date";
        Elements elements = document.select(cssQuery);

        for (Element el: elements) {
            if (el.select(">span").text().equals(date))
            {
                Elements tmp = el.siblingElements();
                String tmpRes = "";
                for (Element t:tmp.select("li"))
                {
                    tmpRes+= t.text()+ "; ";
                }
                res = tmpRes;
                break;
            }
        }

        return res;
    }
}
