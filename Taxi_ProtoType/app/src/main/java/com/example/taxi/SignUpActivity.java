package com.example.taxi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText e2_name,e5_email,e6_password;
    FirebaseAuth auth;
    ProgressDialog dialog;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        e2_name=findViewById(R.id.editText2);
        e5_email=findViewById(R.id.editText5);
        e6_password=findViewById(R.id.editText6);
        auth=FirebaseAuth.getInstance();
        dialog=new ProgressDialog(this);
    }

    public void signUpUser(View v){
        dialog.setMessage("Registering please wait..");
        dialog.show();
        String name= e2_name.getText().toString();
        String email=e5_email.getText().toString();
        String password=e6_password.getText().toString();

        if(name.equals("")&&email.equals("")&&password.equals("")){
            Toast.makeText(getApplicationContext(),"Field cannot be empty",Toast.LENGTH_SHORT).show();
        }
        else{
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        dialog.hide();
                        Toast.makeText(getApplicationContext(),"User Successfully Register",Toast.LENGTH_SHORT).show();
                        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
                        Users user_object=new Users(e2_name.getText().toString(),e5_email.getText().toString(),e6_password.getText().toString());
                        FirebaseUser firebaseUser=auth.getCurrentUser();
                        databaseReference.child(firebaseUser.getUid()).setValue(user_object).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "User data saved", Toast.LENGTH_SHORT).show();
                                    Intent i=new Intent(SignUpActivity.this,MainPageActivity.class);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Data not saved", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else {
                        dialog.hide();
                        Toast.makeText(getApplicationContext(),"User Not Register. Please Try again...",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
