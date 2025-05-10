package com.armedwithbow;

import com.bitwig.extension.controller.api.CursorRemoteControlsPage;
import com.bitwig.extension.controller.api.Device;

public class EnvFollowerOscObserver {
    private final CursorRemoteControlsPage remoteControlsPage;
    private double lastValue;

    public Device device;
    public final String address;

    EnvFollowerOscObserver(Device device, ExtensionBase base, String moduleName, int index) {
        this.device = device;
        this.address = String.format("/envelopes/%s/%d", moduleName, index);
        this.lastValue = -1;

        base.host.println(address);
        remoteControlsPage = device.createCursorRemoteControlsPage(8);
        base.host.println(String.valueOf(remoteControlsPage.getParameterCount()));

        remoteControlsPage.getParameter(0).modulatedValue().markInterested();

        remoteControlsPage.getParameter(0).modulatedValue().addValueObserver(value -> {

            double roundValue = Math.round(value * 1000) / 1000.0;
            // if (Math.abs(value - lastValue) > 0.003) { // is we want to send fewer
            // messages, use this
            if (roundValue != lastValue) {
                lastValue = roundValue;
                base.sendOscMessage(address, lastValue);
            }

        });
    }

}
