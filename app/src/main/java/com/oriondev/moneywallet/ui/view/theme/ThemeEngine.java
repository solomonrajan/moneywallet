/*
 * Copyright (c) 2018.
 *
 * This file is part of MoneyWallet.
 *
 * MoneyWallet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MoneyWallet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MoneyWallet.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.oriondev.moneywallet.ui.view.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.annotation.UiThread;
import androidx.core.graphics.ColorUtils;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible to dynamically theme the user interface at runtime.
 */
public class ThemeEngine implements ITheme {

    private static final String FILE_NAME = "theme.config";
    private static final String COLOR_PRIMARY = "color_primary";
    private static final String COLOR_PRIMARY_DARK = "color_primary_dark";
    private static final String COLOR_ACCENT = "color_accent";
    private static final String MODE = "mode";

    // Material Design 3 Baseline Color Palette
    private static final int DEFAULT_COLOR_PRIMARY = Color.parseColor("#6750A4");
    private static final int DEFAULT_COLOR_PRIMARY_DARK = Color.parseColor("#4F378B");
    private static final int DEFAULT_COLOR_ACCENT = Color.parseColor("#7D5260");
    private static final Mode DEFAULT_MODE = Mode.LIGHT;

    // Material Design 3 On-Surface Typography Roles [Light, Dark, Deep Dark (AMOLED)]
    private static final int[] DEFAULT_TEXT_COLOR_PRIMARY = new int[] {
            Color.parseColor("#1D1B20"), // M3 Light On-Surface
            Color.parseColor("#E6E1E5"), // M3 Dark On-Surface
            Color.parseColor("#E6E1E5")  // M3 AMOLED On-Surface
    };

    private static final int[] DEFAULT_TEXT_COLOR_PRIMARY_INVERSE = new int[] {
            Color.parseColor("#E6E1E5"),
            Color.parseColor("#1D1B20"),
            Color.parseColor("#1D1B20")
    };

    private static final int[] DEFAULT_TEXT_COLOR_SECONDARY = new int[] {
            Color.parseColor("#49454F"), // M3 Light On-Surface-Variant
            Color.parseColor("#CAC4D0"), // M3 Dark On-Surface-Variant
            Color.parseColor("#CAC4D0")  // M3 AMOLED On-Surface-Variant
    };

    private static final int[] DEFAULT_TEXT_COLOR_SECONDARY_INVERSE = new int[] {
            Color.parseColor("#CAC4D0"),
            Color.parseColor("#49454F"),
            Color.parseColor("#49454F")
    };

    private static final int[] DEFAULT_ICON_COLOR = new int[] {
            Color.parseColor("#49454F"), // M3 Light Icon (On-Surface-Variant)
            Color.parseColor("#CAC4D0"), // M3 Dark Icon
            Color.parseColor("#CAC4D0")  // M3 AMOLED Icon
    };

    private static final int[] DEFAULT_HINT_TEXT_COLOR = new int[] {
            Color.parseColor("#79747E"), // M3 Outline
            Color.parseColor("#938F99"), // M3 Dark Outline
            Color.parseColor("#938F99")
    };

    // Material Design 3 Surfaces and Containers
    private static final int[] DEFAULT_COLOR_CARD_BACKGROUND = new int[] {
            Color.parseColor("#FFFFFF"), // M3 Light Card
            Color.parseColor("#2B2930"), // M3 Dark Surface Container High
            Color.parseColor("#1D1B20")  // M3 AMOLED Surface Container Low
    };

    private static final int[] DEFAULT_COLOR_WINDOW_FOREGROUND = new int[] {
            Color.parseColor("#F3EDF7"), // M3 Light Surface Container
            Color.parseColor("#211F26"), // M3 Dark Surface Container
            Color.parseColor("#141218")  // M3 AMOLED Surface
    };

    private static final int[] DEFAULT_COLOR_WINDOW_BACKGROUND = new int[] {
            Color.parseColor("#FEF7FF"), // M3 Light Surface
            Color.parseColor("#141218"), // M3 Dark Surface
            Color.parseColor("#000000")  // AMOLED Pure Black
    };

    private static final int[] DEFAULT_COLOR_RIPPLE = new int[] {
            Color.parseColor("#1f1d1b20"), // M3 Ripple Light
            Color.parseColor("#26e6e1e5"), // M3 Ripple Dark
            Color.parseColor("#26e6e1e5")  // M3 Ripple AMOLED
    };

    private static final int[] DRAWER_BACKGROUND_COLOR = new int[] {
            Color.parseColor("#F7F2FA"), // M3 Surface Container Low
            Color.parseColor("#211F26"), // M3 Dark Surface Container
            Color.parseColor("#141218")
    };

