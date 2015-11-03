package com.moobasoft.damego;

import com.moobasoft.damego.ui.fragments.RetainedFragment;
import com.moobasoft.damego.ui.presenters.base.BasePresenter;

import org.junit.Test;

import java.util.UUID;

public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {

        RetainedFragment retainer = new RetainedFragment();

        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        UUID uuid3 = UUID.randomUUID();

        retainer.put(uuid1, new Testenter("one"));
        retainer.put(uuid2, new Testenter("two"));
        retainer.put(uuid3, new Testenter("three"));

        System.out.println(retainer.get(uuid1));
        System.out.println(retainer.get(uuid2));
        System.out.println(retainer.get(uuid3));

    }

    class Testenter extends BasePresenter {

        private final String name;

        public Testenter(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}