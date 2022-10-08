package hu.pannon.workerdashboard;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class FormFragment extends Fragment {

    private Worker worker = Worker.getInstance();
    private String apiKey = "AIzaSyB7vKjtMe8lB1-wgbiUijvyLw8qLSYbapk";
    private int mYear, mMonth, mDay, mHour, mMinute;

    double lat1 = 0, long1 = 0, lat2 = 0, long2 = 0;
    int flag = 0;
    String sType;
    String city="";

    public FormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       EditText salary = (EditText)(getView().findViewById(R.id.edittext_hiresalary));
       salary.setText("0");
       EditText hireAddress = (EditText)(getView().findViewById(R.id.edittext_hireaddress));

        String[] arraySpinner = new String[] {"Férfi", "Nő", "Egyéb"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner)getView().findViewById(R.id.spinner_hiregender)).setAdapter(adapter);

        ((EditText)(getView().findViewById(R.id.edittext_hireaddress))).addTextChangedListener(new TextWatcher() {
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
                        TextView travellingSupportGui = ((TextView) (getView().findViewById(R.id.textview_travellingsupport)));
                        travellingSupportGui.setText("Utazási támogatás mértéke: " + String.valueOf(supportFee) + " Ft (" + String.valueOf(Math.round(kilometers)) + " km) ");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ((EditText)(getView().findViewById(R.id.edittext_hirebirthdate))).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {


                if(((EditText)(getView().findViewById(R.id.edittext_hirebirthdate))).hasFocus()) {
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

                                    ((EditText) (getView().findViewById(R.id.edittext_hirebirthdate))).setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                }
                            }, mYear, mMonth, mDay);

                    datePickerDialog.show();
                }
            }
        });



        //Back
        getView().findViewById(R.id.btn_hireprev).setOnClickListener(new View.OnClickListener() {
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
        getView().findViewById(R.id.btn_hirenext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View currentView = getView();

                worker.setName(((EditText) currentView.findViewById(R.id.edittext_hirename)).getText().toString());
                worker.setAddress(((EditText) currentView.findViewById(R.id.edittext_hireaddress)).getText().toString());
                worker.setBirthdate(((EditText) currentView.findViewById(R.id.edittext_hirebirthdate)).getText().toString());
                worker.setIdCardNum(((EditText) currentView.findViewById(R.id.edittext_hireidnum)).getText().toString());
                String selectedGender = ((Spinner) currentView.findViewById(R.id.spinner_hiregender)).getSelectedItem().toString();
                switch(selectedGender)
                {
                    case "Férfi":
                    {
                        selectedGender = "Male";
                    }break;

                    case "Nő":
                    {
                        selectedGender = "Female";
                    }break;

                    case "Egyéb": {
                        selectedGender = "Other";
                    }break;

                    default:
                    {
                        selectedGender = "Other";
                    }break;
                }
                worker.setGender(selectedGender);
                worker.setSalary(Integer.valueOf(((EditText) currentView.findViewById(R.id.edittext_hiresalary)).getText().toString()));
                worker.setTerritory(((EditText) currentView.findViewById(R.id.edittext_hireterritory)).getText().toString());
                worker.setPosition(((EditText) currentView.findViewById(R.id.edittext_hireposition)).getText().toString());



                Fragment fragment = new GroupFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentHolder, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        //Places.initialize(getContext(), apiKey);

       // hireAddress.setFocusable(false);
       // hireAddress.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        sType = "source";

       //         List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG);
        //        Intent intent = new Autocomplete.IntentBuilder(
        //                AutocompleteActivityMode.OVERLAY,fields
          //      ).build(getContext());

            //    startActivityForResult(intent, 100);

        //    }
       // });


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