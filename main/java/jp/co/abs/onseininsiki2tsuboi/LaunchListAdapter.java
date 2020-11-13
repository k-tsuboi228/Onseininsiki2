package jp.co.abs.onseininsiki2tsuboi;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class LaunchListAdapter{
    Context context;
    LayoutInflater layoutInflater = null;
  //  ArrayList<LaunchItem> mArrayList = null;

    LaunchAppListener mLaunchAppListener = null;
    public interface LaunchAppListener{
        void onLaunch(Intent intent);
    }

    public void setLaunchAppListener(LaunchAppListener _mLaunchAppListener){
        this.mLaunchAppListener = _mLaunchAppListener;
    }

    public LaunchListAdapter(Context context){
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

  /*  public int getCount(){
        return mArrayList.size();
    }

    public Object getItem(int position) {
        return mArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    */
}
