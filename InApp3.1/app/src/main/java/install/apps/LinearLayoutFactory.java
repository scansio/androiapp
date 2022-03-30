package install.apps;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LinearLayoutFactory {

    public static class Builder {
        private final Context context;
        private final LinearLayout layout;
        private boolean setPadding;
        private boolean setGravity;
        private boolean setBackground;
        private boolean setSize;
        private boolean setVisibility;
        private boolean setOrientation;

        public Builder(Context context) {
            this.context = context;
            layout = new LinearLayout(context);
        }

        @TargetApi(Build.VERSION_CODES.N)
        public static LinearLayout buildFrom(Context ct, LinearLayout copy) {
            return new Builder(ct)
                    .setPadding(
                            copy.getPaddingLeft(),
                            copy.getPaddingTop(),
                            copy.getPaddingRight(),
                            copy.getPaddingBottom())
                    .setSize(copy.getWidth(), copy.getHeight())
                    .setBackground(copy.getBackground())
                    .setVisibility(copy.getVisibility())
                    .setGravity(copy.getGravity())
                    .setOrientation(copy.getOrientation())
                    .build();
        }

        public Builder setPadding(int left, int top, int right, int bottom) {
            layout.setPadding(left, top, right, bottom);
            setPadding = true;
            return this;
        }

        public Builder setGravity(int gravity) {
            layout.setGravity(gravity);
            setGravity = true;
            return this;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public Builder setBackground(int res) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                layout.setBackground(context.getDrawable(res));
            }
            setBackground = true;
            return this;
        }

        public Builder setBackground(Drawable res) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                layout.setBackground(res);
            }
            setBackground = true;
            return this;
        }

        public Builder setBackgroundColor(int res) {
            layout.setBackgroundColor(res);
            setBackground = true;
            return this;
        }

        public Builder setSize(int width, int height) {
            layout.setLayoutParams(new ViewGroup.LayoutParams(width, height));
            setSize = true;
            return this;
        }

        public Builder setVisibility(int visibility) {
            layout.setVisibility(visibility);
            setVisibility = true;
            return this;
        }

        public Builder setOrientation(int orientation) {
            layout.setOrientation(orientation);
            setOrientation = true;
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
            if (!setSize) setSize(-1, -2);
            if (!setGravity) setGravity(Gravity.CENTER);
            if (!setPadding) setPadding(5, 5, 5, 5);
            if (!setVisibility) setVisibility(View.VISIBLE);
            if (!setBackground) setBackground(null);
            if (!setOrientation) setOrientation(0);
            return layout;
        }
    }
}
