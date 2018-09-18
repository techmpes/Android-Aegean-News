package com.example.mpes.aegeannews.rss;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.mpes.aegeannews.R;

import java.util.ArrayList;
//Kata to pathma mias eidhshs emfanizetai to periexomeno ths
public class PostItemAdapter extends ArrayAdapter<PostData> {
	private Activity myContext;
	private ArrayList<PostData> datas;
	public PostItemAdapter(Context context, int textViewResourceId,
			ArrayList<PostData> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		myContext = (Activity) context;
		datas = objects;
	}

	public void refresh(ArrayList<PostData> list) {  
		datas = list;  
        notifyDataSetChanged();  
    } 
	//H morfopoihsh ths eidhshs me titlo, periexomeno kai hmeromhnia
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder; 
		if(convertView ==null){
			LayoutInflater inflater = myContext.getLayoutInflater();
			convertView = inflater.inflate(R.layout.rss_postitem, null);
			viewHolder = new ViewHolder();
			viewHolder.postThumbView = (ImageView) convertView.findViewById(R.id.postThumb);
			viewHolder.postTitleView = (TextView) convertView.findViewById(R.id.postTitleLabel);
			viewHolder.postDateView = (TextView) convertView.findViewById(R.id.postDateLabel);
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (datas.get(position).postThumbUrl == null) {
			viewHolder.postThumbView.setImageResource(R.drawable.ic_rss_feed_black_36dp);
		}
		viewHolder.postTitleView.setText(datas.get(position).postTitle);
		viewHolder.postDateView.setText(datas.get(position).postDate);

		return convertView;
	}
	static class ViewHolder {
		  TextView postTitleView;
		  TextView postDateView;
		  ImageView postThumbView;
    }
}
