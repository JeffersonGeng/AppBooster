package com.jeff.appbooster;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private ScrollView scrollView;
    private TextView outputTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Button btEverything = findViewById(R.id.button_Everything);
        Button btReset = findViewById(R.id.button_Reset);
        Button btInterpretOnly = findViewById(R.id.button_InterpretOnly);
        scrollView = findViewById(R.id.scrollView);
        outputTv = findViewById(R.id.textView);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btEverything.setOnClickListener(v -> {
            String cmd = "cmd package compile -m everything -a";
            Cmd(cmd);
            Log.d("Run",cmd);
        });
        btReset.setOnClickListener(v -> {
            String cmd = "cmd package compile --reset -a";
            Cmd(cmd);
            Log.d("Run",cmd);
        });
        btInterpretOnly.setOnClickListener(v -> {
            String cmd = "cmd package compile -m interpret-only -f -a";
            Cmd(cmd);
            Log.d("Run",cmd);
        });
    }

    public void Cmd(String cmd){
        /*String result = "";
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            result = process.toString();
            Log.d("Success",result);
        }catch (IOException e){
            Log.d("Error",e.toString());
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(result)) Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();*/
        new Thread(() -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", cmd);
                Process process = processBuilder.start();
                //Process process = Runtime.getRuntime().exec(cmd);

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line;

                while ((line = reader.readLine()) != null) {
                    updateOutput(line);
                }

                while ((line = errorReader.readLine()) != null) {
                    updateOutput(line);
                }

                process.waitFor();
                reader.close();
                errorReader.close();

                updateOutput("命令执行完成");
            } catch (IOException | InterruptedException e) {
                Log.e("Error", e.toString());
                updateOutput("命令执行失败: " + e.getMessage());
            }
        }).start();
    }

    private void updateOutput(String output) {
        runOnUiThread(() -> {
            outputTv.append(output + "\n");
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        });
    }


}