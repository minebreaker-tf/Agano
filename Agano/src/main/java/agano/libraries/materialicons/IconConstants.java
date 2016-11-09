package agano.libraries.materialicons;

public enum IconConstants {

    SEND("\ue163"),
    SEARCH("\ue8b6"),
    REFRESH("\ue5d5"),
    FILE("\ue24d"),
    DOWNLOAD("\ue2c4"),
    PHOTO("\ue251"),;

    private final String codePoint;

    IconConstants(String codePoint) {
        this.codePoint = codePoint;
    }

    public String getCodePoint() {
        return codePoint;
    }

}
