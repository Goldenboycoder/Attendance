package com.example.attendance;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LogoutScreen.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LogoutScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogoutScreen extends Fragment implements View.OnClickListener {





    public LogoutScreen() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static LogoutScreen newInstance() {
        LogoutScreen fragment = new LogoutScreen();

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
        View v= inflater.inflate(R.layout.fragment_logout_screen, container, false);
        Button exit=v.findViewById(R.id.btnExit);
        exit.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnExit:
                Intent intent=new Intent(getActivity(),MainActivity.class);
                startActivity(intent);
                break;
                default:
                    Toast.makeText(getContext(),"fragment onclick error",Toast.LENGTH_SHORT).show();
        }
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

  /*  public void exitToLogin(View v){
        Intent intent=new Intent(getActivity(),MainActivity.class);
        startActivity(intent);
    }*/
}
