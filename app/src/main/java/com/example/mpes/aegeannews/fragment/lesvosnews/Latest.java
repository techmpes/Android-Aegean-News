package com.example.mpes.aegeannews.fragment.lesvosnews;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.example.mpes.aegeannews.R;
import com.example.mpes.aegeannews.rss.PostData;
import com.example.mpes.aegeannews.rss.PostItemAdapter;
import com.example.mpes.aegeannews.rss.PullToRefreshListView;
import com.example.mpes.aegeannews.rss.RSSDetailActivity;
import com.example.mpes.aegeannews.rss.RSSXMLTag;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Latest extends Fragment {
    //Lista poy krataei olo to periexomeno mias eidhshs
    private ArrayList<PostData> listData = new ArrayList<PostData>();
    private PullToRefreshListView listView;

    private PostItemAdapter itemAdapter;
    View v=null;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Elegxos gia thn omalh leitourgia
        if(v==null) {
            //Energopoihsh tou view
            v = inflater.inflate(R.layout.news_latest, container, false);
            //Endeiksh fortwshs
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
            //Link ths istoselidas poy pernoyme to periexomeno
            new HTTPDownloadTask().execute("http://www.news.gr/rss.ashx?colid=2");

            //Emfanizontai oi eidhseis pou apothkeusame sthm lista listData
            listView = (PullToRefreshListView) v.findViewById(R.id.postListView);
            itemAdapter = new PostItemAdapter(getContext(), R.layout.rss_postitem, listData);
            listView.setAdapter(itemAdapter);
            listView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {

                public void onRefresh() {
                    // Your code to refresh the list contents goes here
                    itemAdapter.refresh(listData);
                    //Otan kaleitai to refresh, auto diarkei duo deuterolepta
                    listView.postDelayed(new Runnable() {

                        public void run() {
                            listView.onRefreshComplete();
                        }
                    }, 2000);
                }
            });
            listView.setOnItemClickListener(onItemClickListener);
        }
        return v;
    }
    //
    private class HTTPDownloadTask extends
            AsyncTask<String, Integer, ArrayList<PostData>> {
        private RSSXMLTag currentTag;
        private ArrayList<PostData> postDataList = new ArrayList<PostData>();

        @Override
        //Animation- kyklos fortwshs
        protected void onPreExecute() {
            super.onPreExecute();
            ObjectAnimator animation = ObjectAnimator.ofInt (progressBar, "progress", 0, 500); // see this max value coming back here, we animale towards that value
            animation.setDuration (5000); //in milliseconds
            animation.setInterpolator (new DecelerateInterpolator());
            animation.start ();
        }

        @Override
        protected ArrayList<PostData> doInBackground(String... params) {
            String urlStr = params[0];
            InputStream is = null;


            try {
                //Egkathidrysh ths sundeshs
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10 * 1000);
                connection.setConnectTimeout(10 * 1000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                //Proetimasia twn xml klasewn
                int response = connection.getResponseCode();
                is = url.openConnection().getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(is, null);

                int eventType = xpp.getEventType();
                PostData pdData = null;
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, DD MMM yyyy ");
                //Ejorujh dedomenwn
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {

                    } else if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("item")) {
                            pdData = new PostData();
                            currentTag = RSSXMLTag.IGNORETAG;
                        } else if (xpp.getName().equals("title")) {
                            currentTag = RSSXMLTag.TITLE;
                        } else if (xpp.getName().equals("link")) {
                            currentTag = RSSXMLTag.LINK;
                        } else if (xpp.getName().equals("pubDate")) {
                            currentTag = RSSXMLTag.DATE;
                        } else if (xpp.getName().equals("description")) {
                            currentTag = RSSXMLTag.DESCRIPTION;
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        if (xpp.getName().equals("item")) {
                            assert pdData != null;
                            try {
                                Date postDate = dateFormat.parse(pdData.postDate);
                                pdData.postDate = dateFormat.format(postDate);
                                postDataList.add(pdData);
                            }catch(Exception e){
                                postDataList.add(pdData);
                            }
                        } else {
                            currentTag = RSSXMLTag.IGNORETAG;
                        }
                    }
                    //Analogws me ti data sullegei ta kataxwrei
                    else if (eventType == XmlPullParser.TEXT) {
                        String content = xpp.getText();
                        content = content.trim();
                        if (pdData != null) {
                            switch (currentTag) {
                                case TITLE:
                                    if (content.length() != 0) {
                                        if (pdData.postTitle != null) {
                                            pdData.postTitle += content;
                                        } else {
                                            pdData.postTitle = content;
                                        }
                                    }
                                    break;
                                case LINK:
                                    if (content.length() != 0) {
                                        if (pdData.postLink != null) {
                                            pdData.postLink += content;
                                        } else {
                                            pdData.postLink = content;
                                        }
                                    }
                                    break;
                                case DATE:
                                    if (content.length() != 0) {
                                        if (pdData.postDate != null) {
                                            pdData.postDate += content;
                                        } else {
                                            pdData.postDate = content;
                                        }
                                    }
                                    break;
                                case DESCRIPTION:
                                    if (content.length() != 0) {
                                        if (pdData.postDetail != null) {
                                            pdData.postDetail += content;
                                        } else {
                                            pdData.postDetail = content;
                                        }
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    eventType = xpp.next();
                }
            } catch ( XmlPullParserException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return postDataList;
        }

        @Override
        //Emfanish twn periexomenwn
        protected void onPostExecute(ArrayList<PostData> result) {
            progressBar.clearAnimation();
            progressBar.setVisibility(View.GONE);
            for (int i = 0; i < result.size(); i++) {
                listData.add(result.get(i));
            }
            //Refresh
            itemAdapter.refresh(listData);
            listView.postDelayed(new Runnable() {
                public void run() {
                    listView.onRefreshComplete();
                }
            }, 2000);
        }
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        //An paththei kapoio neo emfanizetai to periexomeno toy
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            PostData data = listData.get(arg2);

            Bundle postInfo = new Bundle();
            postInfo.putString("content", data.postDetail);

            Intent postviewIntent = new Intent(getContext(), RSSDetailActivity.class);
            postviewIntent.putExtras(postInfo);
            startActivity(postviewIntent);
        }
    };
}