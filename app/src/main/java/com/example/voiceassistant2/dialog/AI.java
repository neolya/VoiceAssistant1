package com.example.voiceassistant2.dialog;

import androidx.core.util.Consumer;

import com.example.voiceassistant2.digit.api.ConvertToString;
import com.example.voiceassistant2.html.celebrate.ParsingHtmlService;
import com.example.voiceassistant2.weatherstack.api.ForecastToString;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class AI{
    Map<String, String> dictionary;
    private String time, dayOfWeek, day, summer;
    private int daysToSummer;
    SimpleDateFormat format;
    Date dateOne, dateTwo;
    String[] answer;

    public AI ()
    {
        format = new SimpleDateFormat("dd.MM.yyyy");
        dateOne = null;
        dateTwo = null;
        summer = "01.06.2020";
        day = new SimpleDateFormat ("dd.MM.yyyy").format(new Date());
        dictionary = new HashMap<>();
        time = new SimpleDateFormat ("hh:mm").format(new Date());
        dayOfWeek = new SimpleDateFormat ("EEEE").format(new Date());
        answer = new String[]{"Вопрос понял, думаю..."};
        daysToSummer = countDaysToSummer();

        dictionary.put("привет","Привет");
        dictionary.put("как дела","Не плохо");
        dictionary.put("чем занимаешься","Отвечаю на вопросы");
        dictionary.put("какой сегодня день", day);
        dictionary.put("который час", time);
        dictionary.put("какой сегодня день недели", dayOfWeek);
        dictionary.put("сколько дней до лета", "блин блинский, до лета "+ String.valueOf(daysToSummer) + " дня");
    }

    private String getDegreeEnding(int b) {
        int a = Math.abs(b);
        if(a>=5 && a <=20)
            return " градусов ";
        if(a%10==0 || a%10 >=5 && a%10 <=9)
            return " градусов ";
        if(a%10 == 1)
            return " градус ";

        return " градуса ";
    }

    private static DateFormatSymbols myDateFormatSymbols = new DateFormatSymbols() {
        @Override
        public String[] getMonths() {
            return new String[]{"января", "февраля", "марта", "апреля", "мая", "июня",
                    "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        }
    };

    private String getDate(String s)
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", myDateFormatSymbols );
        String resDay = null;

        Pattern datePattern = Pattern.compile("(\\d{1,2}.\\d{1,2}.\\d{4})"); // задаем текст запроса
        Matcher matcher = datePattern.matcher(s);
        if (matcher.find())
        {
            String tmp = matcher.group(1);
            try {
                Date tmp2 = format.parse(tmp);
                resDay = dateFormat.format(tmp2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (s.contains("сегодня"))
        {
            resDay = dateFormat.format(c.getTime());
        }
        else if (s.contains("завтра"))
        {
            c.add(Calendar.DAY_OF_YEAR, 1);
            resDay = dateFormat.format(c.getTime());
        }
        else if (s.contains("вчера"))
        {
            c.add(Calendar.DAY_OF_YEAR, -1);
            resDay = dateFormat.format(c.getTime());
        }

        return resDay;
    }

    private int countDaysToSummer ()
    {
        try {
            dateOne = format.parse(summer);
            dateTwo = format.parse(day);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Количество дней между датами в миллисекундах
        long difference = dateOne.getTime() - dateTwo.getTime();
        // Перевод количества дней между датами из миллисекунд в дн
        int diff =  (int)(difference / (24 * 60 * 60 * 1000)); // миллисекунды / (24ч * 60мин * 60сек * 1000мс)
        return diff;
    }

    public void getAnswer(String question, final Consumer<String> callback) {

        question = question.toLowerCase();

        Pattern convertPattern = Pattern.compile("перевести число (\\d+)"); // задаем текст запроса
        Matcher convertMatcher = convertPattern.matcher(question);

        Pattern cityPattern = Pattern.compile("погода в городе (\\p{L}+)", Pattern.CASE_INSENSITIVE); // задаем текст запроса
        Matcher cityMatcher = cityPattern.matcher(question);

        Pattern celebratePattern = Pattern.compile("праздник", Pattern.CASE_INSENSITIVE); // задаем текст запроса
        Matcher celebrateMatcher = celebratePattern.matcher(question);

        if (cityMatcher.find()){ // погода в городе Z
            final String cityName = cityMatcher.group(1);
            answer[0] = "Не знаю я, какая у вас там погода";
            ForecastToString.getForecast(cityName, new Consumer<String>() {
                @Override
                public void accept(String s) {
                    if(s!=null) {
                        callback.accept(String.join(", ", s));
                    }
                    else { callback.accept(String.join(", ",  answer[0])); }
                }
            });
        }
        else if (convertMatcher.find()) { // перевод числа из цифр в буквы
            final String digit = convertMatcher.group(1);
            answer[0] = "Не знаю, что за число";

            ConvertToString.getConvert(digit, new Consumer<String>() {
                @Override
                public void accept(String s) {
                    if(s!=null) {
                        callback.accept(String.join(", ", s));
                    }
                    else { callback.accept(String.join(", ", answer[0])); }
                }
            });
        }
        else if (celebrateMatcher.find()) // праздник в заданную дату
        {
            answer[0] = "Не могу дать ответ";
            ParsingHtmlService phs = new ParsingHtmlService();
            String findDate = getDate(question);

 /*           AsyncTask at = new AsyncTask<String, Integer, Void>(){
                String s = "";
                @Override
                protected Void doInBackground(String... strings) {

                    for (int i = 0; i < strings.length; i++) {
                        try {
                            s += strings[i] +": " + phs.getHoliday(strings[i]) + "\n";
                        } catch (IOException e) {
                        s = "Не могу дать ответ";
                    }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result){
                    //super.onPostExecute(result);
                    callback.accept(String.join(", ", s));
                }
             };
            if (findDate != null)
                at.execute(findDate.split(","));
            else callback.accept(String.join(", ", answer[0]));*/

            if (findDate != null)
            {
                String[] strings = findDate.split(",");
                final String[] s = {" "};
                Observable.fromCallable(() -> {
                    for (int i = 0; i < strings.length; i++)
                    {
                        try
                        {
                            s[0] += strings[i] + ": " + phs.getHoliday(strings[i]) + "\n";
                        } catch (IOException e) {
                            s[0] = "Что-то пошло не так";
                        }
                    }
                    return s[0];
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    callback.accept(String.join(", ", s[0]));
                });
            }
            else callback.accept(String.join(", ", answer[0]));
        }
        else {

            for (Map.Entry<String, String> entry : dictionary.entrySet()) {
                if (question.contains(entry.getKey())) {
                    answer[0] = entry.getValue();
                    break;
                }
            }

            callback.accept(String.join(", ", answer[0]));
        }
    }
}
