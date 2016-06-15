package agano.ipmsg;

// TODO スタブクラス
public class CommandImpl {

    IpmsgProtocolCommand command;

    public CommandImpl(IpmsgProtocolCommand command) {
        this.command = command;
    }

    // TODO オプション

    /**
     * このインスタンスが示す命令のlong値、すなわち転送される命令コードとオプションの数字
     *
     * @return long表現
     */
    public long getCode() {
        return command.getValue();
    }

    /**
     * {@code return Long.toString(getCode()); }
     *
     * @return asLongの文字列表現
     */
    public String getCodeAsString() {
        return Long.toString(getCode());
    }

    @Override
    public String toString() {
        return command.toString();
    }

}
