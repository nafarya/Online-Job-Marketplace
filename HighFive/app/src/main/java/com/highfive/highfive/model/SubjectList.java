package com.highfive.highfive.model;

import java.util.ArrayList;

/**
 * Created by dan on 09.04.17.
 */

public class SubjectList {
    private ArrayList<Subject> list;

    public SubjectList(ArrayList<Subject> list) {
        this.list = list;
    }

    public ArrayList<Subject> getSubjectList() {
        return this.list;
    }

    public ArrayList<Subject> getStudentSubjectList() {
        ArrayList<Subject> tmp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getDifficultyLevel().equals("student")) {
                tmp.add(list.get(i));
            }
        }
        return tmp;
    }

    public String getSubjectNameById(String id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id)) {
                return list.get(i).getName();
            }
        }
        return "no such subjectId";
    }
}
