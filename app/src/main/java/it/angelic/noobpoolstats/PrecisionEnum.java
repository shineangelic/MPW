package it.angelic.noobpoolstats;

public enum PrecisionEnum {
        ONE_DIGIT("%.1f"),
        TWO_DIGIT("%.2f"),
        THREE_DIGIT("%.3f"),
        NONE("%.0f");

        private final String mFormat;
        PrecisionEnum(String format){
            this.mFormat = format;
        }

        public String getFormat(){
            return mFormat;
        }
    }