package bennett.chad.memorymatching;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import android.os.Handler;

public class Card extends ImageView {

    private MainActivityFragment gameFragment;

    private int back;
    private String front;
    private Drawable frontDrawable;

    private boolean shown = false;
    private boolean matched = false;

    private Handler handler;

    public Card(Context context, int backImage, String frontImage, MainActivityFragment fragment) {
        super(context);

        gameFragment = fragment;

        back = backImage;
        front = frontImage;

        handler = new Handler();

        super.setImageResource(backImage);

        try (InputStream stream = context.getAssets().open(gameFragment.CARDS_ASSET_NAME + "/" + front)) {
            frontDrawable = Drawable.createFromStream(stream, front.replace(".png", ""));
        } catch (IOException e) {
            Log.e("Card", "Error loading image file name", e);
        }
    }

    public void clicked() {
        final Card unmatchedVisibleCard = gameFragment.getUnmatchedVisibleCard();
        final Card thisCard = this;

        this.setOnClickListener(null);
        show();

        if (unmatchedVisibleCard == null) {
            gameFragment.setUnmatchedVisibleCard(this);
        }
        else if (unmatchedVisibleCard.getValue().equals(getValue())) {
            matched = true;
            unmatchedVisibleCard.setIsMatched();
            gameFragment.setUnmatchedVisibleCard(null);
            gameFragment.setAllCardActions();
        }
        else {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Log.d("Card Clicked", "Cards don't match");

                    thisCard.hide();
                    unmatchedVisibleCard.hide();

                    thisCard.gameFragment.setUnmatchedVisibleCard(null);
                    thisCard.gameFragment.setAllCardActions();
                }
            };

            handler.postDelayed(runnable, gameFragment.getRevealTimeInMillis());
        }
    }

    public void show() {
        setImageDrawable(frontDrawable);
        shown = true;
    }

    public void hide() {
        setImageResource(back);
        shown = false;
    }

    public String getValue() {
        return front;
    }

    public boolean isShown() {
        return shown;
    }

    public void setIsMatched() {
        matched = true;
    }

    public boolean isMatched() {
        return matched;
    }
}
