package printing.printing;


import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;


public class CustomItemAdapter extends ArrayAdapter<ItemModel> implements View.OnClickListener{

    private ArrayList<ItemModel> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView txtName;
        TextView txtType;
        TextView txtVersion;
        ImageView info;
    }

    public CustomItemAdapter(ArrayList<ItemModel> data, Context context) {
        super(context, R.layout.item_row, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        ItemModel dataModel=(ItemModel)object;


        switch (v.getId())
        {
            case R.id.item_info:
                Snackbar.make(v, "Цена товара: " +dataModel.getPrice(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemModel dataModel = getItem(position);
        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {


            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_row, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.type);
            viewHolder.txtVersion = (TextView) convertView.findViewById(R.id.version_number);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.item_info);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;


        viewHolder.txtType.setText(dataModel.getDescription());
        viewHolder.txtVersion.setText(dataModel.getTitle());
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);
        return convertView;
    }


}
