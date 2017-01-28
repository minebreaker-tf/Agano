package agano.runner.swing;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import static com.google.common.base.Preconditions.checkNotNull;

public class SizibleImageIcon extends ImageIcon {

    public SizibleImageIcon(BufferedImage image, int newWidth, int newHeight) {
        super(ofScale(image, newWidth, newHeight));
    }

    private static BufferedImage ofScale(@Nonnull BufferedImage src, int newWidth, int newHeight) {
        checkNotNull(src);

        BufferedImage dst = new BufferedImage(newWidth, newHeight, src.getType());
        new AffineTransformOp(
                AffineTransform.getScaleInstance(
                        newHeight / src.getHeight(), // これ端数大丈夫?
                        newWidth / src.getWidth()
                ),
                AffineTransformOp.TYPE_BICUBIC
        ).filter(src, dst);

        return dst;
    }

}
