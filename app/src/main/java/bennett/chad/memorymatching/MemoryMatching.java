package bennett.chad.memorymatching;

import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryMatching extends Fragment {

    private SharedPreferences prefs;

    private LinearLayout gameLayout;

    private List<Card> cards;
    private int[] gameSize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        cards = new ArrayList<>();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_memory_matching, container, false);

        gameLayout = (LinearLayout) view.findViewById(R.id.game_layout);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                reset();
            }
        });

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
        gameLayout.removeAllViews();

        int cardWidth = gameLayout.getWidth() / gameSize[0];
        int cardHeight = gameLayout.getHeight() / gameSize[1];

        for (int r = 0; r < gameSize[1]; r++) {
            LinearLayout row = new LinearLayout(this.getContext());
            gameLayout.addView(row);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
            row.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
            row.requestLayout();

            for (int c = 0; c < gameSize[0]; c++) {
                Card card = cards.get(r * gameSize[0] + c);
                ImageView cardImage = new ImageView(this.getContext());

                cardImage.setImageResource(card.getBack());
                row.addView(cardImage);
                cardImage.getLayoutParams().width = cardWidth;
                cardImage.getLayoutParams().height = cardHeight;
                cardImage.requestLayout();
            }
        }
    }

    public void reset() {
        setGameSize();
        setCards();
        displayCards();
    }

}
