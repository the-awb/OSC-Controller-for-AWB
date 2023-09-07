package com.armedwithbow;

import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.DeviceBank;
import com.bitwig.extension.controller.api.DeviceLayer;
import com.bitwig.extension.controller.api.DeviceLayerBank;
import com.bitwig.extension.controller.api.DeviceMatcher;

import java.util.UUID;
public class InstrumentLayerHandler {
    private final DeviceMatcher matcher;
    // public final Device fxDevice;
    public final DeviceBank instBank;
    // public final DeviceLayerBank fxLayerBank;
    // public final int layersSize;


    InstrumentLayerHandler(ExtensionBase base, DeviceBank sourceDeviceBank){
        matcher = base.host.createBitwigDeviceMatcher(UUID.fromString("5024be2e-65d6-4d40-bbfe-8b2ea993c445"));
        sourceDeviceBank.setDeviceMatcher(this.matcher);
        instBank = sourceDeviceBank;
        
        
        // fxLayerBank.itemCount().markInterested();
    }

    DeviceBank getLayerDeviceBank(ExtensionBase base, int bankIndex, int layerSize, int layerI, int bankSize){
        // base.host.println( String.valueOf(fxLayerBank.itemCount().get()));
        return instBank.getItemAt(bankIndex).createLayerBank(layerSize).getItemAt(layerI).createDeviceBank(bankSize);
        // return fxLayerBank.getItemAt(layerI).createDeviceBank(bankSize);
    }

    Device getInstDevice(int bankIndex){
        return instBank.getItemAt(bankIndex);
    }
}



