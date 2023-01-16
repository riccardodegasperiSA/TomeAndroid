package it.itsar.tomeandroid;

import static it.itsar.tomeandroid.LeggiScrivi.scriviLocale;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;

public class LeggiStoria extends AppCompatActivity {
    TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leggi_storia);

        Storia storia = (Storia) getIntent().getSerializableExtra("storia");

        this.setTitle(storia.getTitle());

        try {
            Boolean writeOk = scriviLocale(getFilesDir(),"storia.txt",storia.toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }

        content = findViewById(R.id.content);
        content.setText(storia.getContent());
    }
}