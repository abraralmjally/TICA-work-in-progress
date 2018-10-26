package com.wilki.tica.activities;


import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wilki.tica.dataLayer.DbContract;
import com.wilki.tica.dataLayer.DbHelper;
import com.wilki.tica.exceptions.InstructionsRequiredException;
import com.wilki.tica.exceptions.MissingDatatException;
import com.wilki.tica.fragments.MessageFragment;
import com.wilki.tica.logicLayer.CStudents;
import com.wilki.tica.R;
import com.wilki.tica.logicLayer.StudentAdapter;
import com.wilki.tica.logicLayer.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.List;

public class GroupSelectorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public List<CStudents> Studentslist = new ArrayList<CStudents>();
    private int selectedGroup;
    private String SSchoolName ;
    private List<String> AllSchoolNames = new ArrayList<>();
    private Spinner SchoolNameSpinner;
    private DbHelper mDbHelper = new DbHelper(this);
    ArrayAdapter<CStudents> GroupAdapter;
    private StudentAdapter adapter;
    private String SelectedGroupname;
    String SelectedGroupName,SelectedStudentName;
    String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Backup/Students.csv";



    ListView Grouplist ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_selector);
        TextView title = (TextView) findViewById(R.id.select_a_group);
        Typeface customFont = Typeface.createFromAsset(getAssets(), getString(R.string.font_path));

        title.setTypeface(customFont);
        SchoolNameSpinner = (Spinner) findViewById(R.id.spinner_school_selector);
         Grouplist = (ListView) findViewById(R.id.grouplist);
         //TODO after the group is created a new listed should be sent to this page

        Studentslist= mDbHelper.fetchAllStudents();
        if(Studentslist== null) {
            try {
                insertStudent( findViewById(R.id.activity_group_selector));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        AllSchoolNames=getSSchoolNames(Studentslist);
        SetupSchoolSpinner();
        adapter = new StudentAdapter(this, Studentslist);
        Grouplist.setAdapter(adapter);
        Grouplist.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,
                                            int position, long id) {

                        // We know the View is a <extView so we can cast it
                        TextView clickedGView = (TextView) view.findViewById(R.id.text_view_groupname);
                        TextView clickedStudentView = (TextView) view.findViewById(R.id.text_view_studentname);
                         SelectedGroupName= clickedGView.getText().toString();
                         SelectedStudentName= clickedStudentView.getText().toString();
                        Toast.makeText(GroupSelectorActivity.this, "Item with id ["+id+"] - Position ["+position+"] - Student Name ["+SelectedStudentName+"]- Group Name ["+SelectedGroupName+"]", Toast.LENGTH_SHORT).show();

                    }
                }
        );

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String schholname = (String) parent.getItemAtPosition(position);
        SSchoolName = schholname;
        Toast.makeText(parent.getContext(), "You selected: " + SSchoolName,
                Toast.LENGTH_LONG).show();
//Here we use the Filtering Feature which we implemented in our Adapter class.
        adapter.getFilter().filter(schholname,new Filter.FilterListener() {
            @Override
            public void onFilterComplete(int count) {

            }
        });

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void CreateNewGroup(View  view){
        Intent toLaunch = new Intent(getApplicationContext(), CreateNewGroupActivity.class);
        toLaunch.putExtra("SelectedSchool", SSchoolName);
        toLaunch.putExtra("Studentslist",(Serializable) Studentslist);
        toLaunch.putExtra("Schoolslist",(Serializable) AllSchoolNames);
        startActivity(toLaunch);
    }


    public void taskMenu(View  view){

        try {
            Intent toLaunch = new Intent(getApplicationContext(), MainMenuActivity.class);
                toLaunch.putExtra("SelectedGroup", SelectedGroupName);
                startActivity(toLaunch);
            if (SelectedGroupName== null){
                throw new MissingDatatException("Please Selected Group name") ;}
        } catch (MissingDatatException  e)

        {
            makeMessageDialogue(e.getMessage());
        }

    }


    /*
     * Shows a fragment with the message provided as an argument.
     */
    private void makeMessageDialogue(String message){
        MessageFragment messageFragment = new MessageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        messageFragment.setArguments(bundle);
        messageFragment.show(getFragmentManager(), "message dialogue");
    }



    private void SetupSchoolSpinner() {
        {
            List<String> SchoolNameslist = getSSchoolNames(Studentslist);
            // Creating adapter for spinner
            ArrayAdapter<String> SchoolSpinnerAdapter = new ArrayAdapter <String>(this,
                    android.R.layout.simple_spinner_item, SchoolNameslist);
            // Specify dropdown layout style - simple list view with 1 item per line
            SchoolSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SchoolNameSpinner.setAdapter(SchoolSpinnerAdapter);
            SchoolNameSpinner.setOnItemSelectedListener(this);

        }
    }

    private List <String> getSSchoolNames  (List<CStudents> AllStudents ) {
        List<String> AllSchoolNames = new ArrayList<>();
        String currentschool;

        for (CStudents cStudents : AllStudents) {
            currentschool = cStudents.getSchoolName();
            if (!AllSchoolNames.contains(currentschool)) {
                AllSchoolNames.add(currentschool);
            }

        }

        return AllSchoolNames;
    }


    private void insertStudent(final View view) throws IOException {


        mDbHelper = new DbHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        File file = new File(directory_path);
        if (!file.exists()) {
            Utils.showSnackBar(view, "No file");
            return;
        }

        BufferedReader buffer = new BufferedReader(new FileReader(file));
        String line = "";
        String tableName = DbContract.Students.TABLE_NAME;
        String columns = DbContract.Students.COLUMN_NAME_GROUP_NAME + "," + DbContract.Students.COLUMN_NAME_GENDER + "," + DbContract.Students.COLUMN_NAME_SCHOOL + ",";
        String str1 = "INSERT INTO " + tableName + " (" + columns + ") values(";
        String str2 = ");";

        db.beginTransaction();
        try {
            while ((line = buffer.readLine()) != null) {
                String[] colums = line.split(",");
                if (colums.length != 3) {
                    Log.d("CSVParser", "Skipping Bad CSV Row");
                    continue;
                }
                ContentValues values = new ContentValues(3);
                values.put(DbContract.Students.COLUMN_NAME_STUDENT, colums[0].trim());
                values.put(DbContract.Students.COLUMN_NAME_GENDER, colums[1].trim());
                values.put(DbContract.Students.COLUMN_NAME_SCHOOL, colums[2].trim());
                // values.put(DbContract.Students.COLUMN_NAME_GROUP_NAME, colums[3].trim());

                db.insert(tableName, null, values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        db.setTransactionSuccessful();
        Utils.showSnackBar(view, "Excel imported into " + tableName);

        db.endTransaction();

    }
}


