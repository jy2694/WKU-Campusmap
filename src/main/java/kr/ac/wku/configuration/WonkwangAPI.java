package kr.ac.wku.configuration;


import kr.ac.wku.entity.Subject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;

@Component
public class WonkwangAPI {

    public String getName(String cookies) throws IOException {
        URL url = new URL("https://intra.wku.ac.kr/SWupis/V005/Service/Stud/Resume/resume.jsp?sm=3");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Cookie", cookies);
        InputStream is = con.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(is, "euc_kr"));
        String line;
        boolean print = false;
        while((line = in.readLine()) != null){
            if(line.contains("성 명")) print = true;
            else if(print){
                return line.trim().replace("<td bgcolor='#FFFFFF'>", "").replace("</td>","").replace("<td>","");
            }
        }
        return null;
    }

    public String getStudentNumber(String cookies) throws IOException{
        URL url = new URL("https://intra.wku.ac.kr/SWupis/V005/Service/Stud/Resume/resume.jsp?sm=3");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Cookie", cookies);
        InputStream is = con.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(is, "euc_kr"));
        String line;
        boolean print = false;
        while((line = in.readLine()) != null){
            if(line.contains("학 번")) print = true;
            else if(print){
                return line.trim().replace("<td bgcolor='#FFFFFF'>", "").replace("</td>","").replace("<td>","");
            }
        }
        return null;
    }

    public Subject[] getSubjects(String cookies) throws IOException {
        URL url = new URL("https://intra.wku.ac.kr/SWupis/V005/Service/Stud/Sugang/Request/requestList.jsp?sm=3");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Cookie", cookies);
        InputStream is = con.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(is, "euc_kr"));
        String line;
        boolean print = false;
        List<String> rawdatas = new ArrayList<>();
        while((line = in.readLine()) != null) {
            if(line.contains("화면 출력")) print = true;
            if(line.contains("총 수강학점")) print = false;
            if(print && (line.contains("<font color='black'>") || line.contains("href=\"JavaScript:DetailView("))) {
                rawdatas.add(line);
            }
        }

        Subject[] subjects = new Subject[rawdatas.size()/14];
        for(int i = 0; i < rawdatas.size(); i += 14) {
            String division = rawdatas.get(i).split(">")[2].replace("</font", "");
            String subjectId = rawdatas.get(i+1).split(">")[2].replace("</font", "");
            String subjectName = rawdatas.get(i+3).split(">")[1].replace("</a", "");
            int subjectNum = Integer.parseInt(rawdatas.get(i+4).split(">")[2].replace("</font", ""));
            float credit = Float.parseFloat(rawdatas.get(i+5).split(">")[2].replace("</font", ""));
            String time = rawdatas.get(i+6).split(">")[2].replace("</font", "");
            String prof = rawdatas.get(i+7).split(">")[2].replace("</font", "");
            int studentNumber = Integer.parseInt(rawdatas.get(i+8).split(">")[2].replace("</font", ""));
            String locations = rawdatas.get(i+13).split(">")[2].replace("</font", "");
            Subject subject = new Subject(division, subjectId, subjectName, subjectNum,
                    credit, time, prof, studentNumber, locations);
            subjects[i/14] = subject;
        }
        return subjects;
    }

    public String getWKUCookies(String userID, String userPW) throws IOException {
        Map<String,Object> params = new LinkedHashMap<>(); // 파라미터 세팅
        params.put("nextURL", "https://intra.wku.ac.kr/SWupis/V005/loginReturn.jsp");
        params.put("site", "SWUPIS");
        params.put("userid", userID);
        params.put("passwd", userPW);

        StringBuilder postData = new StringBuilder();
        for(Map.Entry<String,Object> param : params.entrySet()) {
            if(postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "EUC-KR"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "EUC-KR"));
        }
        byte[] postDataBytes = postData.toString().getBytes("EUC-KR");

        HashMap<String, String> cookiemap = new HashMap<String, String>();

        CookieManager manager = new CookieManager();
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);

        URL url = new URL("https://auth.wku.ac.kr/Cert/User/Login/login.jsp?"+postData.toString());
        URLConnection connection = url.openConnection();
        Object obj = connection.getContent();
        connection = url.openConnection();

        url = new URL("https://intra.wku.ac.kr/SWupis/V005/loginReturn.jsp");
        connection = url.openConnection();
        obj = connection.getContent();
        connection = url.openConnection();

        CookieStore cookieJar = manager.getCookieStore();
        List<HttpCookie> cookies = cookieJar.getCookies();
        for (HttpCookie cookie: cookies) {
            String[] data = cookie.toString().split("=");
            cookiemap.put(data[0], data[1]);
        }


        StringBuilder cookie = new StringBuilder();
        for(String key : cookiemap.keySet()) {
            cookie.append(String.format("%s=%s;", key, cookiemap.get(key)));
        }
        return cookie.toString();
    }

    public Subject[][] getTimetable(Subject[] subjects){
        Subject[][] table = new Subject[5][9];
        String[] day = new String[]{"월","화","수","목","금"};
        for(Subject subject : subjects){
            String time = subject.getTime();
            for(int dc = 0; dc < 5; dc ++){
                int index = time.indexOf(day[dc]);
                if(index > -1){
                    index++;
                    while(Character.toString(time.charAt(index)).matches("[0-9]+")){
                        table[dc][time.charAt(index)-'0'-1] = subject;
                        index++;
                        if(index >= time.length()) break;
                    }
                }
            }
        }
        return table;
    }

    public Subject[] getTodayTimetable(Subject[] subjects){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if(day == Calendar.SUNDAY || day == Calendar.SATURDAY) return new Subject[]{};
        return getTimetable(subjects)[day-2];
    }
}
