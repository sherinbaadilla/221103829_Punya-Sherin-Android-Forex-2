package com.sherin1234.forex2;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout _swipeRefreshLayout;
    private RecyclerView _recyclerView;
    private TextView _timestampTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.swipeRefreshLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initSwipeRefreshLayout();
        _recyclerView = findViewById(R.id.recyclerView1);
        _timestampTextView = findViewById(R.id.timestampTextView);

        bindRecyclerView();
    }

    private void bindRecyclerView() {
        String restUrl = "https://openexchangerates.org/api/latest.json?app_id=27bf9b5fcace4763be9b177c8df9744d";
        String currUrl = "https://openexchangerates.org/api/currencies.json";

        AsyncHttpClient client = new AsyncHttpClient();
        Log.d("http", "msg : accessing API ratesUrl");

        client.get(restUrl, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject root = new JSONObject(new String(responseBody));
                    JSONObject ratesObj = root.getJSONObject("rates");
                    long timestamp = root.getLong("timestamp");

                    setTimestamp(timestamp);

                    client.get(currUrl, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                JSONObject currencies = new JSONObject(new String(responseBody));
                                List<ForexModel> forexList = new ArrayList<>();

                                Iterator<String> keys = ratesObj.keys();
                                while (keys.hasNext()) {
                                    String code = keys.next();
                                    if (code.equals("IDR")) continue;

                                    try {
                                        double rate = ratesObj.getDouble(code);
                                        String name = currencies.optString(code, "Unknown");
                                        forexList.add(new ForexModel(code, name, rate));
                                    } catch (JSONException e) {
                                        Log.d("ForexError", "msg : Gagal parsing untuk " + code, e);
                                    }
                                }

                                ForexAdapter adapter = new ForexAdapter(forexList);
                                _recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                                _recyclerView.setAdapter(adapter);

                            } catch (JSONException e) {
                                Toast.makeText(MainActivity.this, "Gagal parsing data mata uang", Toast.LENGTH_SHORT).show();
                            }

                            _swipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(MainActivity.this, "Gagal ambil nama mata uang", Toast.LENGTH_SHORT).show();
                            _swipeRefreshLayout.setRefreshing(false);
                        }
                    });

                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "Gagal parsing kurs", Toast.LENGTH_SHORT).show();
                    _swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("http", error.getMessage());
                Toast.makeText(MainActivity.this, "Gagal ambil kurs", Toast.LENGTH_SHORT).show();
                _swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void setTimestamp(long timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        String dateTime = format.format(new java.util.Date(timestamp * 1000));
        _timestampTextView.setText("Tanggal dan Waktu: " + dateTime);
    }

    private void initSwipeRefreshLayout() {
        _swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        _swipeRefreshLayout.setOnRefreshListener(this::bindRecyclerView);
    }
}
