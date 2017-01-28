package souleima.saviour;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 1/27/2017.
 */

public class FirstAidKitActivity extends BaseActivity {

    private ListView mListView;
    private String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_aid_kit);
        mListView = (ListView) findViewById(R.id.listView);
        items = getResources().getStringArray(R.array.FirstAidKitArray);
        List<FirstAidKitItem> firstAidKitItems = genererItems();
        FirstAidKitItemAdapter adapter = new FirstAidKitItemAdapter(FirstAidKitActivity.this, firstAidKitItems);
        mListView.setAdapter(adapter);
    }

    private List<FirstAidKitItem> genererItems(){
        List<FirstAidKitItem> kitItems = new ArrayList<FirstAidKitItem>();
        for (String s:items){
        kitItems.add(new FirstAidKitItem(s));}
        return kitItems;
    }
}