package bennett.chad.memorymatching;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

public class Card extends ImageView {

    private int back;
    private String front;
    private Drawable frontDrawable;

    public Card(Context context, int backImage, String frontImage) {
        super(context);

        back = backImage;
        front = frontImage;

        super.setImageResource(backImage);

        try (InputStream stream = context.getAssets().open("cards/" + front)) {
            frontDrawable = Drawable.createFromStream(stream, front.replace(".png", ""));
        } catch (IOException e) {
            Log.e("Card", "Error loading image file name", e);
        }
    }

    public void show() {
        setImageDrawable(frontDrawable);
    }

    public String getValue() {
        return front;
    }
}
