package com.wilki.tica.logicLayer;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;


import com.wilki.tica.R;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends BaseAdapter implements Filterable{

    private Context context;
    private List <CStudents> Studentslinst;
    private List <CStudents> StudentsFilterlinst;
    private LayoutInflater mLayoutInflater;
    private ItemFilter  studentFilter = new ItemFilter();



    public StudentAdapter(Context context, List values){

        this.Studentslinst=values;
        StudentsFilterlinst=values;
        mLayoutInflater=LayoutInflater.from(context);

    }

    public int getCount(){

        return Studentslinst.size();
    }

    public CStudents getItem(int position){

        return Studentslinst.get(position);
    }

    public long getItemId(int position)

    {return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        View updateView;
        ViewHolder viewHolder;
        if(view == null)
        {
            updateView = mLayoutInflater.inflate(R.layout.group_listitem,null);
            viewHolder = new ViewHolder();
            viewHolder.setschhol((TextView) updateView.findViewById(R.id.text_view_SCHOOL));
            viewHolder.setStname((TextView) updateView.findViewById(R.id.text_view_studentname));
            viewHolder.setgroupname((TextView) updateView.findViewById(R.id.text_view_groupname));
            updateView.setTag(viewHolder);
        }

        else
        {
            updateView=view;
            viewHolder = (ViewHolder) updateView.getTag();
        }
        final CStudents item = getItem(position);
        viewHolder.school.setText(item.getSchoolName());
        viewHolder.Stname.setText(item.getStudentsname());
        viewHolder.Groupname.setText(item.getGroupName());

       /* updateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                // Access the row position here to get the correct data item
                CStudents item = getItem(position);

                // Do what you want here...
            }
        });*/


        return updateView;
    }

    @Override
    public Filter getFilter() {
        if (studentFilter == null) {
            studentFilter = new ItemFilter();
        }
        return studentFilter;
    }

    static class ViewHolder{
         TextView Stname;
        TextView Groupname;
        TextView school;

        public TextView getStname() {
            return Stname;
        }

        public void setschhol(TextView sschool) {
            school=sschool;
        }

        public void setStname(TextView stname) {
            Stname = stname;
        }

        public void setgroupname(TextView groupname) {
            Groupname= groupname;
        }

        public TextView getGroupname() {
            return Groupname;
        }
    }




    public View getSpinnerview(int position, View convertView, ViewGroup parent) {
        TextView view = new TextView(context);
        view.setTextColor(Color.BLACK);
        view.setGravity(Gravity.CENTER);
        view.setText(Studentslinst.get(position).getSchoolName());

        return view;
    }


        //View of Spinner on dropdown Popping

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView view = new TextView(context);
        view.setTextColor(Color.BLACK);
        view.setText(Studentslinst.get(position).getSchoolName());
        view.setHeight(60);

        return view;
    }

    // InnerClass for enabling Search feature by implementing the methods

    private class ItemFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

//below checks the match for the cityId and adds to the filterlist
            String SSchoolName= constraint.toString();
            FilterResults AllGroupinfo = new FilterResults();
            ArrayList<CStudents> filterList = new ArrayList<CStudents>();
            String currentName,currentgroup,currentschool;
            int curretId;


            if (! SSchoolName.equals(null)) {
                for (CStudents cStudents : StudentsFilterlinst) {
                    curretId=cStudents.getStudentID();
                    currentschool=cStudents.getSchoolName();
                    if ( SSchoolName.equals(currentschool))
                    {
                        filterList.add(cStudents);
                    }
                }

                     AllGroupinfo.count = filterList.size();
                         AllGroupinfo.values = filterList;

            } else {

        AllGroupinfo.count = StudentsFilterlinst.size();
        AllGroupinfo.values = StudentsFilterlinst;

            }
            return AllGroupinfo;
        }



        //Publishes the matches found, i.e., the selected schoolname
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            Studentslinst = (ArrayList<CStudents>)results.values;
            notifyDataSetChanged();
        }
    }



}
