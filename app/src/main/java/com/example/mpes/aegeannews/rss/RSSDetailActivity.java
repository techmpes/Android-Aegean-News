package com.example.mpes.aegeannews.rss;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

import com.example.mpes.aegeannews.R;

public class RSSDetailActivity  extends AppCompatActivity {
	
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rss_detail);
		 Bundle bundle = this.getIntent().getExtras();

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null){
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}

		 String postContent = bundle.getString("content");
		 webView = (WebView)this.findViewById(R.id.RSSwebView);
		 webView.loadData(postContent, "text/html; charset=utf-8","utf-8");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
