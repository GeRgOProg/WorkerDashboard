package hu.pannon.workerdashboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class FireFragment extends Fragment {

    private String currentWorker="";


    public FireFragment() {
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
        return inflater.inflate(R.layout.fragment_fire, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get worker names
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
                    ListView simpleList = (ListView) getView().findViewById(R.id.listview_workerfire);
                    String[] list=new String[workers.size()];
                    for(int i=0;i<workers.size();i++){
                        list[i]=workers.get(i);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list);
                    simpleList.setAdapter(arrayAdapter);
                }
                else
                {
                    Log.d("db_error_tag", "Adatbázis beolvasási hiba: getWorkerNames");
                }
            }

        });

        //Change: Copied setOnItemSelected body into setOnItemClickListener body. Now it should work correctly. 2022-10-18, Keresztúri Gergő
        //Item selected on ListView -> Setting current worker's name to variable
        ((ListView)(getView().findViewById(R.id.listview_workerfire))).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListView simpleList = (ListView) getView().findViewById(R.id.listview_workerfire);
                currentWorker= simpleList.getItemAtPosition(i).toString();
                Log.d("success_tag", "Currentworker: "+currentWorker);
                Toast.makeText(getContext(),currentWorker+" kiválasztva!",Toast.LENGTH_SHORT).show();
            }
        });


        //Back
        getView().findViewById(R.id.btn_fireprev).setOnClickListener(new View.OnClickListener() {
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
        getView().findViewById(R.id.btn_firenext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!currentWorker.isEmpty()) {

                    DatabaseLoader.getInstance().database.collection("Worker").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                for(QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.get("name").equals(currentWorker)) {
                                        //Assuming there are unique names in the db, we delete on match..
                                        DatabaseLoader.getInstance().database.collection("Worker").document(document.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("DB_success_tag", "DocumentSnapshot successfully deleted!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("DB_error_tag", "Error deleting document", e);
                                                    }
                                                });
                                    }
                                }
                            }
                            else
                            {
                                Log.d("db_error_tag", "Adatbázis beolvasási hiba: getWorkerNames");
                            }
                        }

                    });

                    //After deleting, move back to the home fragment
                    Fragment fragment = new HomeFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentHolder, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                else{
                    //ListView simpleList = (ListView) getView().findViewById(R.id.listview_workerfire);
                    //If no worker was selected.. notify the user
                    Toast.makeText(getContext(), "Nem választott ki munkást!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}