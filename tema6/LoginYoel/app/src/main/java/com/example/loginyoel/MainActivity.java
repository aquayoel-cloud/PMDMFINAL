package com.example.loginyoel;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.materialswitch.MaterialSwitch;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText etCorreo = findViewById(R.id.etCorreo);
        EditText etPass = findViewById(R.id.etPass);
        MaterialSwitch swRecordar = findViewById(R.id.swRecordar);
        Button btnContinuar = findViewById(R.id.btnContinuar);
        TextView tvMensaje = findViewById(R.id.tvMensaje);

        btnContinuar.setOnClickListener(v -> {
            String correo = etCorreo.getText().toString();
            String pass = etPass.getText().toString();

            if (correo.isEmpty() || pass.isEmpty()) {
                if (correo.isEmpty()) {
                    etCorreo.setError(getString(R.string.error_correo));
                }
                if (pass.isEmpty()) {
                    etPass.setError(getString(R.string.error_pass));
                }
                return;
            }

            if (correo.equals("yoel@correo.com") && pass.equals("1234")) {
                tvMensaje.setTextColor(getColor(R.color.verde));
                if (swRecordar.isChecked()) {
                    tvMensaje.setText(R.string.ok_recordar);
                } else {
                    tvMensaje.setText(R.string.ok);
                }
            } else {
                tvMensaje.setTextColor(getColor(R.color.rojo));
                tvMensaje.setText(R.string.incorrecto);
            }
        });
    }
}