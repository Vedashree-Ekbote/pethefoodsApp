package com.example.pethe;

import androidx.appcompat.app.AppCompatActivity;
import android.content.res.Resources;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SearchResultsAdapter searchResultsAdapter;
    private List<String[]> searchResultsList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonsearch = findViewById(R.id.buttonSearch);
        EditText editTextSearch = findViewById(R.id.editTextSearch);

        buttonsearch.setOnClickListener(view -> {
            String searchTerm=editTextSearch.getText().toString().toLowerCase();
            performSearch(searchTerm);
        });
        recyclerView = findViewById(R.id.recyclerViewSearchResults);
        searchResultsAdapter = new SearchResultsAdapter(searchResultsList);
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

                // Rest of your processing
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

    private void performSearch(String searchItem) {
        searchResultsList.clear();

        List<String[]> csvData = readCSVFromResources();

        for (String[] line : csvData) {
            String itemname = line[0];
            String price = line[1];

            if (itemname.toLowerCase().contains(searchItem)) {
                double price_100gm = calculateprice(price, 100);
                double price_125gm = calculateprice(price, 125);
                double price_250gm = calculateprice(price, 250);
                double price_500gm = calculateprice(price, 500);

                searchResultsList.add(new String[] {
                        itemname,
                        String.valueOf(price_100gm),
                        String.valueOf(price_125gm),
                        String.valueOf(price_250gm),
                        String.valueOf(price_500gm)
                });
            }
        }
        searchResultsAdapter.notifyDataSetChanged();
    }


    private  double calculateprice(String price,int quantity){
        double priceperkg=Double.parseDouble(price);
        return (priceperkg/1000)*quantity;

    }
    private class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

        private List<String[]> searchResults;

        public SearchResultsAdapter(List<String[]> searchResults) {
            this.searchResults = searchResults;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_search_result, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String[] result = searchResults.get(position);
            String itemname = result[0];
            String price100 = result[1];
            String price125 = result[2];
            String price250 = result[3];
            String price500 = result[4];

            // Calculate prices and format them with two decimal places
            double price100Value = calculateprice(price100, 100);
            double price125Value = calculateprice(price125, 125);
            double price250Value = calculateprice(price250, 250);
            double price500Value = calculateprice(price500, 500);

            // Format the calculated prices
            String formattedPrice100 = String.format("%f", price100Value);
            String formattedPrice125 = String.format("%f", price125Value);
            String formattedPrice250 = String.format("%f", price250Value);
            String formattedPrice500 = String.format("%f", price500Value);

            String resultText = "Item: " + itemname + "\n" +
                    "Price for 100g:" + formattedPrice100 + "\n" +
                    "Price for 125g:" + formattedPrice125 + "\n" +
                    "Price for 250g:" + formattedPrice250 + "\n" +
                    "Price for 500g:" + formattedPrice500;

            holder.textViewItem.setText(resultText);
        }

        @Override
        public int getItemCount() {
            return searchResults.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textViewItem;

            public ViewHolder(View itemView) {
                super(itemView);
                textViewItem = itemView.findViewById(R.id.textViewItem);
            }
        }
    }

}