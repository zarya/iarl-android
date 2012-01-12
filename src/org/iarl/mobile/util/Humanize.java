package org.iarl.mobile.util;

public class Humanize {
    public static String frequency(long freq) {
        return frequency(freq, false);
    }

    public static String frequency(long freq, boolean shorten) {
        String sign = "";
        if (freq < 0) {
            freq = freq * -1;
            sign = "-";
        }

        if (freq < 1000) {
            return sign + String.valueOf(freq) + "Hz";
        } else if (freq < 1000000) {
            long kHz = (freq / 1000);
            long Hz = (freq - (kHz * 1000));
            if (!shorten || Hz > 0) {
                return sign + String.format("%d.%03dkHz", kHz, Hz);
            } else {
                return sign + String.format("%d  kHz", kHz);
            }
        } else if (freq < 1000000000) {
            long MHz = (freq / 1000000);
            long kHz = (freq - (MHz * 1000000)) / 1000;
            long Hz = (freq - (MHz * 1000000) - (kHz * 1000));
            if (!shorten || kHz > 0) {
                String remain = "  ";
                if (Hz != 0) {
                    while (Hz > 10) {
                        Hz = Hz / 10;
                    }
                    remain = "," + String.valueOf(Hz);
                }
                return sign + String.format("%d.%03d%sMHz", MHz, kHz, remain);
            } else {
                return sign + String.format("%d  MHz", MHz);
            }
        } else if (freq < 1000000000000l) {
            long GHz = (freq / 1000000000);
            long MHz = (freq - (GHz * 1000000000)) / 1000000;
            long kHz = (freq - (GHz * 1000000000) - (MHz * 1000000));
            if (!shorten || MHz > 0) {
                String remain = "  ";
                if (kHz != 0) {
                    while (kHz > 10) {
                        kHz = kHz / 10;
                    }
                    remain = "," + String.valueOf(kHz);
                }
                return sign + String.format("%d.%03d%sGHz", GHz, MHz, remain);
            } else {
                return sign + String.format("%d  GHz", GHz);
            }
        } else {
            return "overflow";
        }
    }
}
