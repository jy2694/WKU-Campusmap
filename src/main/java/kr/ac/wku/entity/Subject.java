package kr.ac.wku.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class Subject {
    private final String division;
    private final String subjectId;
    private final String subjectName;
    private final int subjectNum;
    private final int credit;
    private final String time;
    private final String prof;
    private final int studentNumber;
    private final List<String> locations = new ArrayList<>();

    public Subject(String division,
                   String subjectId,
                   String subjectName,
                   int subjectNum,
                   float credit,
                   String time,
                   String prof,
                   int studentNumber,
                   String locations) {
        this.division = division;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.subjectNum = subjectNum;
        this.credit = (int)credit;
        this.time = time;
        this.prof = prof;
        this.studentNumber = studentNumber;
        this.locations.addAll(Arrays.asList(locations.split("/")));
    }
}
