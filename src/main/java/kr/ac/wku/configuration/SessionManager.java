package kr.ac.wku.configuration;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    public static final String SESSION_COOKIE_NAME = "userToken";

    private final Map<String, Object> sessionStore = new ConcurrentHashMap<>();

    public void createSession(Object value, HttpServletResponse response){
        String SessionId = UUID.randomUUID().toString();
        sessionStore.put(SessionId, value);

        Cookie mySessionCookie = new Cookie(SESSION_COOKIE_NAME, SessionId);
        mySessionCookie.setPath("/");
        response.addCookie(mySessionCookie);
    }
    public Object getSession(HttpServletRequest request){
        Cookie Sessioncookie = findCookie(request, SESSION_COOKIE_NAME);
        if(Sessioncookie == null){
            return null;
        }
        return sessionStore.get(Sessioncookie.getValue());
    }

    public Cookie findCookie(HttpServletRequest request, String cookieName){
        Cookie[] cookies = request.getCookies();

        if(cookies == null){
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findAny()
                .orElse(null);
    }

    public void expireCookie(HttpServletRequest request){
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if(sessionCookie != null){
            sessionStore.remove(sessionCookie.getValue());
        }
    }
}