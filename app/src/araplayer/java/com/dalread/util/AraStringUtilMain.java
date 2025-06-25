package com.dalread.util;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;

import org.apache.commons.io.FilenameUtils;

import java.security.SecureRandom;

public class AraStringUtilMain {
    public static SpannableString getSubtitleNameWithBoldExtension(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return new SpannableString("");
        }
        SupportSubtitleFormat type = FileUtil.getSubtitleExtension(fileName);
        if (type == SupportSubtitleFormat.NONE) {
            return new SpannableString(fileName);
        }

        String fileNameWithoutExt = FilenameUtils.removeExtension(fileName);
        String fileExtension = FilenameUtils.getExtension(fileName);

        SpannableString spannableString = new SpannableString(fileNameWithoutExt + "." + fileExtension);
        int extensionStartIndex = fileNameWithoutExt.length() + 1; // Add 1 for the dot separator
        int extensionEndIndex = extensionStartIndex + fileExtension.length();

        spannableString.setSpan(new StyleSpan(Typeface.BOLD), extensionStartIndex, extensionEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }
}