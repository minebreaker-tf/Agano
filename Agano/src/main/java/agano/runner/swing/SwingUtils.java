package agano.runner.swing;

import java.awt.*;

public final class SwingUtils {

    private SwingUtils() {}

    public static Color fromRGB(int r, int g, int b) {
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }


}
