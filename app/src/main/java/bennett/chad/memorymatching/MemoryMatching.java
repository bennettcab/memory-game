package bennett.chad.memorymatching;

import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryMatching extends Fragment {

    private TextView cardsTextView;
    private SharedPreferences prefs;
    private ImageView cardImageView;
    private List<Card> cards;
    private int[] gameSize;

    private static final String TAG = "MemoryMatching";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        cards = new ArrayList<>();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memory_matching, container, false);

        cardsTextView = (TextView) view.findViewById(R.id.cards_text);
        cardImageView = (ImageView) view.findViewById(R.id.card1);

        cardImageView.setImageResource(R.drawable.card_back);

        reset();

        return view;
    }

    public void setGameSize() {
        String[] gameSizeStrings = prefs.getString("pref_game_size", "2x2").split("x");

        gameSize = new int[gameSizeStrings.length];
        for (int i = 0; i < gameSizeStrings.length; i++) {
            gameSize[i] = Integer.parseInt(gameSizeStrings[i]);
        }
    }

    public void setCards() {
        cards.clear();

        int totalCards = gameSize[0] * gameSize[1];

        for (int i = 0; i < totalCards; i++) {
            int cardValue = i < totalCards / 2 ? i : i - totalCards / 2;

            cards.add(new Card(R.drawable.card_back, cardValue));
        }

        Collections.shuffle(cards);
    }

    public void displayCards() {
        StringBuilder cardsString = new StringBuilder(cards.size());
        int rowCount = 0;

        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            String separator;
            rowCount++;

            if (rowCount == gameSize[0]) {
                separator = "\n";
                rowCount = 0;
            } else {
                separator = " ";
            }

            cardsString.append(Integer.toString(card.getValue()) + separator);
        }

        cardsTextView.setText(cardsString);
    }

    public void reset() {
        setGameSize();
        setCards();
        displayCards();
    }

}
