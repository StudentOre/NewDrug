package com.example.newdrug;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

// Chatボタンの処理
        Button chatButton = findViewById(R.id.ChatButton); // ChatボタンのIDを適切なIDに変更すること
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Chatアクティビティに遷移
                Intent intent = new Intent(Home.this, Chat.class);
                startActivity(intent);
            }
        });
//連絡事項
        Button contactButton = findViewById(R.id.ContactButton);
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this, Contact.class);
                startActivity(intent);
            }
        });
//個人情報
        Button personalInfomationButton = findViewById(R.id.PersonalInformationButton);
        personalInfomationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this, PersonalInformation.class);
                startActivity(intent);
            }
        });
//お薬手帳
        Button medicineBookButton = findViewById(R.id.MedicineBookButton);
        medicineBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this, MainActivity.class);
                startActivity(intent);
            }
        });

//        Button mainCameraButton = findViewById(R.id.MainCameraButton);
//        mainCameraButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Home.this, MainCamera.class);
//                startActivity(intent);
//            }
//        });

    }
}