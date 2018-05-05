package edu.quinnipiac.app.yoda;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class YodaFragment extends Fragment {

    public TextView r;
    public Button b;
    public String translation;

    public YodaFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yoda, container, false);
        r = (TextView) view.findViewById(R.id.result);
        translation = MainActivity.output;
        r.setText(translation);
        return view;
    }

}

