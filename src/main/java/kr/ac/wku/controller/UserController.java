package kr.ac.wku.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.wku.configuration.SessionManager;
import kr.ac.wku.configuration.WonkwangAPI;
import kr.ac.wku.dto.UserLoginData;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@AllArgsConstructor
@RestController
public class UserController {

    private final SessionManager sessionManager;
    private final WonkwangAPI wonkwangAPI;

    @PostMapping("/auth/signin")
    public ResponseEntity<Object> signIn(UserLoginData data, HttpServletResponse response) throws IOException {
        String line = wonkwangAPI.getWKUCookies(data.getId(), data.getPw());
        if(!line.contains("wkuTokenKey")) return ResponseEntity.status(401).body("아이디 또는 패스워드가 일치하지 않습니다.");
        sessionManager.createSession(line, response);
        return ResponseEntity.ok().body(wonkwangAPI.getName(line) + "(" + wonkwangAPI.getStudentNumber(line) + ")");
    }

    @GetMapping("/auth/subjects")
    public ResponseEntity<Object> getSubjects(HttpServletRequest request) throws IOException {
        String cookies = (String) sessionManager.getSession(request);
        if(cookies == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");
        return ResponseEntity.ok().body(wonkwangAPI.getSubjects(cookies));
    }

    @PostMapping("/auth/signout")
    public void signOut(HttpServletRequest request){
        sessionManager.expireCookie(request);
    }

    @GetMapping("/auth/today")
    public ResponseEntity<Object> getTodayTimetable(HttpServletRequest request) throws IOException {
        String cookies = (String) sessionManager.getSession(request);
        if(cookies == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");
        return ResponseEntity.ok().body(wonkwangAPI.getTodayTimetable(wonkwangAPI.getSubjects(cookies)));
    }

    @GetMapping("/auth/timetable")
    public ResponseEntity<Object> getAllTimetable(HttpServletRequest request) throws IOException {
        String cookies = (String) sessionManager.getSession(request);
        if(cookies == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");
        return ResponseEntity.ok().body(wonkwangAPI.getTimetable(wonkwangAPI.getSubjects(cookies)));
    }
}