    private static final int[] DRAWER_ICON_COLOR = new int[] {
            Color.parseColor("#49454F"), // M3 On-Surface-Variant
            Color.parseColor("#CAC4D0"),
            Color.parseColor("#CAC4D0")
    };

    private static final int[] DRAWER_TEXT_COLOR = new int[] {
            Color.parseColor("#1D1B20"), // M3 On-Surface
            Color.parseColor("#E6E1E5"),
            Color.parseColor("#E6E1E5")
    };

    private static final int[] DRAWER_SELECTED_ITEM_COLOR = new int[] {
            Color.parseColor("#E8DEF8"), // M3 Secondary Container
            Color.parseColor("#4A4458"), // M3 Dark Secondary Container
            Color.parseColor("#36343B")
    };

    private static final int INDEX_MODE_LIGHT = 0;
    private static final int INDEX_MODE_DARK = 1;
    private static final int INDEX_MODE_DEEP_DARK = 2;

    private static final List<ThemeObserver> mThemeObserverList = new ArrayList<>();

    private static ThemeEngine sInstance;

    public static void initialize(Context context) {
        if (sInstance == null) {
            sInstance = new ThemeEngine(context);
        }
    }

    public static void registerObserver(ThemeObserver observer) {
        mThemeObserverList.add(observer);
    }

    public static void unregisterObserver(ThemeObserver observer) {
        mThemeObserverList.remove(observer);
    }

    @UiThread
    public static void setColorPrimary(int colorPrimary) {
        if (sInstance != null) {
            if (colorPrimary != sInstance.getColorPrimary()) {
                sInstance.mPreferences.edit().putInt(COLOR_PRIMARY, colorPrimary).apply();
                sInstance.mPreferences.edit().putInt(COLOR_PRIMARY_DARK, Util.darkenColor(colorPrimary)).apply();
                notifyObservers();
            }
        } else {
            throw new RuntimeException("ThemeEngine not initialized!");
        }
    }

    @UiThread
    public static void setColorAccent(int colorAccent) {
        if (sInstance != null) {
            if (colorAccent != sInstance.getColorAccent()) {
                sInstance.mPreferences.edit().putInt(COLOR_ACCENT, colorAccent).apply();
                notifyObservers();
            }
        } else {
            throw new RuntimeException("ThemeEngine not initialized!");
        }
    }

    @UiThread
    public static void setMode(Mode mode) {
        if (sInstance != null && mode != null) {
            if (mode != sInstance.getMode()) {
                sInstance.mPreferences.edit().putInt(MODE, mode.getIndex()).apply();
                notifyObservers();
            }
        } else {
            throw new RuntimeException("ThemeEngine not initialized!");
        }
    }

    @UiThread
    private static void notifyObservers() {
        if (sInstance != null) {
            for (ThemeObserver observer : mThemeObserverList) {
                observer.onThemeChanged(sInstance);
            }
        }
    }

    public static ITheme getTheme() {
        if (sInstance != null) {
            return sInstance;
        } else {
            throw new RuntimeException("ThemeEngine not initialized!");
        }
    }

