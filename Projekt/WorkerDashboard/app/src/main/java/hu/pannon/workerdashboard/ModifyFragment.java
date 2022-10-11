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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ModifyFragment extends Fragment {
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Worker worker = Worker.getInstance();
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

                    String card = "";
                    String birthdate = "";
                    String gender = "";
                    String salary = "";
                    String position = "";
                    String territory = "";
                    String address = "";

                    for(QueryDocumentSnapshot document : task.getResult())
                    {
                        workers.add(document.get("name").toString());
                        if(document.get("name").equals(workers.get(0)))
                        {
                            card = document.get("id_card_num").toString();
                            birthdate = document.get("birthdate").toString();
                            gender = document.get("gender").toString();
                            salary = document.get("salary").toString();
                            position = document.get("position").toString();
                           territory = document.get("territory").toString();
                           address = document.get("address").toString();
                        }
                        Log.e("Worker name:",document.get("name").toString());
                    }


                    //edittext_modifyidcardnum
                    //edittext_birthdate
                    //spinner_gender
                    //edittext_salary
                    //edittext_address
                    //edittext_position
                    //edittext_territory
                    EditText edittext_modifyidcardnum = (EditText) getView().findViewById(R.id.edittext_modifyidcardnum);
                    EditText edittext_birthdate = (EditText) getView().findViewById(R.id.edittext_birthdate);
                    Spinner spinner_gender = (Spinner) getView().findViewById(R.id.spinner_gender);
                    EditText edittext_salary = (EditText) getView().findViewById(R.id.edittext_salary);
                    EditText edittext_position = (EditText) getView().findViewById(R.id.edittext_position);
                    EditText edittext_territory = (EditText) getView().findViewById(R.id.edittext_territory);
                    EditText edittext_address = (EditText) getView().findViewById(R.id.edittext_address);

                    edittext_modifyidcardnum.setText(card);
                    edittext_birthdate.setText(birthdate);
                    edittext_salary.setText(salary);
                    edittext_position.setText(position);
                    edittext_territory.setText(territory);
                    edittext_address.setText(address);
                    switch(gender) {
                        case "male": {
                            spinner_gender.setSelection(0);
                        }
                        break;

                        case "female": {
                            spinner_gender.setSelection(1);

                        }
                        break;

                        case "other":
                        {
                            spinner_gender.setSelection(2);

                        }break;

                    }


                    String[] workerArray = new String[workers.size()];
                    for(int i = 0; i < workers.size(); i++)
                    {
                        workerArray[i] = workers.get(i);                    }
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

                            if (addressList != null && addressList.size() != 0) {
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


        ((Spinner)getView().findViewById(R.id.spinner_name)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                DatabaseLoader.getInstance().database.collection("Worker").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult())
                            {
                                if(document.get("name").equals(((Spinner)getView().findViewById(R.id.spinner_name)).getSelectedItem().toString()))
                                {
                                    worker.setName(document.get("name").toString());
                                    worker.setBirthdate(document.get("birthdate").toString());
                                    worker.setGender(document.get("gender").toString());
                                    worker.setIdCardNum(document.get("id_card_num").toString());
                                    worker.setPosition(document.get("position").toString());
                                    worker.setTerritory(document.get("territory").toString());
                                    worker.setSalary(Integer.valueOf(document.get("salary").toString()));
                                    worker.setAddress(document.get("address").toString());

                                }
                            }

                            //edittext_modifyidcardnum
                            //edittext_birthdate
                            //spinner_gender
                            //edittext_salary
                            //edittext_position
                            //edittext_territory
                            EditText edittext_modifyidcardnum = (EditText) getView().findViewById(R.id.edittext_modifyidcardnum);
                            EditText edittext_birthdate = (EditText) getView().findViewById(R.id.edittext_birthdate);
                            Spinner spinner_gender = (Spinner) getView().findViewById(R.id.spinner_gender);
                            EditText edittext_salary = (EditText) getView().findViewById(R.id.edittext_salary);
                            EditText edittext_position = (EditText) getView().findViewById(R.id.edittext_position);
                            EditText edittext_territory = (EditText) getView().findViewById(R.id.edittext_territory);
                            EditText edittext_address = (EditText) getView().findViewById(R.id.edittext_address);

                            edittext_modifyidcardnum.setText(worker.getIdCardNum());
                            edittext_birthdate.setText(worker.getBirthdate());
                            edittext_position.setText(worker.getPosition());
                            edittext_salary.setText(Integer.toString(worker.getSalary()));
                            edittext_territory.setText(worker.getTerritory());
                            edittext_address.setText(worker.getAddress());


                            switch(worker.getGender()) {
                                case "male": {
                                    spinner_gender.setSelection(0);
                                }
                                break;

                                case "female": {
                                    spinner_gender.setSelection(1);

                                }
                                break;

                                case "other":
                                {
                                    spinner_gender.setSelection(2);

                                }break;

                            }


                        }
                        else
                        {
                            Log.d("db_error_tag", "Adatbázis beolvasási hiba: getWorkerNames");
                        }
                    }

                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
                EditText edittext_modifyidcardnum = (EditText) getView().findViewById(R.id.edittext_modifyidcardnum);
                EditText edittext_birthdate = (EditText) getView().findViewById(R.id.edittext_birthdate);
                EditText edittext_salary = (EditText) getView().findViewById(R.id.edittext_salary);
                EditText edittext_position = (EditText) getView().findViewById(R.id.edittext_position);
                EditText edittext_territory = (EditText) getView().findViewById(R.id.edittext_territory);
                EditText edittext_address = (EditText) getView().findViewById(R.id.edittext_address);

                if(edittext_address.getText().toString().isEmpty()) {
                    edittext_address.setError("Kérem töltse ki a mezőt!");
                }
                else if(edittext_address.getText().toString().split(",").length != 2)
                {
                    edittext_address.setError("Kérem töltse ki a mezőt a következő formában: Város, utca házszám.");
                }
                else if(edittext_birthdate.getText().toString().isEmpty())
                {
                    edittext_birthdate.setError("Kérem töltse ki a mezőt!");
                }
                else if(edittext_modifyidcardnum.getText().toString().isEmpty())
                {
                    edittext_modifyidcardnum.setError("Kérem töltse ki a mezőt!");
                }
                else if(edittext_salary.getText().toString().isEmpty())
                {
                    edittext_salary.setError("Kérem töltse ki a mezőt!");
                }
                else if(edittext_position.getText().toString().isEmpty())
                {
                    edittext_position.setError("Kérem töltse ki a mezőt!");
                }
                else if(edittext_territory.getText().toString().isEmpty())
                {
                    edittext_territory.setError("Kérem töltse ki a mezőt!");
                }
                else
                {
                    //Updating actual firestore data
                    DatabaseLoader.getInstance().database.collection("Worker").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                String documentId = "";
                                for(QueryDocumentSnapshot document : task.getResult())
                                {
                                    if(document.get("name").equals(worker.getName()))
                                        documentId = document.getId();
                                    Log.e("Worker name:",document.get("name").toString());
                                }

                                Map<String, Object> workerMap = new HashMap<>();
                                workerMap.put("name", worker.getName());
                                workerMap.put("address", worker.getAddress());
                                workerMap.put("birthdate", worker.getBirthdate());
                                workerMap.put("id_card_num", worker.getIdCardNum());
                                workerMap.put("gender", worker.getGender());
                                workerMap.put("salary", worker.getSalary());
                                workerMap.put("territory", worker.getTerritory());
                                workerMap.put("position", worker.getPosition());
                                Log.d("document_id_tag", "DOCUMENT_ID: " + documentId);
                                DatabaseLoader.getInstance().database.collection("Worker").document(documentId).update(workerMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Fragment fragment = new HomeFragment();
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.fragmentHolder, fragment);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                        Toast.makeText(getContext(), "Sikeres módosítás!", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }
                            else
                            {
                                Log.d("db_error_tag", "Adatbázis beolvasási hiba: getWorkerNames");
                            }
                        }

                    });


                }


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