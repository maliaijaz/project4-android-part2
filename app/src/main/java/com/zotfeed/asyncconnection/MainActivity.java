package com.zotfeed.asyncconnection;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.zotfeed.asyncconnection.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import android.os.AsyncTask;

//import org.apache.http.*;

//import com.loopj.android.http.*;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.cookie.Cookie;

//import cz.msebera.android.httpclient.Header;
//import cz.msebera.android.httpclient.cookie.Cookie;


public class MainActivity extends Activity {

    private EditText editText;


    public void submit(){
        final TextView output = (TextView) findViewById(R.id.textView1); // program forces me to make this variable final
        // it should NOT be final

        if(editText.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please type in a movie name", Toast.LENGTH_SHORT).show();
        }else{

            AsyncHttpClient client = new AsyncHttpClient();
            final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
            client.setCookieStore(myCookieStore);
            RequestParams params = new RequestParams();

            // PASS IN YOUR editText.getText().toString() to this URL, so that the servlet receives this
            // and then does the search query in the searchServlet
            // URL = "http://10.0.2.2:8080/fabflix/search-automatic-servlet?keywords="+searchText.getText()
            //                     String moviename = editText.getText().toString();
            client.get("http://10.0.2.2:8080/HelloWorldServletApp/SearchSerlvet?title=" + editText.getText(), null, new TextHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
                    Log.d("MSG:", "status code = " + statusCode);
                    if (statusCode == 404) {
                        Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                    } else if (statusCode == 500) {
                        Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
                                Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String response) {
                    Log.d("MSG:", "RESPONSE IS " + response);

                    String moviename = editText.getText().toString();

                    String data = "";
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for(int i=0; i < jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String name = jsonObject.optString("title").toString();
                            data += "Movie Title: " + name + " " + moviename + "\n"; // might need to remove it
                        }
                        output.setText(data); // change output to NOT final
                    } catch (JSONException e) {Log.d("MLM:","Wrong JSON");}

                }
            });
        }
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cookie stuff
        final PersistentCookieStore temp=new PersistentCookieStore(this);
        List<Cookie> cookies=temp.getCookies();
        for(Cookie t:cookies){
            Log.d("MSG123:",t.getName()+t.getValue());
        }

        setContentView(R.layout.activity_main);

        // grab the text
        Button button = (Button) findViewById(R.id.button);
        editText   = (EditText)findViewById(R.id.editText);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();

            }
        });

    }
}