    public static void applyTheme(View view, boolean propagate) {
        if (view instanceof ThemeConsumer) {
            ((ThemeConsumer) view).onApplyTheme(sInstance);
        }
        if (propagate && view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                applyTheme(((ViewGroup) view).getChildAt(i), true);
            }
        }
    }

    private final SharedPreferences mPreferences;

    private ThemeEngine(Context context) {
        mPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public int getColorPrimary() {
        return noAlpha(mPreferences.getInt(COLOR_PRIMARY, DEFAULT_COLOR_PRIMARY));
    }

    @Override
    public int getColorPrimaryDark() {
        return noAlpha(mPreferences.getInt(COLOR_PRIMARY_DARK, DEFAULT_COLOR_PRIMARY_DARK));
    }

    @Override
    public int getColorAccent() {
        return noAlpha(mPreferences.getInt(COLOR_ACCENT, DEFAULT_COLOR_ACCENT));
    }

    private int noAlpha(int color) {
        return ColorUtils.setAlphaComponent(color, 255);
    }

    @Override
    public Mode getMode() {
        switch (mPreferences.getInt(MODE, DEFAULT_MODE.getIndex())) {
            case INDEX_MODE_LIGHT:
                return Mode.LIGHT;
            case INDEX_MODE_DARK:
                return Mode.DARK;
            case INDEX_MODE_DEEP_DARK:
                return Mode.DEEP_DARK;
            default:
                // the stored value has been manually altered
                return Mode.LIGHT;
        }
    }

    @Override
    public boolean isDark() {
        return getMode() != Mode.LIGHT;
    }

    @Override
    public int getTextColorPrimary() {
        return DEFAULT_TEXT_COLOR_PRIMARY[getMode().getIndex()];
    }

    @Override
    public int getTextColorSecondary() {
        return DEFAULT_TEXT_COLOR_SECONDARY[getMode().getIndex()];
    }

    @Override
    public int getTextColorPrimaryInverse() {
        return DEFAULT_TEXT_COLOR_PRIMARY_INVERSE[getMode().getIndex()];
    }

    @Override
    public int getTextColorSecondaryInverse() {
        return DEFAULT_TEXT_COLOR_SECONDARY_INVERSE[getMode().getIndex()];
    }

    @Override
    public int getColorCardBackground() {
        return DEFAULT_COLOR_CARD_BACKGROUND[getMode().getIndex()];
    }

    @Override
    public int getColorWindowForeground() {
        return DEFAULT_COLOR_WINDOW_FOREGROUND[getMode().getIndex()];
    }

    @Override
    public int getColorWindowBackground() {
        return DEFAULT_COLOR_WINDOW_BACKGROUND[getMode().getIndex()];
    }

    @Override
    public int getColorRipple() {
        return DEFAULT_COLOR_RIPPLE[getMode().getIndex()];
    }

    @Override
    public int getIconColor() {
        return DEFAULT_ICON_COLOR[getMode().getIndex()];
    }

    @Override
    public int getHintTextColor() {
        return DEFAULT_HINT_TEXT_COLOR[getMode().getIndex()];
    }

    @Override
    public int getErrorColor() {
        return Color.RED;
    }

    @Override
    public int getDrawerBackgroundColor() {
        return DRAWER_BACKGROUND_COLOR[getMode().getIndex()];
    }

    @Override
    public int getDrawerIconColor() {
        return DRAWER_ICON_COLOR[getMode().getIndex()];
    }

    @Override
    public int getDrawerSelectedIconColor() {
        return drawerSelectedForeground();
    }

    @Override
    public int getDrawerTextColor() {
        return DRAWER_TEXT_COLOR[getMode().getIndex()];
    }

    @Override
    public int getDrawerSelectedTextColor() {
        return drawerSelectedForeground();
    }

    // The opaque black or white, not the surface's own icon or text color: those are what the
    // closed entries are already drawn in.
    private int drawerSelectedForeground() {
        int background = getDrawerSelectedItemColor();
        return Util.visibleOr(getColorPrimary(), background, getBestColor(background));
    }

    @Override
    public int getDrawerSelectedItemColor() {
        return DRAWER_SELECTED_ITEM_COLOR[getMode().getIndex()];
    }

    @Override
    public int getBestColor(int background) {
        return Util.isColorLight(background) ? Color.BLACK : Color.WHITE;
    }

    @Override
    public int getBestTextColor(int background) {
        int index = Util.isColorLight(background) ? INDEX_MODE_LIGHT : INDEX_MODE_DARK;
        return DEFAULT_TEXT_COLOR_PRIMARY[index];
    }

    @Override
    public int getBestHintColor(int background) {
        int index = Util.isColorLight(background) ? INDEX_MODE_LIGHT : INDEX_MODE_DARK;
        return DEFAULT_HINT_TEXT_COLOR[index];
    }

    @Override
    public int getVisibleColor(int color, int fallback) {
        return Util.holdsUpAnywhere(this, color) ? color : fallback;
    }

    @Override
    public int getBestIconColor(int background) {
        int index = Util.isColorLight(background) ? INDEX_MODE_LIGHT : INDEX_MODE_DARK;
        return DEFAULT_ICON_COLOR[index];
    }

    private int getColorByMode(int colorLight, int colorDark) {
        switch (mPreferences.getInt(MODE, DEFAULT_MODE.getIndex())) {
            case INDEX_MODE_LIGHT:
                return colorLight;
            default:
                return colorDark;
        }
    }

    public enum Mode {

        LIGHT(INDEX_MODE_LIGHT),
        DARK(INDEX_MODE_DARK),
        DEEP_DARK(INDEX_MODE_DEEP_DARK);

        private final int mIndex;

        /*package-local*/ Mode(int index) {
            mIndex = index;
        }

        /*package-local*/ int getIndex() {
            return mIndex;
        }
    }

    public interface ThemeObserver {

        void onThemeChanged(ITheme theme);
    }

    public interface ThemeConsumer {

        void onApplyTheme(ITheme theme);
    }
}