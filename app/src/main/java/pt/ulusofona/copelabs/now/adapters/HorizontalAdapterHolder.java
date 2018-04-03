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

import pt.ulusofona.copelabs.now.interfaces.NowMainActivityInterface;

/**
 * This class is part of Now@ application. It extends to ReyclerView.Adapter. This class returns
 * a view for each object in a collection of data.
 *
 * @author Omar Aponte (COPELABS/ULHT)
 * @version 1.0
 *          COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:06 PM
 */

public class HorizontalAdapterHolder extends RecyclerView.Adapter<HorizontalAdapterHolder.MyViewHolder> {

    /**
     * Used for debug.
     */
    private String TAG = HorizontalAdapterHolder.class.getSimpleName();

    /**
     * List of string with the name of the categories.
     */
    private List<String> mHorizontalList;

    /**
     * Context of the application.
     */
    private Context mContext;

    /**
     * Interface of the main activity.
     */
    private NowMainActivityInterface mNowMainActivityInterface;


    /**
     * This method is hte constructor of HorizontalAdapterHolder
     *
     * @param horizontalList           List<String> of collection data
     * @param context                  Context of the application
     * @param nowMainActivityInterface NowMainActivityInterface interface of the main activity.
     */
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

    /**
     * This method executes the bind process of the view holder.
     *
     * @param holder   Holder which contains the view.
     * @param position Position of the ViewHolder.
     */
    @Override
    public void onBindViewHolder(final HorizontalAdapterHolder.MyViewHolder holder, final int position) {

        holder.txtView.setText(mHorizontalList.get(position));
        holder.txtView.setId(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.txtView.getCurrentTextColor() == mContext.getResources().getColor(R.color.textDeseable)) {

                    holder.itemView.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.backgroud_holder_enable, null));
                    holder.txtView.setTextColor(mContext.getResources().getColor(R.color.blue));

                    // Keep track of the ChronoSync + Interest.
                    mNowMainActivityInterface.updateValueSelected(mHorizontalList.get(position));

                } else {

                    holder.itemView.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.backgorud_holder_item_desable, null));
                    holder.txtView.setTextColor(mContext.getResources().getColor(R.color.textDeseable));
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

    /**
     * View holder used to keep the information about the categories.
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {

        /**
         * Textview displays the name of the category.
         */
        private TextView txtView;

        public MyViewHolder(View view) {
            super(view);
            txtView = (TextView) view.findViewById(R.id.txtView);

        }
    }

}
