package file.filefinder;

import android.widget.*;
import android.view.*;
import android.content.*;
import android.graphics.drawable.Drawable;

public class LinearLayoutFactory {

  private LinearLayoutFactory() {}

  public static class Builder {
    private Context context;
    private LinearLayout layout;

    public Builder(Context context) {
      this.context = context;
      layout = new LinearLayout(context);
      layout.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
      layout.setGravity(Gravity.CENTER);
      layout.setPadding(5, 5, 5, 5);
      layout.setOrientation(1);
      layout.setVisibility(View.VISIBLE);
    }

    public Builder setPadding(int left, int top, int right, int bottom) {
      layout.setPadding(left, top, right, bottom);
      return this;
    }

    public Builder setGravity(int gravity) {
      layout.setGravity(gravity);
      return this;
    }

    public Builder setOrientation(int orientation) {
      layout.setOrientation(orientation);
      return this;
    }

    public Builder setBackground(int res) {
      layout.setBackground(context.getDrawable(res));
      return this;
    }

    public Builder setBackgroundColor(int res) {
      layout.setBackgroundColor(res);
      return this;
    }
    
    public Builder setBackground(Drawable res) {
      layout.setBackground(res);
      return this;
    }

    public Builder setSize(int width, int height) {
      layout.setLayoutParams(new ViewGroup.LayoutParams(width, height));
      return this;
    }

    public Builder setVisibility(int visibility) {
      layout.setVisibility(visibility);
      return this;
    }

    public Builder addChild(View view) {
      try {
        layout.addView(view);
      } catch (Exception e) {
        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
      }
      return this;
    }

    public Builder addChildAt(View view, int index) {
      try {
        layout.addView(view, index);
      } catch (Exception e) {
        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
      }
      return this;
    }

    public LinearLayout build() {
      return layout;
    }
    
    public static LinearLayout buildFrom(Context ct, LinearLayout copy){
    int[] padding = {copy.getPaddingLeft(), copy.getPaddingTop(), copy.getPaddingRight(), copy.getPaddingBottom()};
    Drawable d = copy.getBackground();
    int visible = copy.getVisibility();
    int gravity = copy.getGravity();
    int orientation = copy.getOrientation();
    
    return new Builder(ct)
    .setPadding(padding[0], padding[1], padding[2], padding[3])
    .setSize(-2, -2)
    .setBackground(d)
    .setVisibility(visible)
    .setGravity(gravity)
    .setOrientation(orientation)
    .build();
    }
  }
}
