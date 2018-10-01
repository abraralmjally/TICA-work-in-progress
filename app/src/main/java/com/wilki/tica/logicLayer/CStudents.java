package com.wilki.tica.logicLayer;
import android.content.Context;

import com.wilki.tica.dataLayer.DbHelper;

import java.io.Serializable;
import java.util.List;

public class CStudents implements Serializable{



    int StudentID;
    String Studentsname;
    int StudentGender;
    String SchoolName;
    String GroupName;

    public CStudents(int studentid, String studentname,int studentGender,  String schoolName) {

        StudentID= studentid;
        Studentsname= studentname;
        StudentGender =studentGender;
        SchoolName= schoolName;

    }

    public CStudents(int studentid, String studentname,int studentGender,  String schoolName,  String groupname) {

        StudentID= studentid;
        Studentsname= studentname;
        StudentGender =studentGender;
        SchoolName= schoolName;
        GroupName= groupname;

    }

    public CStudents(int studentid, String studentname,   String schoolName,  String groupname) {

        StudentID= studentid;
        Studentsname= studentname;
        GroupName=groupname;
        SchoolName= schoolName;

    }


    public static List<CStudents> getAllCStudents(Context cont , String schoolName){
        DbHelper dbHelper = new DbHelper(cont);
        List<CStudents> CStudentsList = dbHelper.fetchAllStudents(schoolName);
        dbHelper.close();
        return CStudentsList;
    }


    public int getStudentID ()
    {
        return StudentID;

    }

    public String getStudentsname ()
    {
        return Studentsname;

    }

    public String getGroupName ()
    {
        return GroupName;

    }

    public String getSchoolName ()
    {
        return SchoolName;

    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

}
