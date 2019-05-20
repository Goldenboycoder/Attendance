package com.example.attendance;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.MyViewHolder> {
    private ArrayList<Course> courses;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView id, name, deleteBtn;
        private View frontLayout;

        public MyViewHolder(View view) {
            super(view);
            id = view.findViewById(R.id.item_number);
            name = view.findViewById(R.id.content);
            frontLayout = itemView.findViewById(R.id.front_layout);
            deleteBtn = view.findViewById(R.id.Delete);
        }


        //Bind on click listeners to each item
        public void bind() {
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("courses").child(courses.get(getAdapterPosition()).getId());
                    DatabaseReference sDatabase = FirebaseDatabase.getInstance().getReference().child("records").child(courses.get(getAdapterPosition()).getId());
                    mDatabase.removeValue();
                    sDatabase.removeValue();
                    notifyDataSetChanged();
                }
            });

            frontLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, "ooj", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    public CourseAdapter(Context context, ArrayList<Course> courses) {
        viewBinderHelper.setOpenOnlyOne(true);
        mContext = context;
        this.courses = courses;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_course_management, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.id.setText(course.getId());
        holder.name.setText(course.getName());
        //Call binding function
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }
}
