package com.wilki.tica.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.wilki.tica.dataLayer.DbContract.Sessions;
import com.wilki.tica.R;
import com.wilki.tica.dataLayer.DbContract;
import com.wilki.tica.dataLayer.DbHelper;
import com.wilki.tica.logicLayer.CStudents;
import com.wilki.tica.logicLayer.TaskPerformance;
import com.wilki.tica.dataLayer.DbContract.Students;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CreateNewGroupActivity extends AppCompatActivity  {


   private Spinner SchoolNameSpinner;

    /** EditText field to enter the group name */
    private EditText GroupNametEditText;

    /***** Student 1 *****/
    /** spinner for the students name in the datatbase **/
    private Spinner S1Spinner;
    /** textview to retrieve   the students gender  from the datatbase **/
    private TextView S1Gender;

    /***** Student 2 *****/
    /** spinner for the students name in the datatbase **/
    private Spinner S2Spinner;
    /** textview to retrieve   the students gender  from the datatbase **/
    private TextView S2Gender;


    private String Student1Name;
    private String Student2Name;
    private DbHelper mDbHelper;
    private String SSchoolName ;
    List<CStudents> StudentsNameslist ;
    List<String> Schoolslist ;





    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        mDbHelper = new DbHelper(this);
        // Find all relevant views that we will need to read user input from
        SchoolNameSpinner = (Spinner) findViewById(R.id.spinner_school);
        GroupNametEditText = (EditText) findViewById(R.id.edit_group_name);
        //Student 1
        S1Spinner = (Spinner) findViewById(R.id.spinner_student1);
        // Student 2
        S2Spinner = (Spinner) findViewById(R.id.spinner_student2);
        // Spinner click listener
        Intent extras = getIntent();

        String selesctedschool = extras.getStringExtra("SelectedSchool");
        List<CStudents> Studentlist= (List<CStudents>) extras.getSerializableExtra("Studentslist");
        List<String> SchoolList= (List<String>) extras.getSerializableExtra("Schoolslist");
        if(extras !=null)
        {
            if(selesctedschool !=null)
            {
              SSchoolName = selesctedschool;

            }
            if(Studentlist!= null)

            {
                StudentsNameslist = Studentlist;
            }
            else
            { StudentsNameslist=mDbHelper.fetchAllStudents();}

            if(SchoolList!=null)
            {
                Schoolslist=SchoolList;
            }
            else
            {
                Schoolslist=mDbHelper.fetchAllSchools();
            }
        }

        SetupSchoolSpinner1(Schoolslist);
        SetupStudent1Spinner(SSchoolName,StudentsNameslist);

    }

    //to create group from the EditText
    public void CreateNewGroupDB(){
        String groupnameString = GroupNametEditText.getText().toString().trim();

       DbHelper mDbHelper = new DbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues UpdateStudent1 = new ContentValues();
        UpdateStudent1.put(Students.COLUMN_NAME_GROUP_NAME,groupnameString);
        long newwRowID = db.update(DbContract.Students.TABLE_NAME, UpdateStudent1,Students.COLUMN_NAME_STUDENT + "= ?",new String [] {Student1Name} );
        Log.v("CreateNewGroupActivity","New group is added to Student 1"+ newwRowID);

        ContentValues UpdateStudent2 = new ContentValues();
        UpdateStudent2.put(Students.COLUMN_NAME_GROUP_NAME,groupnameString);

        long newwRowID2 = db.update(DbContract.Students.TABLE_NAME, UpdateStudent2,Students.COLUMN_NAME_STUDENT + "= ?",new String [] {Student2Name} );
        Log.v("CreateNewGroupActivity","New group is added to Student 2"+ newwRowID);

        if (newwRowID == -1){
            Toast.makeText(this,"Error with saving Group to Student 1", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"Group name updated successfully for Student 1: " + newwRowID, Toast.LENGTH_SHORT).show ();
        }

        if (newwRowID2 == -1){
            Toast.makeText(this,"Error with saving Group to Student 2", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"Group name updated successfully for Student 2: " + newwRowID, Toast.LENGTH_SHORT).show ();
        }

        db.close(); // Closing database connection

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_group_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                CreateNewGroupDB();
                //TODO after the group is created a new listed should be sent to this page

                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                /*        Intent toLaunch = new Intent(getApplicationContext(), GroupSelectorActivity.class);
                toLaunch.putExtra("Studentslist",(Serializable) Studentslist);
                startActivity(toLaunch);*/
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void SetupSchoolSpinner1(List <String> Schoolslist) {
        {
            mDbHelper = new DbHelper(this);

            List<String> SchoolNameslist =Schoolslist;

            // Creating adapter for spinner
            ArrayAdapter<String> SchoolSpinnerAdapter = new ArrayAdapter <String>(this,
                    android.R.layout.simple_spinner_item, SchoolNameslist);
            // Specify dropdown layout style - simple list view with 1 item per line
            SchoolSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            SchoolNameSpinner.setAdapter(SchoolSpinnerAdapter);

            // Set the integer mSelected to the constant values
            SchoolNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selection = (String) parent.getItemAtPosition(position);
                    SSchoolName = selection;
                    SetupStudent1Spinner(SSchoolName,StudentsNameslist);
                    // SetupStudent1SpinnerSelection(SSchoolName);
                    // Showing selected spinner item
                    Toast.makeText(parent.getContext(), "You selected: " + SSchoolName,
                            Toast.LENGTH_LONG).show();
                }

                // Because AdapterView is an abstract class, onNothingSelected must be defined
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    SSchoolName = null;
                }
            });
        }
    }

    private void SetupStudent1Spinner(String SSchoolName,List<CStudents> AllStudents ) {
        {

            List<String> StudentNameonly = getStudentNamelist(SSchoolName,AllStudents);
            /*/GET students information
            for (CStudents cstudents : StudentsNameslist)
            {
                if(StudentsNameslist.contains(SSchoolName))
                StudentNameonly.add(cstudents.getStudentsname());
            }
*/
            // Creating adapter for spinner
            ArrayAdapter<String> StudentAdapter = new ArrayAdapter <String>(this,
                    android.R.layout.simple_spinner_item, StudentNameonly);
            // Specify dropdown layout style - simple list view with 1 item per line
            StudentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            S1Spinner.setAdapter(StudentAdapter);
            // Set the integer mSelected to the constant values
            S1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selection = (String) parent.getItemAtPosition(position);
                    Student1Name = selection;
                    // Showing selected spinner item
                    Toast.makeText(parent.getContext(), "You selected: " + Student1Name,
                            Toast.LENGTH_LONG).show();
                }

                // Because AdapterView is an abstract class, onNothingSelected must be defined
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Student1Name = null;
                }
            });

            S2Spinner.setAdapter(StudentAdapter);
            // Set the integer mSelected to the constant values
            S2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selection = (String) parent.getItemAtPosition(position);
                    Student2Name = selection;
                    // Showing selected spinner item
                    Toast.makeText(parent.getContext(), "You selected: " + Student2Name,
                            Toast.LENGTH_LONG).show();
                }

                // Because AdapterView is an abstract class, onNothingSelected must be defined
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Student2Name = null;
                }
            });
        }
    }




    private List<String> getStudentNamelist(String SSchoolName,List<CStudents> AllStudents  ) {


        List<String> StudentNameonly = new ArrayList<String>();
        String currentname,currentSchool;

        //GET students information
        for (CStudents cstudents : AllStudents  ) {
            currentname = cstudents.getStudentsname();
            currentSchool = cstudents.getSchoolName();
            if (!StudentNameonly.contains(currentname) && (SSchoolName).equals(currentSchool)) {
                StudentNameonly.add(currentname);
            }

        }
        return StudentNameonly;
    }




}
