package hu.pannon.workerdashboard;

import static android.content.Context.SENSOR_SERVICE;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginFragment extends Fragment {

    public User user = User.getInstance();

    public LoginFragment() {
    }
    private Button button;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Burnt in login credentials for testing
        ((EditText)(getView().findViewById(R.id.edittext_username))).setText("admin");
        ((EditText)(getView().findViewById(R.id.edittext_password))).setText("admin");

        //Login section
        getView().findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = ((EditText)(getView().findViewById(R.id.edittext_username))).getText().toString();
                String password = ((EditText)(getView().findViewById(R.id.edittext_password))).getText().toString();
              //  Log.d("db_success_tag", "[WORKERRESULT]: Felhasz: " + username + "jelszó: " + password);


                if(username.isEmpty())
                {
                    ((EditText)(getView().findViewById(R.id.edittext_username))).setError("Kérem töltse ki a mezőt!");
                }
                else if (password.isEmpty())
                {
                    ((EditText)(getView().findViewById(R.id.edittext_password))).setError("Kérem töltse ki a mezőt!");
                }
                else {
                    final boolean[] allGood = {false, false}; //Two conditions for successful login: Password is correct, User exists
                    DatabaseLoader.getInstance().database.collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                for(QueryDocumentSnapshot document : task.getResult())
                                {
                                   // Log.d("db_success_tag", "[WORKERRESULT]: Felhasz: " + document.get("username").toString() + "jelszó: " + document.get("password").toString());

                                    if(document.get("username").toString().equals(username))
                                    {
                                        if(document.get("password").toString().equals(password))
                                        {
                                            allGood[0] = true; //Password is correct
                                            allGood[1] = true; //User exists
                                            user.setUsername(username);
                                            user.setPassword(password);
                                        }
                                        else
                                        {
                                            allGood[1] = true; //User exists
                                            ((EditText)(getView().findViewById(R.id.edittext_password))).setError("Rossz jelszót adott meg!\nKérem próbálja újra!");
                                        }
                                    }
                                }
                                if(allGood[0]) {
                                    Fragment fragment = new HomeFragment();
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.fragmentHolder, fragment);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                }
                                else
                                {
                                    if(allGood[1] == false)
                                        ((EditText)(getView().findViewById(R.id.edittext_username))).setError("Ilyen felhasználó nem létezik!");
                                }
                            }
                            else
                            {
                                Log.d("db_error_tag", "Adatbázis beolvasási hiba: Login");
                            }
                        }

                    });


                }
            }
        });
    }

}