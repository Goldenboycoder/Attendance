package com.example.attendance;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.MyViewHolder> {
    private List<Student> studentList;
    private List<Boolean> attended;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    String c;
    String s;
    String d;
    Logs ml;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, id, status, changeStatus;
        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.s_name);
            id = view.findViewById(R.id.s_id);
            status = view.findViewById(R.id.status);
            changeStatus = view.findViewById(R.id.ChangeStatus);
        }

        //Bind on click listeners to each item
        public void bind() {
            changeStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("records").child(c).child(s).child(d).child(studentList.get(getAdapterPosition()).getId());
                    attended.set(getAdapterPosition(), !(attended.get(getAdapterPosition())) );
                    mDatabase.setValue(attended.get(getAdapterPosition()));
                    notifyItemChanged(getAdapterPosition());
                    ml.setAbsenses();
                }
            });
        }
    }
    /*
    * TODO Bug the 2 list are changing when filtered we need to connect attended and students somehow so that when student att pos 3 becomes at pos 1 after filtering his attanded value which also is at pos 1 now need to go back to the old postion 3
    *
    * */

    public StudentAdapter(List<Student> studentList, List<Boolean> attended, String c, String s, String d, Logs ml) {
        viewBinderHelper.setOpenOnlyOne(true);
        this.studentList = studentList;
        this.attended = attended;
        this.c = c;
        this.s = s;
        this.d = d;
        this.ml = ml;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Student student = studentList.get(position);
        Boolean bool = attended.get(position);
        holder.name.setText(student.getName());
        holder.id.setText(student.getId());
        holder.status.setText(bool ? "" : "Absent");
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public void filterList(List<Student> filteredList,List<Boolean>filteredAttended){
        studentList=filteredList;
        attended=filteredAttended;
        notifyDataSetChanged();
    }
    public void updateAttended(int position,boolean attended){
        this.attended.set(position,attended);
        notifyItemChanged(position);
    }
}
