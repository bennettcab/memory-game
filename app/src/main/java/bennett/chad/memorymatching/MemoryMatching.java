package bennett.chad.memorymatching;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MemoryMatching extends Fragment {

    private TextView fragmentTextView;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memory_matching, container, false);

        fragmentTextView = (TextView) view.findViewById(R.id.title_text_view);

        String gameSize = prefs.getString("pref_game_size", "2x2");

        fragmentTextView.setText(gameSize);

        return view;
    }

}
