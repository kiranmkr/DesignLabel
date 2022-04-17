package com.example.designlabel.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.designlabel.R;
import com.example.designlabel.customCallBack.FontAdapterCallBack;


public class FontAdapter extends RecyclerView.Adapter<FontAdapter.ViewHolder> {

    private Context mcontext;
    private String[] mFontList;
    private Integer selection = null;
    private FontAdapterCallBack mCallBack;

    public FontAdapter(String[] data, FontAdapterCallBack callBack) {
        this.mFontList = data;
        this.mCallBack = callBack;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mcontext = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.re_font_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        holder.fontName.setTypeface(Typeface.createFromAsset(mcontext.getAssets(), "font/" + mFontList[position]));

        if (selection != null) {

            if (position == selection) {
                holder.fontName.setTextColor(Color.parseColor("#b39ddb"));
            } else {
                holder.fontName.setTextColor(Color.BLACK);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mFontList.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView fontName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            fontName = itemView.findViewById(R.id.textView264);

            fontName.setOnClickListener(v ->
            {
                try {

                    mCallBack.setFont(mFontList[getAdapterPosition()]);
                    setSelection(getAdapterPosition());

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        }
    }

    public void setSelection(int position) {
        selection = position;
        notifyDataSetChanged();
    }

}
