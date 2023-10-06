package com.example.pethe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;

public class GetPrice extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchResultsAdapter searchResultsAdapter;
    private List<String[]> searchResultsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_price);

        Button buttonsearch = findViewById(R.id.buttonSearch);
        Button addItem=findViewById(R.id.Additembutton);
        EditText editTextSearch = findViewById(R.id.editTextSearch);

        buttonsearch.setOnClickListener(view -> {
            String searchTerm=editTextSearch.getText().toString().toLowerCase();
            performSearch(searchTerm);
        });

        addItem.setOnClickListener(view -> {
            AlertDialog.Builder builder=new AlertDialog.Builder(GetPrice.this);
            LayoutInflater inflater= getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_add_item,null);
            builder.setView(dialogView);

            EditText editTextItemName = dialogView.findViewById(R.id.editTextItemName);
            EditText editTextPrice = dialogView.findViewById(R.id.editTextItemPrice);

            builder.setPositiveButton("Add",(dialog,which)->{
                String newItemname=editTextItemName.getText().toString();
                String newPrice=editTextPrice.getText().toString();

                if(!newItemname.isEmpty() && !newPrice.isEmpty()){
                    appendDataToCSV(newItemname,newPrice);

                    Toast.makeText(GetPrice.this, "Item Added Successfully", Toast.LENGTH_SHORT).show();

                    dialog.dismiss();
                }else{
                    Toast.makeText(GetPrice.this, "Plaease fill all the fields correctly", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {

                dialog.dismiss();
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        });
        recyclerView = findViewById(R.id.recyclerViewSearchResults);
        searchResultsAdapter = new GetPrice.SearchResultsAdapter(searchResultsList);
        recyclerView.setAdapter(searchResultsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<String[]> csvData = readCSVFromResources();

        if (csvData.isEmpty()) {
            Log.e("CSV", "No data found in CSV file");
            return;
        }
        for(String[] line:csvData){
            if (line.length >= 2) { // Ensure the array has at least 2 elements
                Log.d("CSV", Arrays.toString(line));
                String itemname = line[0];
                String price = line[1];

            } else {
                Log.e("CSV", "Invalid data in CSV line: " + Arrays.toString(line));
            }
        }
    }
    public List<String[]>readCSVFromResources(){
        List<String[]> csvLines = new ArrayList<>();

        try {
            Resources resources = getResources();
            InputStream inputStream= resources.openRawResource(R.raw.rates);
            BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                csvLines.add(data);
                Log.d("CSV", "Data read: " + Arrays.toString(data));
            }

            reader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return csvLines;
    }

    private void appendDataToCSV(String newItemName,String newPrice){
        String[] newData={newItemName,newPrice};

        try{
            File directory = getFilesDir();
            File file = new File(directory, "rates.csv");
            FileWriter writer = new FileWriter(file, true);

            CSVUtils.writeLine(writer,Arrays.asList(newData));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private double calculateprice(String price, double quantity) {
        double pricePerKilogram = Double.parseDouble(price);

        return (pricePerKilogram * quantity)/1000.0;
    }
    private void performSearch(String searchItem) {
        searchResultsList.clear();

        List<String[]> csvData = readCSVFromResources();

        for (String[] line : csvData) {
            String itemname = line[0];
            String price = line[1];
            try {
                double priceperKg = Double.parseDouble(price);
                Log.d("csv", "itemname: " + itemname + ",price per KG:" + priceperKg);
                if (itemname.toLowerCase().contains(searchItem)) {
                    double price_1KG = calculateprice(price, 1000.0);
                    double price_100gm = calculateprice(price, 100.0);
                    double price_125gm = calculateprice(price, 125.0);
                    double price_250gm = calculateprice(price, 250.0);
                    double price_500gm = calculateprice(price, 500.0);
                    Log.d(price, "100gm: "+price_100gm);
                    Log.d(price, "125gm: "+price_125gm);
                    Log.d(price, "250gm: "+price_250gm);
                    Log.d(price, "500gm: "+price_500gm);
                    searchResultsList.add(new String[]{
                            itemname,
                            String.format("%.2f", price_1KG),
                            String.format("%.2f", price_100gm),
                            String.format("%.2f", price_125gm),
                            String.format("%.2f", price_250gm),
                            String.format("%.2f", price_500gm)
                    });
                }
            } catch (NumberFormatException e) {
                Log.e("CSV", "Error in parsing price" + price);
            }
        }
        searchResultsAdapter.notifyDataSetChanged();
    }

    public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

        private List<String[]> searchResults;

        public SearchResultsAdapter(List<String[]> searchResults) {
            this.searchResults = searchResults;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_search_result, parent, false);
            return new GetPrice.SearchResultsAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String[] result = searchResults.get(position);
            String itemname = result[0];
            String price1=result[1];
            String price100 = result[2];
            String price125 = result[3];
            String price250 = result[4];
            String price500 = result[5];

            holder.textViewItem.setText("Item:" + itemname);
            holder.itemViewprice1.setText("Price for 1kg:" + price1);
            holder.itemViewprice100.setText("Price for 100g:" + price100);
            holder.itemViewprice125.setText("Price for 125g:" + price125);
            holder.itemViewprice250.setText("Price for 250g:" + price250);
            holder.itemViewprice500.setText("Price for 500g:" + price500);
        }

        @Override
        public int getItemCount() {

            return searchResults.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textViewItem;
            TextView itemViewprice1;
            TextView itemViewprice100;
            TextView itemViewprice125;
            TextView itemViewprice250;
            TextView itemViewprice500;


            public ViewHolder(View itemView) {
                super(itemView);
                textViewItem = itemView.findViewById(R.id.textViewItem);
                itemViewprice1=itemView.findViewById(R.id.itemViewprice1);
                itemViewprice100=itemView.findViewById(R.id.itemViewprice100);
                itemViewprice125=itemView.findViewById(R.id.itemViewprice125);
                itemViewprice250=itemView.findViewById(R.id.itemViewprice250);
                itemViewprice500=itemView.findViewById(R.id.itemViewprice500);

            }
        }
    }

}
