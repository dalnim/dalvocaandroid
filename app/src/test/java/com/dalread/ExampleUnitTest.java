package com.dalread;

import static org.junit.Assert.assertEquals;

import com.dalread.util.Utils;

import org.junit.Test;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testGetLang() {
//        assertEquals("ENGLISH", Utils.convertIndexToLanguage(4));
//        assertEquals("en-US", Utils.convertIndexToVoiceLang(4));
    }

    @Test
    public void testCutJson() {
        assertEquals("", Utils.cutWordJson(""));
        assertEquals("", Utils.cutWordJson("{}"));
        assertEquals("", Utils.cutWordJson("{{}}"));
        assertEquals("{}", Utils.cutWordJson("{:{}}"));
        assertEquals("{}", Utils.cutWordJson("{data=:{}}"));
        assertEquals("{123}", Utils.cutWordJson("{data=:{123}}"));
    }
}