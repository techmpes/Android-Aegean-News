package com.example.mpes.aegeannews.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mpes.aegeannews.R;
import com.example.mpes.aegeannews.fragment.surosnews.Latest;
import com.example.mpes.aegeannews.fragment.surosnews.Politics;
import com.example.mpes.aegeannews.fragment.surosnews.Sports;

public class SurosFragment extends Fragment  {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SurosFragment() {
    }

    public static SurosFragment newInstance(String param1, String param2) {

            SurosFragment fragment = new SurosFragment();
                Bundle args = new Bundle();
                args.putString(ARG_PARAM1, param1);
                args.putString(ARG_PARAM2, param2);
                fragment.setArguments(args);
                return fragment;
            }

            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                if (getArguments() != null) {
                    mParam1 = getArguments().getString(ARG_PARAM1);
                    mParam2 = getArguments().getString(ARG_PARAM2);
                }
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                FragmentTabHost tabHost = new FragmentTabHost(getActivity());
                tabHost.setup(getActivity(), getChildFragmentManager(), R.layout.fragment_news);

                Bundle arg1 = new Bundle();
                arg1.putInt("Latest News", 1);
                tabHost.addTab(tabHost.newTabSpec("Tab1").setIndicator("Latest News"), Latest.class, arg1);
                Bundle arg2 = new Bundle();
                arg2.putInt("Environment", 2);
                tabHost.addTab(tabHost.newTabSpec("Tab2").setIndicator("Environment"), Politics.class, arg2);
                Bundle arg3 = new Bundle();
                arg3.putInt("Sports", 3);
                tabHost.addTab(tabHost.newTabSpec("Tab3").setIndicator("Sports"), Sports.class, arg3);

                return tabHost;
            }

            public void onButtonPressed(Uri uri) {
                if (mListener != null) {
                    mListener.onFragmentInteraction(uri);
                }
            }

            @Override
            public void onAttach(Context context) {
                super.onAttach(context);

            }

            @Override
            public void onDetach() {
                super.onDetach();
                mListener = null;
            }

            public interface OnFragmentInteractionListener {
                void onFragmentInteraction(Uri uri);
            }
        }
