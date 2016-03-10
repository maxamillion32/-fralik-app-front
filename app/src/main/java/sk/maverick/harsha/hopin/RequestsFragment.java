/*
 * Copyright (c)
 * Sree Harsha Mamilla
 * Pasyanthi
 * github/mavharsha
 *
 */

package sk.maverick.harsha.hopin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sk.maverick.harsha.hopin.Http.HttpManager;
import sk.maverick.harsha.hopin.Http.HttpResponse;
import sk.maverick.harsha.hopin.Http.RequestParams;
import sk.maverick.harsha.hopin.Models.*;
import sk.maverick.harsha.hopin.Util.DividerItemDecorator;

public class RequestsFragment extends Fragment{

    List<sk.maverick.harsha.hopin.Models.Request> mdataset = new ArrayList<>();
    RequestsContentAdapter requestscontentAdapter;
    private final static String TAG  = "REQUESTSFRAG";


    public RequestsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        getActivity().setTitle("Requests");

        CoordinatorLayout layout = (CoordinatorLayout) inflater.inflate(
                R.layout.fragment_requests, container, false);

        RecyclerView recyclerView = (RecyclerView) layout.findViewById(R.id.requests_recycler);

        requestscontentAdapter = new RequestsContentAdapter(mdataset);
        recyclerView.setAdapter(requestscontentAdapter);
        RecyclerView.ItemDecoration decoration = new DividerItemDecorator(getActivity(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(decoration);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        RequestParams request = new RequestParams();
        request.setUri(App.getIp()+"notification/Mavharsha");

        new RequestAsync().execute(request);
        return layout;
    }

    private class RequestsContentAdapter extends RecyclerView.Adapter<RequestsContentAdapter.ViewHolder> {

        List<sk.maverick.harsha.hopin.Models.Request> dataset;
        public RequestsContentAdapter(List<sk.maverick.harsha.hopin.Models.Request> dataset) {
            this.dataset = dataset;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_row_request, parent, false);

            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.requester.setText(dataset.get(position).getRequesteduser() +" has requested "
                                    + dataset.get(position).getSeatsrequested() +" for "
                                    + dataset.get(position).getEventname() +" event");

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }

        @Override
        public int getItemCount() {
            return dataset.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder{

            TextView requester, seatsrequested;
            public ViewHolder(View itemView) {
                super(itemView);
                 requester = (TextView) itemView.findViewById(R.id.recyclerview_request_requester);
            }
        }
    }

    private class RequestAsync extends AsyncTask<RequestParams, Void, HttpResponse>{

        @Override
        protected void onPreExecute() {
        }


        @Override
        protected HttpResponse doInBackground(RequestParams... params) {
            HttpResponse httpResponse = null;
            try {
                httpResponse =  HttpManager.getData(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return httpResponse;
        }

        @Override
        protected void onPostExecute(HttpResponse response) {

            if(response == null){
                Toast.makeText(getActivity(), "Error! Please try later", Toast.LENGTH_LONG).show();
            }
            else if(response.getStatusCode()!= 200){
                Toast.makeText(getActivity(), "Error! Please try later", Toast.LENGTH_LONG).show();
            }
            else if (response.getStatusCode() == 200){
                Toast.makeText(getActivity(), "Got data"+ response.getBody(), Toast.LENGTH_LONG).show();
                parseResopnse(response.getBody());
            }
        }

    }

    private void parseResopnse(String body) {

        Log.v(TAG, "parseRespone" + body);

        JSONArray details = null;
        sk.maverick.harsha.hopin.Models.Request request = null;

        try {
            details = new JSONArray(body);
            if(details== null){
                Log.v(TAG, "Null hai sir");
            }
            else
            {
                Log.v(TAG, "Null nahi hai sir");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<sk.maverick.harsha.hopin.Models.Request> jsonAdapter = moshi.adapter(sk.maverick.harsha.hopin.Models.Request.class);

        for (int i=0; i< details.length(); i++){

            try {
                request = jsonAdapter.fromJson(details.getJSONObject(i).toString());
                mdataset.add(request);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.v(TAG, "Event size is " + mdataset.size());
        requestscontentAdapter.notifyDataSetChanged();
    }

}
