package agano.ipmsg;

import agano.util.AganoException;

import java.util.EnumSet;

public final class FileInfo {

    private final long code;
    private final FileType fileType;
    private final EnumSet<Attribute> attributes;

    public FileInfo(FileType fileType) {
        this.fileType = fileType;
        this.attributes = EnumSet.noneOf(Attribute.class);
        this.code = fileType.getCode();
    }

    public FileInfo(FileType fileType, EnumSet<Attribute> attribute) {
        this.fileType = fileType;
        this.attributes = attribute;

        long code = fileType.getCode();
        for (Attribute attr : attributes) {
            code |= attr.getCode();
        }

        this.code = code;
    }

    public FileInfo(long raw) {
        this.code = raw;
        this.fileType = FileType.find(code).orElseThrow(() -> new AganoException("Unrecognizable file type specified."));
        this.attributes = Attribute.find(raw);
    }

    public long getCode() {
        return code;
    }

    public FileType getFileType() {
        return fileType;
    }

    public EnumSet<Attribute> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return Long.toHexString(code);
    }

}
