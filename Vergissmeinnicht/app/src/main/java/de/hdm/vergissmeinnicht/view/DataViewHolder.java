package de.hdm.vergissmeinnicht.view;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdm.vergissmeinnicht.R;

/**
 * Created by Dennis Jonietz on 30.05.2015.
 */
public class DataViewHolder extends RecyclerView.ViewHolder {

    @Nullable @Bind(R.id.event_name)          public TextView eventNameTxt;
    @Nullable @Bind(R.id.event_repeat)        public TextView eventInfoTxt;
    @Nullable @Bind(R.id.event_info)          public TextView eventRepeatTxt;
    @Nullable @Bind(R.id.btn_delete_data)     public TextView deleteDataBtn;
    @Nullable @Bind(R.id.btn_edit_data)       public TextView editDataBtn;

    public DataViewHolder(View view) {
        super(view);

        // initialize Butterknife
        ButterKnife.bind(this, view);

    }

}