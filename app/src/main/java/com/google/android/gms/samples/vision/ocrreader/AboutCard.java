package com.google.android.gms.samples.vision.ocrreader;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutCard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_card);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.title_activity_main);

        int id = getIntent().getIntExtra("id", 0);
        CardInstance card = CardInstance.cards.get(id);

        String payment = CardInstance.getPaymentSystem(card.getCardNumber());
        if(payment != null){
            ((TextView) findViewById(R.id.paymentSystem)).setText(payment);
        }

        LinearLayout linearLayout = findViewById(R.id.propertiesLinLay);
        addData("Bank card ID", card.getCardNumber(), this, linearLayout);

        addData("Valid through", card.getValid(), this, linearLayout);

        addData("Card number", card.getAccountNumber(), this, linearLayout);

        addData("Owner", card.getOwner(), this, linearLayout);

        addData("Issuer", card.getCompany(), this, linearLayout);
    }

    private void addData(String type, String data, Context context, LinearLayout linearLayout){
        if(data == null || data.compareTo("null") == 0 || data.isEmpty()) return;
        View view = LayoutInflater.from(context).inflate(R.layout.property, null);
        TextView headerText = view.findViewById(R.id.type);
        headerText.setText(type);
        TextView dataText = view.findViewById(R.id.headerProperty);
        dataText.setText(data);
        linearLayout.addView(view);
    }

    /**
     * Настройка шапки
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Обработка нажатия на элементы шапки
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                finish();
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
