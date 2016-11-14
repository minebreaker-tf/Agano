package agano.ipmsg;

import com.google.common.base.Splitter;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
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
                    Long.valueOf(packet.get(1)),
                    packet.get(2),
                    packet.get(3),
                    Long.valueOf(packet.get(4)),
                    packet.get(5),
                    port,
                    Collections.emptyList() // TODO
            );

        } catch (IndexOutOfBoundsException e) {
            throw new MalformedMessageException(message);
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
