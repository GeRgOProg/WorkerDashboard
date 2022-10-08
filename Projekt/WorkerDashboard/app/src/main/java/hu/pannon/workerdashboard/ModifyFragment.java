package hu.pannon.workerdashboard;

import android.app.DatePickerDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ModifyFragment extends Fragment {
    private int mYear, mMonth, mDay, mHour, mMinute;

    double lat1 = 0, long1 = 0, lat2 = 0, long2 = 0;
    int flag = 0;
    String sType;
    String city="";

    public ModifyFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_modify, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<String> workers = new ArrayList<>();
        DatabaseLoader.getInstance().database.collection("Worker").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task.getResult())
                    {
                        workers.add(document.get("name").toString());
                        Log.e("Worker name:",document.get("name").toString());
                    }

                    String[] workerArray = new String[workers.size()];
                    for(int i = 0; i < workers.size(); i++)
                    {
                        workerArray[i] = workers.get(i);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, workerArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    ((Spinner)getView().findViewById(R.id.spinner_name)).setAdapter(adapter);

                    String[] arraySpinner = new String[] {"Férfi", "Nő", "Egyéb"};
                    ArrayAdapter<String> Arradapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, arraySpinner);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    ((Spinner)getView().findViewById(R.id.spinner_gender)).setAdapter(Arradapter);
                }
                else
                {
                    Log.d("db_error_tag", "Adatbázis beolvasási hiba: getWorkerNames");
                }
            }

        });

        ((EditText)(getView().findViewById(R.id.edittext_address))).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String address = charSequence.toString();
                if(address.indexOf(",") != -1)
                {
                    String[] addressSnippets = address.split(",");

                    String new_city = addressSnippets[0];
                    if(!new_city.equals(city)) {
                        city = new_city;
                        Geocoder geocoder = new Geocoder(getContext());
                        List<Address> addressList;

                        try {
                            addressList = geocoder.getFromLocationName(city, 1);

                            if (addressList != null) {
                                lat1 = addressList.get(0).getLatitude();
                                long1 = addressList.get(0).getLongitude();


                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        List<Address> addressList2;
                        try {
                            addressList2 = geocoder.getFromLocationName("Veszprém", 1);

                            if (addressList2 != null) {
                                lat2 = addressList2.get(0).getLatitude();
                                long2 = addressList2.get(0).getLongitude();


                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        double kilometers = distance(lat1, lat2, long1, long2);
                        double supportFee = Math.round(kilometers * 15);
                        TextView travellingSupportGui = ((TextView) (getView().findViewById(R.id.textview_modifytravellingsupport)));
                        travellingSupportGui.setText("Utazási támogatás mértéke: " + String.valueOf(supportFee) + " Ft (" + String.valueOf(Math.round(kilometers)) + " km) ");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        ((EditText)(getView().findViewById(R.id.edittext_birthdate))).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {


                if(((EditText)(getView().findViewById(R.id.edittext_birthdate))).hasFocus()) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


                    // Get Current Date
                    final Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);


                    DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {

                                    ((EditText) (getView().findViewById(R.id.edittext_birthdate))).setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                }
                            }, mYear, mMonth, mDay);

                    datePickerDialog.show();



                }
            }
        });


        //Back
        getView().findViewById(R.id.btn_prevmodi).setOnClickListener(new View.OnClickListener() {
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

        //Next
        getView().findViewById(R.id.btn_nextmodi).setOnClickListener(new View.OnClickListener() {
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
    }


    public double distance(double lat1, double lat2, double lon1, double lon2)
    {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return(c * r);
    }
}