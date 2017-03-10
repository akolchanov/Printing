package printing.printing;


import android.content.Context;
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


public class CustomNewsAdapter extends ArrayAdapter<NewsModel> implements View.OnClickListener{

    private ArrayList<NewsModel> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView txtName;
        TextView txtType;
        TextView txtVersion;
        ImageView info;
    }

    public CustomNewsAdapter(ArrayList<NewsModel> data, Context context) {
        super(context, R.layout.news_row, data);
        this.dataSet = data;
        this.mContext = context;

    }


    @Override
    public void onClick(View v) {}

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewsModel dataModel = getItem(position);
        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.news_row, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.title);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.content);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;


        viewHolder.txtName.setText(dataModel.getText());
        viewHolder.txtType.setText(dataModel.getTitle());
        return convertView;
    }


}
