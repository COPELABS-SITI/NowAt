package pt.ulusofona.copelabs.now.adapters;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.copelabs.now.R;



import java.util.List;
import java.util.Observable;




/**
 * Created by copelabs on 07/03/2017.
 */

public class HorizontalAdapterHolder extends RecyclerView.Adapter<HorizontalAdapterHolder.MyViewHolder> implements View.OnClickListener{

    private List<String> horizontalList;

    private View.OnClickListener listener=null;

    public List<MyViewHolder> holders;

     class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtView;

        public MyViewHolder(View view) {
            super(view);
            txtView = (TextView) view.findViewById(R.id.txtView);

        }
    }


    public HorizontalAdapterHolder( List<String> horizontalList) {
        this.horizontalList = horizontalList;

    }

    @Override
    public HorizontalAdapterHolder.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_item_view, parent, false);

        itemView.setOnClickListener(this);

        MyViewHolder viewHolder = new MyViewHolder(itemView);



        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final HorizontalAdapterHolder.MyViewHolder holder, final int position) {
        holder.txtView.setText(horizontalList.get(position));


        /*holder.txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.txtView.getCurrentTextColor() == context.getResources().getColor(R.color.colorPrimary)) {

                    holder.itemView.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.backgroud_holder_disabled, null));
                    holder.txtView.setTextColor(context.getResources().getColor(R.color.white));

                    // Keep track of the ChronoSync + Interest.


                }else{

                    holder.itemView.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.backgorud_holder_item_selected, null));
                    holder.txtView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    //ndnStop();

                }

            }
        });*/
    }


    @Override
    public int getItemCount() {

        return horizontalList.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
        Log.d("en el on","hgf");
    }




    @Override
    public void onClick(View view) {
        Log.d("antes del if", "");
        if (listener != null) {
            Log.d("dentro del if", "");
            listener.onClick(view);
        }
    }

    public class ObservableValue extends Observable
    {
        private int n = 0;
        public ObservableValue(int n)
        {
            this.n = n;
        }
        public void setValue(int n)
        {
            this.n = n;
            setChanged();
            notifyObservers();
        }
        public int getValue()
        {
            return n;
        }
    }

}
