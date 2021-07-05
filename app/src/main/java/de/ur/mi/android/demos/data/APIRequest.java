package de.ur.mi.android.demos.data;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


/**
 * Die APIRequest-Klasse dient dazu, Daten aus einer Online-Quelle zu beziehen.
 * Dabei wird die Volley-Library eingesetzt, um die Nebenläufigkeit und die Kommunikation zu steuern.
 */
public class APIRequest {

    private final Route route;
    private final Context context;

    public APIRequest(Route route, Context context) {
        this.route = route;
        this.context = context;
    }

    /**
     * Diese Methode feuert einen HTTP GET-Request an die Route, die im Konstruktor übergeben wurde.
     * Sobald eine Antwort des Servers eintrifft, wird ein Listener informiert.
     *
     * @param listener: Der ResponseListener erhält den JSONString aus der HTTP-Response. Darin befindet sich in diesem Fall ein Array aus AudioBooks.
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
     * Dieses Enum dient dazu, die benötigten Routen strukturiert zu verwalten.
     * In diesem Fall ist nur eine URL hinterlegt, dieser Ansatz lässt sich allerdings sehr schön skalieren.
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
