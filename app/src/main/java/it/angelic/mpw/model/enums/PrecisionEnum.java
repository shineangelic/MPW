package it.angelic.mpw.model.enums;

public enum PrecisionEnum {
    ONE_DIGIT("%.1f"),
    TWO_DIGIT("%.2f"),
    THREE_DIGIT("%.3f"),
    SIX_DIGIT("%.6f"),
    NONE("%.0f");

    private final String mFormat;

    PrecisionEnum(String format) {
        this.mFormat = format;
    }

    public String getFormat() {
        return mFormat;
    }
}