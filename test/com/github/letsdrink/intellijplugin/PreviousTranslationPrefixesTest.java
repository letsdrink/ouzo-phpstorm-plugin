package com.github.letsdrink.intellijplugin;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class PreviousTranslationPrefixesTest {
    @Test
    public void shouldReturnLast3Prefixes() {
        //given
        PreviousTranslationPrefixes prefixes = new PreviousTranslationPrefixes();

        prefixes.addForKey("user.name");
        prefixes.addForKey("agent.surname");
        prefixes.addForKey("admin.age");
        prefixes.addForKey("client.number");

        //when
        List<String> list = prefixes.getList();

        //then
        assertEquals(asList("client.", "admin.", "agent."), list);
    }
}