package cnedu.ustc.jd.viewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ustc.jd5737.helloworld.R;

/**
 * Created by jd5737 on 2016/7/27.
 */
public class PageFragment extends Fragment {

    private static final String PAGE_FRAGMENT_INDEX = "com.arcsoft.jd5735.page_fragment_index";
    private int index;
    public static PageFragment newInstance(int index) {

        Bundle args = new Bundle();
        args.putInt(PAGE_FRAGMENT_INDEX,index);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public  View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_view_page,container,false);
        TextView tv = (TextView) rootView.findViewById(R.id.fragment_view_page_tv_title);
        Bundle args = getArguments();
        index = args.getInt(PAGE_FRAGMENT_INDEX);
        tv.setText("Fragment " + index);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.util.Log.d("XXXX","x:"+rootView.getX()+",left:"+rootView.getLeft());
                android.util.Log.d("XXXX","w:"+container.getWidth()+",h:"+container.getHeight());
            }
        });
        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        //Log.d("XXXX","onStart() : " + index);
    }

    @Override
    public void onResume(){
        super.onResume();
        //Log.d("XXXX","onResume() : " + index);
    }

    @Override
    public void onStop(){
        super.onStop();
        //Log.d("XXXX","onStop() : " + index);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //Log.d("XXXX","onDestroy() : " + index);
    }
}
