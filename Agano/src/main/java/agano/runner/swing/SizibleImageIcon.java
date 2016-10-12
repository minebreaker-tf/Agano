package agano.runner.swing;

import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class SizibleImageIcon extends ImageIcon {

    public SizibleImageIcon(BufferedImage image, int newWidth, int newHeight) {
        super(ofScale(image, newWidth, newHeight));
    }

    private static BufferedImage ofScale(BufferedImage src, int newWidth, int newHeight) {
        BufferedImage dst = new BufferedImage(newWidth, newHeight, src.getType());
        new AffineTransformOp(
                AffineTransform.getScaleInstance(
                        newHeight / src.getHeight(),
                        newWidth / src.getWidth()
                ),
                AffineTransformOp.TYPE_BICUBIC
        ).filter(src, dst);

        return dst;
    }

}
