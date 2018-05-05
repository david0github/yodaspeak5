package edu.quinnipiac.app.yoda;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RandomFragment extends Fragment {

    private TextView random;
    private String translation;

    public RandomFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_random, container, false);
        TextView random = (TextView) view.findViewById(R.id.randomResult);
        translation = ResultActivity.output;
        random.setText(translation);
        return view;
    }

}
