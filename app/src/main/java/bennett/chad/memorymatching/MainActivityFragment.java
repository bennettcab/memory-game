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
import java.util.concurrent.TimeUnit;

public class MainActivityFragment extends Fragment {

    private SharedPreferences prefs;

    private LinearLayout gameLayout;

    private List<Card> cards;
    private int[] gameSize;
    private int revealTimeInMillis;
    private long gameStartTime;
    private int incorrectGuesses;

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

    public void setGameParams() {
        gameStartTime = System.currentTimeMillis();
        incorrectGuesses = 0;
        unmatchedVisibleCard = null;
        revealTimeInMillis = Integer.valueOf(prefs.getString("pref_reveal_time", "1000"));

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
        setGameParams();
        setCards();
        displayCards();
        setAllCardActions();
    }

    public int getRevealTimeInMillis() {
        return revealTimeInMillis;
    }

    public void addIncorrectGuess() {
        incorrectGuesses++;
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
        long gameTimeMillis = System.currentTimeMillis() - gameStartTime;
        long gameTimeHours = TimeUnit.MILLISECONDS.toHours(gameTimeMillis);
        long gameTimeMinutes = TimeUnit.MILLISECONDS.toMinutes(gameTimeMillis) % TimeUnit.HOURS.toMinutes(1);
        long gameTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(gameTimeMillis) % TimeUnit.MINUTES.toSeconds(1);
        String hoursLabel = gameTimeHours == 1 ? "hour" : "hours";
        String minutesLabel = gameTimeMinutes == 1 ? "minute" : "minutes";
        String secondsLabel = gameTimeSeconds == 1 ? "second" : "seconds";
        String gameTime;

        if (gameTimeHours > 0) {
            gameTime = String.format("%d " + hoursLabel + " %d " + minutesLabel + " %d " + secondsLabel, gameTimeHours, gameTimeMinutes, gameTimeSeconds);
        }
        else if (gameTimeMinutes > 0) {
            gameTime = String.format("%d " + minutesLabel + " %d " + secondsLabel, gameTimeMinutes, gameTimeSeconds);
        }
        else {
            gameTime = String.format("%d " + secondsLabel, gameTimeSeconds);
        }

        String matchesLabel = incorrectGuesses == 1 ? "wrong match" : "wrong matches";

        new AlertDialog.Builder(getContext()).setMessage("Good game! You finished in " + gameTime + " and had " + incorrectGuesses + " " + matchesLabel + ".")
            .setCancelable(false).setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    reset();
                }
        }).show();
    }
}
