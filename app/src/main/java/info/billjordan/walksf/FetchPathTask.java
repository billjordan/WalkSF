package info.billjordan.walksf;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by bill on 6/28/15.
 */
public class FetchPathTask extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] objects) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;
        ArrayList<Integer> path = null;

        try {
            // Construct the URL for the query
            // http://www.billjordan.info/php/calcPath.php?data[]=startCNN&data[]=endCNN
            // http://localhost:8080/walksf/least_work/27147000/24336000/
            URL url = new URL("http://192.168.1.127:8080/walksf/least_work/27147000/24336000/");

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty. No point in parsing.
                return null;
            }
            JSONObject jsonObject;
            path = new ArrayList<Integer>();
            Boolean startIntersectionValid;
            Boolean endIntersectionValid;
            try {
                jsonObject = new JSONObject(buffer.toString());
                JSONArray jsonPath = jsonObject.getJSONArray("path");

                for(int i = 0; i < jsonPath.length(); i++){
                    path.add(jsonPath.getInt(i));
                }
                startIntersectionValid = jsonObject.getBoolean("start_intersection_valid");
                endIntersectionValid = jsonObject.getBoolean("end_intersection_valid");
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            //remove start and end brackets

//            String[] cnnStrings = path.split(",");
//            forecastJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
        return path;
    }
}
