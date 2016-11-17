package agano.ipmsg;

import com.google.common.base.Splitter;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static agano.util.StringUtils.byteToString;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

public final class MessageFactory {

    private MessageFactory() {}

    // TODO 文字コード判定できるメソッドに書き直さないと
    @Deprecated
    @Nonnull
    public static Message fromByte(@Nonnull ByteBuffer originalMessage, int port) {

        checkNotNull(originalMessage);

        String converted = byteToString(originalMessage, StandardCharsets.UTF_8);

        return fromString(converted, port);
    }

    @Nonnull
    public static Message fromString(@Nonnull String message, int port) {
        checkNotNull(message);

        try {

            List<String> packet = split(message);

            return new Message(
                    packet.get(0),
                    Long.parseLong(packet.get(1)),
                    packet.get(2),
                    packet.get(3),
                    Long.parseLong(packet.get(4)),
                    packet.get(5),
                    port
            );

        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new MalformedMessageException(message, e);
        }

    }

    @Nonnull
    public static FileAttachedMessage fileAttachedMessageFromString(@Nonnull String original, int port) {
        try {
            List<String> packet = split(original);
            String load = packet.get(5);
            String[] sep = load.split("\\u0000");
            String message = sep[0];
            List<Attachment> attachments = Attachment.decodeAttachments(sep[1]);
            Operation op = new Operation(Long.parseLong(packet.get(4)));

            return new FileAttachedMessage(
                    packet.get(0),
                    Long.parseLong(packet.get(1)),
                    packet.get(2),
                    packet.get(3),
                    op,
                    message,
                    port,
                    attachments
            );
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new MalformedMessageException(original, e);
        }
    }

    @Nonnull
    public static FileSendRequestMessage fileSendRequestMessageFromString(@Nonnull String original, int port) {
        try {
            String[] packet = original.split(":");

            Operation op = new Operation(Long.parseLong(packet[4]));

            return new FileSendRequestMessage(
                    packet[0],
                    Long.parseLong(packet[1]),
                    packet[2],
                    packet[3],
                    op,
                    port,
                    Long.parseLong(packet[5], 16),
                    Long.parseLong(packet[6], 16),
                    Long.parseLong(packet[7], 16)
            );
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new MalformedMessageException(original, e);
        }
    }

    /**
     * 文字列を要素に分解する<br />
     * バージョン情報(1) : パケット番号(任意の数字) : 自User名 : 自Host名 : Command番号 : 積み荷
     *
     * @param raw 元のメッセージ
     * @return プロトコルの各要素ごとに分割された文字列の配列
     */
    @Nonnull
    private static List<String> split(String raw) {

        List<String> split = newArrayList(Splitter.on(":")
                                                  .split(raw));

        // 積み荷部分に「:」が含まれていたとき、値を結合
        while (split.size() > 6) {
            split.add(5, split.remove(5) + ":" + split.remove(5));
        }

        return split;
    }

}
