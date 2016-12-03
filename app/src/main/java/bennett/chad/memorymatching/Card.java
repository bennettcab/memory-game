package bennett.chad.memorymatching;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

public class Card extends ImageView {

    private MainActivityFragment gameFragment;

    private int back;
    private String front;
    private Drawable frontDrawable;

    private boolean shown = false;
    private boolean matched = false;

    public Card(Context context, int backImage, String frontImage, MainActivityFragment fragment) {
        super(context);

        gameFragment = fragment;

        back = backImage;
        front = frontImage;

        super.setImageResource(backImage);

        try (InputStream stream = context.getAssets().open(gameFragment.CARDS_ASSET_NAME + "/" + front)) {
            frontDrawable = Drawable.createFromStream(stream, front.replace(".png", ""));
        } catch (IOException e) {
            Log.e("Card", "Error loading image file name", e);
        }
    }

    public void clicked() {
        Card unmatchedVisibleCard = gameFragment.getUnmatchedVisibleCard();

        if (unmatchedVisibleCard == null) {
            show();
            gameFragment.setUnmatchedVisibleCard(this);
        }
        else if (unmatchedVisibleCard.getValue().equals(front)) {
            show();
            matched = true;
            this.setOnClickListener(null);
            unmatchedVisibleCard.isMatched();
            unmatchedVisibleCard.setOnClickListener(null);
            gameFragment.setUnmatchedVisibleCard(null);
        }
        else {
            hide();
            unmatchedVisibleCard.hide();
            gameFragment.setUnmatchedVisibleCard(null);
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

    public boolean isMatched() {
        return matched;
    }
}
