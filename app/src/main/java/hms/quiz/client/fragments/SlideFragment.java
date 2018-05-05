package hms.quiz.client.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import hms.quiz.client.R;


public class SlideFragment extends Fragment {
    public static final String EXTRA_POSITION = "EXTRA_POSITION";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_slide_page, container, false);

        int position = getArguments().getInt(EXTRA_POSITION);
        Button button = rootView.findViewById(R.id.slide_but1);
        if (position == 6) {
           // Random rnd = new Random();
           // int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
           // rootView.setBackgroundColor(color); //set activity_play_radio_button_selected_state random color to the background
            button.setVisibility(View.VISIBLE);
        } else {
            //set activity_play_radio_button_selected_state random color to the background
           // Random rnd = new Random();
          //  int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
           // rootView.setBackgroundColor(color); //set activity_play_radio_button_selected_state random color to the background
        }
        return rootView; //return the slide view
    }
}