package agano.messaging;

import agano.util.AganoException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

final class FileReceivingHandler extends SimpleChannelInboundHandler<ByteBuf> {  // TODO fragmentation issue

    private static final Logger logger = LoggerFactory.getLogger(FileReceivingHandler.class);

    private final Path saveTo;
    private final int size;
    private final Component parent;
    private final Supplier<Future> onFinish;

    FileReceivingHandler(Path saveTo, int size, Component parent, Supplier<Future> onFinish) {
        this.saveTo = saveTo;
        this.size = size;
        this.parent = parent;
        this.onFinish = onFinish;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        msg.retain();

        logger.info("Receiving file. Saved to: {} Size: {}", saveTo, size);

        try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(saveTo))) { // Truncate file if exists
            msg.readBytes(bos, size); // TODO 読み込み可能な量が意外と少ないのでちゃんと書かないとだめっぽい
            JOptionPane.showMessageDialog(
                    parent,
                    "File transfer completed: " + saveTo.getFileName(),
                    "File transfer completed",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    parent,
                    "File transfer failed.: " + saveTo.getFileName(),
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );
            throw new AganoException("Failed to write file.", e);
        } catch (IndexOutOfBoundsException e) {
            throw new AganoException("Sent file size differs from claimed one.", e);
        } finally {
            msg.release();
            ctx.close();
            onFinish.get();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
        logger.warn("Error while receiving the file.", t);
        ctx.close();
        ctx.close();
        onFinish.get();
    }

}
