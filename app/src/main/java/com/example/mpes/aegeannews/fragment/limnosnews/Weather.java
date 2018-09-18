package com.example.mpes.aegeannews.fragment.limnosnews;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Weather extends Fragment {

    private ArrayList<PostData> listData = new ArrayList<PostData>();
    private PullToRefreshListView listView;
    private PostItemAdapter itemAdapter;
    View v = null;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (v == null) {
            v = inflater.inflate(R.layout.news_politics, container, false);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

            new HTTPDownloadTask().execute("http://www.meteo.gr/rss/news.cfm");


            listView = (PullToRefreshListView) v.findViewById(R.id.postListView);
            itemAdapter = new PostItemAdapter(getContext(), R.layout.rss_postitem, listData);
            listView.setAdapter(itemAdapter);
            listView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {

                public void onRefresh() {
                    // Your code to refresh the list contents goes here
                    itemAdapter.refresh(listData);
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

    private class HTTPDownloadTask extends
            AsyncTask<String, Integer, ArrayList<PostData>> {
        private RSSXMLTag currentTag;
        private ArrayList<PostData> postDataList = new ArrayList<PostData>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 500); // see this max value coming back here, we animale towards that value
            animation.setDuration(5000); //in milliseconds
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();
        }

        @Override
        protected ArrayList<PostData> doInBackground(String... params) {
            String urlStr = params[0];
            InputStream is = null;


            try {
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10 * 1000);
                connection.setConnectTimeout(10 * 1000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                int response = connection.getResponseCode();
                is = url.openConnection().getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(is, null);

                int eventType = xpp.getEventType();
                PostData pdData = null;
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, DD MMM yyyy ");
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
                            Date postDate = dateFormat.parse(pdData.postDate);
                            pdData.postDate = dateFormat.format(postDate);
                            postDataList.add(pdData);
                        } else {
                            currentTag = RSSXMLTag.IGNORETAG;
                        }
                    } else if (eventType == XmlPullParser.TEXT) {
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
            } catch (ParseException | XmlPullParserException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return postDataList;
        }

        @Override
        protected void onPostExecute(ArrayList<PostData> result) {
            progressBar.clearAnimation();
            progressBar.setVisibility(View.GONE);
            for (int i = 0; i < result.size(); i++) {
                listData.add(result.get(i));
            }
            itemAdapter.refresh(listData);
            listView.postDelayed(new Runnable() {
                public void run() {
                    listView.onRefreshComplete();
                }
            }, 2000);
        }
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

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