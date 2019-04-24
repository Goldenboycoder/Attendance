package com.example.attendance;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StudentProfile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StudentProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentProfile extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    TextView name;
    TextView id;

    public final String Preference="privateS";
    public final String Student_Name="sname";
    public final String Student_ID="S_ID";



    public StudentProfile() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static StudentProfile newInstance() {
        StudentProfile fragment = new StudentProfile();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_profile, container, false);
        name=v.findViewById(R.id.editStudentName);
        id=v.findViewById(R.id.editStudentID);
        Button done=v.findViewById(R.id.btnDone);
        done.setOnClickListener(this);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnDone:
                SharedPreferences sharedPreferences=getActivity().getSharedPreferences(Preference, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(Student_Name,name.getText().toString());
                editor.putString(Student_ID,id.getText().toString());
                editor.commit();
                Toast.makeText(getActivity(),"Profile Created",Toast.LENGTH_SHORT).show();
                break;
                default:
                    Toast.makeText(getContext(),"onclick error",Toast.LENGTH_SHORT).show();
        }
    }

    /*public void done(View v){
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences(Preference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(Student_Name,name.getText().toString());
        editor.putString(Student_ID,id.getText().toString());
        editor.commit();
        Toast.makeText(getActivity(),"Profile Created",Toast.LENGTH_SHORT).show();
    }*/
}
