package install.apps;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ButtonFactory {

    private ButtonFactory() {
    }

    public static class Builder {
        private final Context context;
        private final Button button;
        private boolean setPadding;
        private boolean setGravity;
        private boolean setBackground;
        private boolean setSize;
        private boolean setVisibility;

        public Builder(Context context) {
            this.context = context;
            button = new Button(context);
        }

        public static Button buildFrom(Context ct, Button copy) {
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
                    .build();
        }

        public Builder setPadding(int left, int top, int right, int bottom) {
            button.setPadding(left, top, right, bottom);
            setPadding = true;
            return this;
        }

        public Builder setGravity(int gravity) {
            button.setGravity(gravity);
            setGravity = true;
            return this;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public Builder setBackground(int res) {
            button.setBackground(context.getDrawable(res));
            setBackground = true;
            return this;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public Builder setBackground(Drawable res) {
            button.setBackground(res);
            setBackground = true;
            return this;
        }

        public Builder setBackgroundColor(int res) {
            button.setBackgroundColor(res);
            setBackground = true;
            return this;
        }

        public Builder setSize(int width, int height) {
            button.setLayoutParams(new ViewGroup.LayoutParams(width, height));
            setSize = true;
            return this;
        }

        public Builder setVisibility(int visibility) {
            button.setVisibility(visibility);
            setVisibility = true;
            return this;
        }

        public Button build() {
            if (!setSize) setSize(-1, -2);
            if (!setGravity) setGravity(Gravity.CENTER);
            if (!setPadding) setPadding(5, 5, 5, 5);
            if (!setVisibility) setVisibility(View.VISIBLE);
            if (!setBackground) setBackground(null);
            return button;
        }
    }
}
