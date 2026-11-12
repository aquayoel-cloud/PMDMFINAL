package com.example.loginyoel;

import android.content.Intent;
import android.content.SharedPreferences;   // EJ2
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

    private SharedPreferences prefs;   // EJ2

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

        // EJ2: abrir las preferencias y rellenar el correo si hay uno guardado
        prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        String guardado = prefs.getString("correo", "");
        if (!guardado.isEmpty()) {
            etCorreo.setText(guardado);
            swRecordar.setChecked(true);
            etPass.requestFocus();
        }

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
                // EJ2: guardar o borrar el correo según el interruptor
                if (swRecordar.isChecked()) {
                    prefs.edit().putString("correo", correo).apply();
                } else {
                    prefs.edit().remove("correo").apply();
                }

                Intent intent = new Intent(this, MenuActivity.class);
                intent.putExtra(MenuActivity.EXTRA_CORREO, correo);
                startActivity(intent);
                finish();   // el login sale de la pila
            } else {
                tvMensaje.setTextColor(getColor(R.color.rojo));
                tvMensaje.setText(R.string.incorrecto);
            }
        });
    }
}