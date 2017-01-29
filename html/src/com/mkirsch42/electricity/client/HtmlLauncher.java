package com.mkirsch42.electricity.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.mkirsch42.electricity.ElectricitySim;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(1440, 900);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new ElectricitySim();
        }
}