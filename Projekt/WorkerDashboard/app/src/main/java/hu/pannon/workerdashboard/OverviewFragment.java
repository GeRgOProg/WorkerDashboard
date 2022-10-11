package hu.pannon.workerdashboard;

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


public class OverviewFragment extends Fragment {

private Worker worker = Worker.getInstance();
//private DatabaseLoader dbLoader = DatabaseLoader.getInstance();
    public OverviewFragment() {
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
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Setting up user interface
        View currentView = getView();
        ((EditText)currentView.findViewById(R.id.edittext_overviewname)).setText(worker.getName());
        ((EditText)currentView.findViewById(R.id.edittext_overviewaddress)).setText(worker.getAddress());
        ((EditText)currentView.findViewById(R.id.edittext_overviewbirthdate)).setText(worker.getBirthdate());
        ((EditText)currentView.findViewById(R.id.edittext_overviewidnum)).setText(worker.getIdCardNum());
        ((EditText)currentView.findViewById(R.id.edittext_overviewgender)).setText(worker.getGender());
        ((EditText)currentView.findViewById(R.id.edittext_overviewsalary)).setText(String.valueOf(worker.getSalary()));
        ((EditText)currentView.findViewById(R.id.edittext_overviewterritory)).setText(worker.getTerritory());
        ((EditText)currentView.findViewById(R.id.edittext_overviewposition)).setText(worker.getPosition());


        //Back
        getView().findViewById(R.id.btn_overviewprev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new PrivilegeFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentHolder, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        //Next
        getView().findViewById(R.id.btn_overviewnext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  dbLoader.insertWorker(worker);

                Fragment fragment = new PhotoFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentHolder, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }
}