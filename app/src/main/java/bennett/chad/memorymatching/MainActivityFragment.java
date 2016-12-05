package bennett.chad.memorymatching;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivityFragment extends Fragment {

    private SharedPreferences prefs;

    private LinearLayout gameLayout;

    private List<Card> cards;
    private int[] gameSize;
    private int revealTimeInMillis = 1000;

    private Card unmatchedVisibleCard = null;

    public final View.OnClickListener cardListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (getUnmatchedVisibleCard() != null) {
                removeAllCardActions();
            }

            ((Card) view).clicked();

            if (isGameOver()) {
                gameOver();
            }
        }
    };

    public static final String CARDS_ASSET_NAME = "cards";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        cards = new ArrayList<>();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

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
        int totalCards = gameSize[0] * gameSize[1];

        List<String> deckOfCards = getDeckOfCards();
        Collections.shuffle(deckOfCards);

        cards.clear();

        for (int i = 0; i < totalCards; i++) {
            String cardValue = i < totalCards / 2 ? deckOfCards.get(i) : deckOfCards.get(i - totalCards / 2);

            cards.add(new Card(getContext(), R.drawable.card_back, cardValue, this));
        }

        Collections.shuffle(cards);
    }

    @NonNull
    public List<String> getDeckOfCards() {
        List<String> cardList = new ArrayList<>();

        try {
            String[] paths = getActivity().getAssets().list(CARDS_ASSET_NAME);

            for (String path : paths) {
                cardList.add(path);
            }
        } catch (IOException e) {
            Log.e("MainActivityFragment", "Error loading image file names", e);
        }

        return cardList;
    }

    public void displayCards() {
        gameLayout.removeAllViews();

        int cols = gameSize[0];
        int rows = gameSize[1];

        int cardWidth = gameLayout.getWidth() / cols;
        int cardHeight = gameLayout.getHeight() / rows;

        for (int r = 0; r < rows; r++) {
            LinearLayout row = new LinearLayout(this.getContext());
            gameLayout.addView(row);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
            row.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
            row.requestLayout();

            for (int c = 0; c < cols; c++) {
                Card card = cards.get(r * cols + c);

                row.addView(card);
                card.getLayoutParams().width = cardWidth;
                card.getLayoutParams().height = cardHeight;
                card.requestLayout();

            }
        }
    }

    public void setAllCardActions() {
        for (Card card : cards) {
            if (!card.isMatched()) {
                card.setOnClickListener(cardListener);
            }
        }
    }

    public void removeAllCardActions() {
        for (Card card : cards) {
            card.setOnClickListener(null);
        }
    }

    public Card getUnmatchedVisibleCard() {
        return unmatchedVisibleCard;
    }

    public void setUnmatchedVisibleCard(Card card) {
        unmatchedVisibleCard = card;
    }

    public void reset() {
        unmatchedVisibleCard = null;
        setGameSize();
        setCards();
        displayCards();
        setAllCardActions();
    }

    public int getRevealTimeInMillis() {
        return revealTimeInMillis;
    }

    public boolean isGameOver () {
        ArrayList<Card> matchedCards = new ArrayList<Card>();

        for (Card card : cards) {
            if (card.isMatched()) {
                matchedCards.add(card);
            }
        }

        return cards.size() == matchedCards.size();
    }

    private void gameOver() {
        new AlertDialog.Builder(getContext()).setMessage("Good game! Here's your stats: ")
            .setCancelable(false).setPositiveButton("PlayAgain", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    reset();
                }
        }).show();
    }
}
