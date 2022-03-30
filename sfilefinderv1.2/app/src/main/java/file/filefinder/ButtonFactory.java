package file.filefinder;

import android.widget.*;
import android.view.*;
import android.content.*;
import android.graphics.drawable.Drawable;

public class ButtonFactory {

  private ButtonFactory() {}

  public static class Builder {
    private Context context;
    private Button button;

    public Builder(Context context) {
      this.context = context;
      button = new Button(context);
      button.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
      button.setGravity(Gravity.CENTER);
      // button.setPadding(5, 5, 5, 5);
      button.setVisibility(View.VISIBLE);
    }

    public Builder setPadding(int top, int right, int bottom, int left) {
      button.setPadding(top, right, bottom, left);
      return this;
    }

    public Builder setGravity(int gravity) {
      button.setGravity(gravity);
      return this;
    }

    public Builder setBackground(int res) {
      button.setBackground(context.getDrawable(res));
      return this;
    }

    public Builder setBackground(Drawable res) {
      button.setBackground(res);
      return this;
    }

    public Builder setBackgroundColor(int res) {
      button.setBackgroundColor(res);
      return this;
    }

    public Builder setSize(int width, int height) {
      button.setLayoutParams(new ViewGroup.LayoutParams(width, height));
      return this;
    }

    public Builder setTextSize(int size) {
      button.setTextSize(size);
      return this;
    }

    public Builder setText(String str) {
      button.setText(str);
      return this;
    }

    public Builder setVisibility(int visibility) {
      button.setVisibility(visibility);
      return this;
    }

    public Button build() {
      return button;
    }

    public static Button buildFrom(Context ct, Button copy) {
      int[] padding = {
        copy.getPaddingLeft(), copy.getPaddingTop(), copy.getPaddingRight(), copy.getPaddingBottom()
      };
      Drawable d = copy.getBackground();
      int visible = copy.getVisibility();
      int gravity = copy.getGravity();

      return new Builder(ct)
          .setPadding(padding[0], padding[1], padding[2], padding[3])
          .setSize(-1, -1)
          .setBackground(d)
          .setVisibility(visible)
          .setGravity(gravity)
          .build();
    }
  }
}
