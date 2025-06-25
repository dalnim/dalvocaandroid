package com.dalread.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by JetVHS on 3/15/2017.
 */
public class LanguageAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private String[] data;
    private LayoutInflater inflater;

    public LanguageAdapter(Context context, int textViewResourceId, String[] data) {
        super(context, textViewResourceId, data);
        this.mContext = context;
        this.data = data;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.length;
    }

    public String getItem(int position) {
        return data[position];
    }

    public long getItemId(int position) {
        return position;
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder mHolder;

        if (view == null) {
            view = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
            mHolder = new ViewHolder(view);
            view.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) view.getTag();
        }

        mHolder.text1.setText(data[position]);
        return view;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder mHolder;

        if (view == null) {
            view = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
            mHolder = new ViewHolder(view);
            view.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) view.getTag();
        }

        mHolder.text1.setText(data[position]);
        return view;
    }

    public class ViewHolder {
        @BindView(android.R.id.text1)
        TextView text1;

        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
            text1.setGravity(Gravity.LEFT | Gravity.CENTER);
        }
    }
}