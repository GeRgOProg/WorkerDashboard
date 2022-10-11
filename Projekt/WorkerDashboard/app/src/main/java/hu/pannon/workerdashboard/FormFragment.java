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
    //Google api key for a google map input field. We couldn't make it because it costs like 300$ / 9 months..
   // private String apiKey = "AIzaSyB7vKjtMe8lB1-wgbiUijvyLw8qLSYbapk";
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
        //Setting up user interface
       EditText salary = (EditText)(getView().findViewById(R.id.edittext_hiresalary));
       salary.setText("0");
       EditText hireAddress = (EditText)(getView().findViewById(R.id.edittext_hireaddress));

        String[] arraySpinner = new String[] {"Férfi", "Nő", "Egyéb"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner)getView().findViewById(R.id.spinner_hiregender)).setAdapter(adapter);

        //If address input field has changed, event trigger will fire
        ((EditText)(getView().findViewById(R.id.edittext_hireaddress))).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //This is not needed at all..
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Getting city from input [Address = City, everything else]
                String address = charSequence.toString();
                if(address.indexOf(",") != -1)
                {
                    String[] addressSnippets = address.split(",");

                     String new_city = addressSnippets[0];
                     //If the city has changed, calculate the distance
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
                        //Travelling support estimated by 15 Ft/km.
                        travellingSupportGui.setText("Utazási támogatás mértéke: " + String.valueOf(supportFee) + " Ft (" + String.valueOf(Math.round(kilometers)) + " km) ");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //This is not needed at all..
            }
        });

        //DatePicker
        ((EditText)(getView().findViewById(R.id.edittext_hirebirthdate))).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                //Only open calculator if the input field has focus, otherwise it will look funny..
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
                EditText hireName = (EditText)currentView.findViewById(R.id.edittext_hirename);
                EditText hireBirthDate = (EditText)currentView.findViewById(R.id.edittext_hirebirthdate);
                EditText hireAddress = (EditText)currentView.findViewById(R.id.edittext_hireaddress);
                EditText hireIdNum = (EditText)currentView.findViewById(R.id.edittext_hireidnum);
                EditText hireSalary = (EditText)currentView.findViewById(R.id.edittext_hiresalary);
                EditText hirePosition = (EditText)currentView.findViewById(R.id.edittext_hireposition);
                EditText hireTerritory = (EditText)currentView.findViewById(R.id.edittext_hireterritory);

                if(hireName.getText().toString().isEmpty())
                {
                    hireName.setError("Kérem töltse ki a mezőt!");
                }
                else if(hireBirthDate.getText().toString().isEmpty())
                {
                    hireBirthDate.setError("Kérem töltse ki a mezőt!");

                }
                else if(hireAddress.getText().toString().isEmpty())
                {
                    hireAddress.setError("Kérem töltse ki a mezőt!");

                }
                else if(hireAddress.getText().toString().split(",").length != 2)
                {
                    hireAddress.setError("Kérem töltse ki a mezőt a következő forma szerint: Város, Utca házszám.");

                }
                else if(hireIdNum.getText().toString().isEmpty())
                {
                    hireIdNum.setError("Kérem töltse ki a mezőt!");

                }
                else if(hireSalary.getText().toString().isEmpty())
                {
                    hireSalary.setError("Kérem töltse ki a mezőt!");

                }
                else if(hirePosition.getText().toString().isEmpty())
                {
                    hirePosition.setError("Kérem töltse ki a mezőt!");
                }
                else if(hireTerritory.getText().toString().isEmpty())
                {
                    hireTerritory.setError("Kérem töltse ki a mezőt!");

                }
                else
                {
                    //Worker initiation
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


                    //Fragment transaction
                    Fragment fragment = new GroupFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentHolder, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }


            }
        });

        ////Beta code snippet, didn't work..
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

    //Function for distance calculation
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