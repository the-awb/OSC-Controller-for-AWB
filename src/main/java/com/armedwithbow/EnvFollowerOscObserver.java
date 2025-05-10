package com.armedwithbow;

import com.bitwig.extension.controller.api.CursorRemoteControlsPage;
import com.bitwig.extension.controller.api.Device;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.armedwithbow.ToolDeviceHandler;
import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.CursorRemoteControlsPage;

public class EnvFollowerOscObserver {
    private final CursorRemoteControlsPage remoteControlsPage;
    private double lastValue;

    public Device device;
    public final String address;

    EnvFollowerOscObserver(Device device, ExtensionBase base, String moduleName, int index) {
        this.device = device;
        this.address = String.format("/envelopes/%s/%d", moduleName, index);
        // this.address = String.format("envelopes/%/%", moduleName, index);
        this.lastValue = -1;

        base.host.println(address);
        remoteControlsPage = device.createCursorRemoteControlsPage(8);
        base.host.println(String.valueOf(remoteControlsPage.getParameterCount()));

        remoteControlsPage.getParameter(0).modulatedValue().markInterested();

        remoteControlsPage.getParameter(0).modulatedValue().addValueObserver(value -> {

            double roundValue = Math.round(value * 1000) / 1000.0;
            // if (Math.abs(value - lastValue) > 0.001) {
            if (roundValue != lastValue) {
                lastValue = roundValue;
                base.sendOscMessage(address, lastValue);
            }

        });
    }

}
