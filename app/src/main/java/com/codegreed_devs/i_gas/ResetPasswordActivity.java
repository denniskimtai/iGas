package com.codegreed_devs.i_gas;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private Button resetButton;
    private EditText resetEmail;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(this);

        resetEmail = findViewById(R.id.resetEmail);

        resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get reset email entered
                String email = resetEmail.getText().toString().trim();

                //Check if string is empty
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ResetPasswordActivity.this, "Please enter email address to reset your email!", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    dialog.setMessage("Sending password reset link.\nPlease wait...");
                    dialog.show();
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //Check if email reset link was sent
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                Toast.makeText(ResetPasswordActivity.this, "Please Check your email for reset link.", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                dialog.dismiss();
                                Toast.makeText(ResetPasswordActivity.this, "Failed! Please check your internet connectivity and try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });
    }
}
