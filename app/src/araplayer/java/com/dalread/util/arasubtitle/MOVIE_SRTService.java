package com.dalread.util.arasubtitle;

import java.io.File;
import java.util.Set;

public class MOVIE_SRTService extends AbstractMOVIE_SRTBracketSubtitleService {
    protected Set<SRTLine> getSRTLines(File file) {
        SRTParser parser = new SRTParser();
        SRTSub subtitle = parser.parse(file);
        return subtitle.getLines();
    }
}
