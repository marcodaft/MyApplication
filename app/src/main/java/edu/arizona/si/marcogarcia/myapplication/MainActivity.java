package edu.arizona.si.marcogarcia.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;

    ArrayList<HashMap<String, String>> legislatorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        legislatorList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);

        new GetLegislators().execute();
    }

    private class GetLegislators extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            // String url = "http://api.androidhive.info/contacts/";
            String url = "https://www.opensecrets.org/api/?method=getLegislators&id=AZ&apikey=7862eb5af0779ffed2612ff3148a1bfe&output=json";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject response = jsonObj.getJSONObject("response");

                    // Getting JSON Array node
                    JSONArray legislators = response.getJSONArray("legislator");

                    // looping through All Legislators
                    for (int i = 0; i < legislators.length(); i++) {
                        JSONObject l = legislators.getJSONObject(i);
                        JSONObject a = l.getJSONObject("@attributes");
                        String cid = a.getString("cid");
                        String lastname = a.getString("lastname");
//                        Log.d("lastname = ", lastname);  // uncomment this line to print to logcat
                        String firstlast = a.getString("firstlast");
                        String gender = a.getString("gender");

                        // Phone node is JSON Object
//                        JSONObject phone = c.getJSONObject("phone");
//                        String mobile = phone.getString("mobile");
//                        String home = phone.getString("home");
//                        String office = phone.getString("office");

                        // tmp hash map for single legislator
                        HashMap<String, String> legislator = new HashMap<>();

                        // adding each child node to HashMap key => value
                        legislator.put("cid", cid);
                        legislator.put("lastname", lastname);
                        legislator.put("firstlast", firstlast);
                        legislator.put("gender", gender);

                        // adding legislator to legislator list
                        legislatorList.add(legislator);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, legislatorList,
//                    R.layout.list_item, new String[]{ "email","mobile"},
//                    new int[]{R.id.email, R.id.mobile});
                    R.layout.list_item, new String[]{ "lastname","gender"},
                    new int[]{R.id.lastname, R.id.gender});
            lv.setAdapter(adapter);
        }
    }
}