package de.ur.mi.android.demos.data;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Diese Klasse repräsentiert eine einzelne Anfrage an die API.
 * Der Klasse kann im route Objekt eine URL übergeben werden. An diese wird dann eine GET-Anfrage gestellt,
 * wenn die send-Methode aufgerufen wird. Sobald eine Antwort der API kommt wird der in send übergebene
 * Listener vom Typ ResponseListener informiert.
 */
public class APIRequest {

    private final Route route;
    private final Context context;

    /**
     * Um eine API Anfrage zu erstellen ist ein Context und eine URL als Route Objekt erforderlich.
     *
     * @param route URL an welche die Anfrage gesendet werden soll.
     * @param context Anwendungskontext.
     */
    public APIRequest(Route route, Context context) {
        this.route = route;
        this.context = context;
    }

    /**
     * Diese Methode startet die API-Anfrage. Der übergebene Listener wird über das Ergebnis informiert.
     *
     * @param listener ResponseListener, der über die Antwort der API informiert werden solle.
     */
    public void send(ResponseListener listener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, this.route.url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        listener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError();
                    }
                });
        queue.add(stringRequest);
    }

    /**
     * Eine Route entspricht einer URL an die eine GET-Anfrage gesendet werden kann.
     */
    public enum Route {
        AUDIOBOOK_DATA("https://audiobook.software-engineering.education/audiobookdata.json");

        private final String url;

        Route(String url) {
            this.url = url;
        }
    }

    public interface ResponseListener {
        void onResponse(String response);
        void onError();
    }
}
