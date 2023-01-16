package it.itsar.tomeandroid;

import static android.content.ContentValues.TAG;
import static it.itsar.tomeandroid.LeggiScrivi.leggiLocale;
import static it.itsar.tomeandroid.LeggiScrivi.scriviLocale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private boolean isLogged = false;

    private Button loginLogout;
    private ListView listaStorie;

    private ArrayList<Storia> lStorie = new ArrayList<Storia>();

    private EditText usernameLogin;
    private EditText passwordLogin;
    private boolean isUsernameNull = true;
    private boolean isPasswordNull = true;

    private int selected;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference storieRef = db.collection("storie");

    private ArrayAdapter<Storia> storiaArrayAdapter;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->{
                switch(result.getResultCode()) {
                    case Activity.RESULT_OK:
//                            showResult(result);
                        break;
                    case Activity.RESULT_CANCELED:
//                            showResult(result);
                        break;
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isLogged = getIsLogged();

        bindElements();

        setListeners();
        
        fetchStories();
    }

    private void fetchStories() {
        storieRef
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Storia storia = document.toObject(Storia.class);
                            storia.setId(document.getId());
                            storiaArrayAdapter.add(storia);
                        }
                        runOnUiThread(new Runnable() {
                            @SuppressLint("NotifyDataSetChanged")
                            public void run() {
                                storiaArrayAdapter.notifyDataSetChanged();
                            }
                        });
                    } else {
                        Log.w(TAG,"Error getting documents",task.getException());
                    }
                });
    }

    private boolean getIsLogged() {
        try {
            String response = leggiLocale(getFilesDir(), "login.txt");
            if (response.equals("true")) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setListeners() {
        loginLogout.setOnClickListener(view -> {
//            loginLogout();
            if (!isLogged) {
                callLoginDialog();
            } else {
                loginLogout();
            }
        });

        DialogInterface.OnClickListener loginAlertListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_NEGATIVE:
                    Log.d("loginAlertListener", "Negative");
                    break;
                case DialogInterface.BUTTON_POSITIVE:
                    Log.d("loginAlertListener", "Positive");
                    callLoginDialog(selected);
                    break;
            }
        };

        listaStorie.setOnItemClickListener((adapterView, view, i, l) -> {
            selected = i;
            if (!isLogged) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Non loggato")
                        .setMessage("Devi essere loggato per procedere\nLoggare?")
                        .setNegativeButton("No", loginAlertListener)
                        .setPositiveButton("Sì", loginAlertListener)
                        .show();
            } else {
                Intent intent = new Intent(MainActivity.this,LeggiStoria.class);
                intent.putExtra("storia", lStorie.get(i));
                activityResultLauncher.launch(intent);
            }
        });
    }

    private void callLoginDialog() {
        AlertDialog.Builder loginBuilder = new AlertDialog.Builder(MainActivity.this);

        loginBuilder.setTitle("Login");

        final View loginLayout = getLayoutInflater()
                .inflate(R.layout.login_layout, null);

        DialogInterface.OnClickListener loginListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_NEGATIVE:
                    Log.d("login_listener", "canceled");
                    break;
                case DialogInterface.BUTTON_POSITIVE:
                    Log.d("login_listener", "logged in");
                    if (!usernameLogin.getText().equals(null) && !passwordLogin.equals(null))
                        loginLogout();
                    break;
            }
        };

        loginBuilder.setView(loginLayout)
                .setPositiveButton("Confirm", loginListener)
                .setNegativeButton("Cancel", loginListener);

        AlertDialog loginDialog = loginBuilder.create();

        loginDialog.show();

        usernameLogin = loginLayout.findViewById(R.id.login_username);
        passwordLogin = loginLayout.findViewById(R.id.login_password);

        usernameLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable)) {
                    isUsernameNull = true;
                    loginDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    isUsernameNull = false;
                    if (!isPasswordNull) {
                        loginDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }
                }
            }
        });

        passwordLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable)) {
                    isPasswordNull = true;
                    loginDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    isPasswordNull = false;
                    if (!isUsernameNull) {
                        loginDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }
                }
            }
        });

        loginDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    private void callLoginDialog(int i) {
        AlertDialog.Builder loginBuilder = new AlertDialog.Builder(MainActivity.this);

        loginBuilder.setTitle("Login");

        final View loginLayout = getLayoutInflater()
                .inflate(R.layout.login_layout, null);

        DialogInterface.OnClickListener loginListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_NEGATIVE:
                    Log.d("login_listener", "canceled");
                    break;
                case DialogInterface.BUTTON_POSITIVE:
                    Log.d("login_listener", "logged in");
                    if (!usernameLogin.getText().equals(null) && !passwordLogin.equals(null))
                        loginLogout();
                    Intent intent = new Intent(MainActivity.this,LeggiStoria.class);
                    intent.putExtra("storia", lStorie.get(i));
                    activityResultLauncher.launch(intent);
                    break;
            }
        };

        loginBuilder.setView(loginLayout)
                .setPositiveButton("Confirm", loginListener)
                .setNegativeButton("Cancel", loginListener);

        AlertDialog loginDialog = loginBuilder.create();

        loginDialog.show();

        usernameLogin = loginLayout.findViewById(R.id.login_username);
        passwordLogin = loginLayout.findViewById(R.id.login_password);

        usernameLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable)) {
                    isUsernameNull = true;
                    loginDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    isUsernameNull = false;
                    if (!isPasswordNull) {
                        loginDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }
                }
            }
        });

        passwordLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable)) {
                    isPasswordNull = true;
                    loginDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    isPasswordNull = false;
                    if (!isUsernameNull) {
                        loginDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }
                }
            }
        });

        loginDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    private void loginLogout() {
        isLogged = !isLogged;
        setLoginLogout();
        try {
//                scriviLocale("login",Boolean.toString(isLogged));
            Boolean writeOk = scriviLocale(getFilesDir(),"login.txt",Boolean.toString(isLogged));
            if (writeOk) {
                String testo = leggiLocale(getFilesDir(),"login.txt");
                Log.d("Leggi",testo);
            } else {
                Log.d("Male","non va");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setLoginLogout() {
        if (!isLogged) {
            loginLogout.setBackgroundColor(Color.GREEN);
            loginLogout.setText("Login");
        } else {
            loginLogout.setBackgroundColor(Color.RED);
            loginLogout.setText("Logout");
        }
    }

    private void bindElements() {
        storiaArrayAdapter = new ArrayAdapter<>(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                lStorie);

        listaStorie = findViewById(R.id.lista_storie);
        loginLogout = findViewById(R.id.login_logout);

        listaStorie.setAdapter(storiaArrayAdapter);

        setLoginLogout();
    }

}