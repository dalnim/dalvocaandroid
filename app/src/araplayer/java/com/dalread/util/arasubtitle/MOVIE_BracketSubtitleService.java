package com.dalread.util.arasubtitle;

import java.io.File;
import java.util.Set;

public class MOVIE_BracketSubtitleService extends AbstractMOVIE_SRTBracketSubtitleService {
    @Override
    protected Set<SRTLine> getSRTLines(File file) {
        BracketSubtitleParser parser = new BracketSubtitleParser();
        SRTSub subtitle = parser.parse(file);
        return subtitle.getLines();
    }

}

