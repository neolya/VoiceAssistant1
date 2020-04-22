package com.example.voiceassistant2;

import com.example.voiceassistant2.dialog.AI;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;


public class getDateUnitTest {
    @Test
    public void addition_isCorrect() throws IOException {
        AI ai = new AI();
        assertEquals("24 мая 2008", ai.getDate("24.05.2008"));
    }
}