package hu.pannon.workerdashboard;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseLoader {

    private static DatabaseLoader instance = null;
    public FirebaseFirestore database;

    private DatabaseLoader()
    {

    }

    public static DatabaseLoader getInstance()
    {
        if(instance == null)
            instance = new DatabaseLoader();
        return instance;
    }

    public void setDatabase(FirebaseFirestore database) {
        this.database = database;
    }

    //Ez működik.
    public boolean insertWorker(Worker worker)
    {

        Map<String, Object> workerMap = new HashMap<>();
        workerMap.put("name", worker.getName());
        workerMap.put("address", worker.getAddress());
        workerMap.put("birthdate", worker.getBirthdate());
        workerMap.put("id_card_num", worker.getIdCardNum());
        workerMap.put("gender", worker.getGender());
        workerMap.put("salary", worker.getSalary());
        workerMap.put("territory", worker.getTerritory());
        workerMap.put("position", worker.getPosition());

        final boolean[] result = {false};
        this.database.collection("Worker").add(workerMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        result[0] = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        result[0] = false;
                    }
                });

        return result[0];

    }

/*
    //Nem jó, ne is próbáld meghívni.
    public ArrayList<String> asyncWorkerGetter()
    {
        ArrayList<String> workers = new ArrayList<>();
        database.collection("Worker").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task.getResult())
                    {
                        workers.add(document.get("name").toString());
                        Log.e("Worker name:",document.get("name").toString());
                    }

                }
                else
                {
                    Log.d("db_error_tag", "Adatbázis beolvasási hiba: getWorkerNames");
                }
            }

        });



        return workers;
    }
*/


}
