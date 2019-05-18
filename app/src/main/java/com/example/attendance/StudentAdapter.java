package com.example.attendance;

import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.MyViewHolder> {
    private List<Student> studentList;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.S_ID);
            genre = (TextView) view.findViewById(R.id.genre);
            //year = (TextView) view.findViewById(R.id.year);
        }
    }
    public StudentAdapter(List<Student> studentList) {
        this.studentList = studentList;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_list_row, parent, false);

        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.title.setText(student.getName());
        holder.genre.setText(student.getId());
        //holder.year.setText(student.getYear());
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }
}
