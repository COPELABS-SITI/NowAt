package pt.ulusofona.copelabs.now.adapters;


import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.copelabs.now.R;

import java.util.List;

import pt.ulusofona.copelabs.now.activities.NowMainActivityInterface;


/**
 * Created by copelabs on 07/03/2017.
 */

public class HorizontalAdapterHolder extends RecyclerView.Adapter<HorizontalAdapterHolder.MyViewHolder> {

    private String TAG = HorizontalAdapterHolder.class.getSimpleName();

    private List<String> mHorizontalList;

    private Context mContext;

    private NowMainActivityInterface mNowMainActivityInterface;


     public class MyViewHolder extends RecyclerView.ViewHolder  {

        private TextView txtView;

        public MyViewHolder(View view) {
            super(view);
            txtView = (TextView) view.findViewById(R.id.txtView);

        }
     }


    public HorizontalAdapterHolder(List<String> horizontalList, Context context, NowMainActivityInterface nowMainActivityInterface) {
        mHorizontalList = horizontalList;
        mContext = context;
        mNowMainActivityInterface = nowMainActivityInterface;

    }

    @Override
    public HorizontalAdapterHolder.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_item_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final HorizontalAdapterHolder.MyViewHolder holder, final int position) {
        holder.txtView.setText(mHorizontalList.get(position));


        holder.txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.txtView.getCurrentTextColor() == mContext.getResources().getColor(R.color.colorPrimary)) {

                    holder.itemView.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.backgroud_holder_disabled, null));
                    holder.txtView.setTextColor(mContext.getResources().getColor(R.color.white));

                    // Keep track of the ChronoSync + Interest.
                    mNowMainActivityInterface.updateValueSelected(mHorizontalList.get(position));

                }else{

                    holder.itemView.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.backgorud_holder_item_selected, null));
                    holder.txtView.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                    //ndnStop();
                    mNowMainActivityInterface.updateValueSelected(mHorizontalList.get(position));
                }
            }
        });
    }


    @Override
    public int getItemCount() {

        return mHorizontalList.size();
    }

}
