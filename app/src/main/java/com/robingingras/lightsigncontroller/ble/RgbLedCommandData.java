package com.robingingras.lightsigncontroller.ble;

import android.graphics.Color;

import androidx.annotation.NonNull;

import no.nordicsemi.android.ble.data.Data;
import no.nordicsemi.android.ble.data.MutableData;

public final class RgbLedCommandData {
    private static final byte OP_CODE_SET_MODE = 1;
    private static final byte OP_CODE_SET_PATTERN = 2;
    private static final byte OP_CODE_SET_COLOR = 3;

    public enum Mode {
        MODE_OFF(0x01),
        MODE_STATIC(0x02),
        MODE_PATTERN(0x03);

        final byte mode;

        Mode(final int mode) {
            this.mode = (byte) mode;
        }
    }

    public static Data setMode(@NonNull RgbLedCommandData.Mode mode) {
        final MutableData data = new MutableData(new byte[2]);
        data.setByte(OP_CODE_SET_MODE, 0);
        data.setByte(mode.mode, 1);
        return data;
    }

    public static Data setPattern(int pattern) {
        final MutableData data = new MutableData(new byte[2]);
        data.setByte(OP_CODE_SET_PATTERN, 0);
        data.setValue(pattern, Data.FORMAT_UINT8, 1);
        return data;
    }

    public static Data setColor(Color color) {
        int rgb = color.toArgb() & 0x00FFFFFF;
        final MutableData data = new MutableData(new byte[4]);
        data.setByte(OP_CODE_SET_COLOR, 0);
        data.setValue(rgb, Data.FORMAT_UINT24, 1);
        return data;
    }
}
