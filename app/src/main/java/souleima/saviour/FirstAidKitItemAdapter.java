package souleima.saviour;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.List;

/**
 * Created by pc on 1/27/2017.
 */

public class FirstAidKitItemAdapter extends ArrayAdapter<FirstAidKitItem> {

    public FirstAidKitItemAdapter (Context context, List<FirstAidKitItem> itemList) {
        super(context, 0, itemList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.first_aid_kit_item,parent, false);
        }

        FirstAidKitItemViewHolder viewHolder = (FirstAidKitItemViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new FirstAidKitItemViewHolder();
            viewHolder.item = (CheckBox) convertView.findViewById(R.id.cbFirstAidKitItem);
            convertView.setTag(viewHolder);
        }

        //getItem(position) get the item[position] from List<FirstAidKitItem>
        FirstAidKitItem firstAidKitItem = getItem(position);

        //fill the view
        viewHolder.item.setText(firstAidKitItem.getItemName());

        return convertView;
    }

    private class FirstAidKitItemViewHolder{
        public CheckBox item;
    }
}
