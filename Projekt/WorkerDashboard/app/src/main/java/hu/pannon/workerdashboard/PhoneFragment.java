package hu.pannon.workerdashboard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


public class PhoneFragment extends Fragment {




    public PhoneFragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Homepage
        getView().findViewById(R.id.btn_phonenext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new HomeFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentHolder, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        //Dial
        getView().findViewById(R.id.btn_dial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phonenumber = ((EditText)(getView().findViewById(R.id.edittext_phonenumber))).getText().toString();
                if(phonenumber.length() == 11 || phonenumber.length() == 12)
                {
                    if (getContext().checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 100);
                    }
                    else {
                        Uri uri = Uri.parse("tel:" + phonenumber);
                        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                        startActivity(intent);
                        try {

                        } catch (SecurityException e) {
                            Toast.makeText(getContext(), "Hiba történt!", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                else
                {
                    ((EditText)(getView().findViewById(R.id.edittext_phonenumber))).setError("Hibás telefonszám formátum!\nKérem próbálja újra!");
                }
            }
        });


        //Call
        getView().findViewById(R.id.btn_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phonenumber = ((EditText)(getView().findViewById(R.id.edittext_phonenumber))).getText().toString();
                if(phonenumber.length() == 11 || phonenumber.length() == 12)
                {
                    if (getContext().checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 100);
                    }
                    else {
                        Uri uri = Uri.parse("tel:" + phonenumber);
                        Intent intent = new Intent(Intent.ACTION_CALL, uri);
                        startActivity(intent);

                        try {

                        } catch (SecurityException e) {
                            Toast.makeText(getContext(), "Hiba történt!", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                else
                {
                    ((EditText)(getView().findViewById(R.id.edittext_phonenumber))).setError("Hibás telefonszám formátum!\nKérem próbálja újra!");
                }

            }
        });
    }
}