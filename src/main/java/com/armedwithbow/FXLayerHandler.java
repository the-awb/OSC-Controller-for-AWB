package com.armedwithbow;
import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.DeviceBank;
import com.bitwig.extension.controller.api.DeviceLayer;
import com.bitwig.extension.controller.api.DeviceLayerBank;
import com.bitwig.extension.controller.api.DeviceMatcher;

import java.util.UUID;

public class FXLayerHandler {

    private final DeviceMatcher matcher;
    // public final Device fxDevice;
    public final DeviceBank fxBank;
    // public final DeviceLayerBank fxLayerBank;
    // public final int layersSize;


    FXLayerHandler(ExtensionBase base, DeviceBank sourceDeviceBank, int sourceIndex, int layersSize){
        // this.FX_LAYER_ID = ;
        // this.layersSize = layersSize;
        matcher = base.host.createBitwigDeviceMatcher(UUID.fromString("a0913b7f-096b-4ac9-bddd-33c775314b42"));
        sourceDeviceBank.setDeviceMatcher(this.matcher);
        fxBank = sourceDeviceBank;
        
        
        // fxLayerBank.itemCount().markInterested();
    }

    DeviceBank getLayerDeviceBank(ExtensionBase base, int bankIndex, int layerSize, int layerI, int bankSize){
        // base.host.println( String.valueOf(fxLayerBank.itemCount().get()));
        return fxBank.getItemAt(bankIndex).createLayerBank(layerSize).getItemAt(layerI).createDeviceBank(bankSize);
        // return fxLayerBank.getItemAt(layerI).createDeviceBank(bankSize);
    }

    Device getFXDevice(int bankIndex){
        return fxBank.getItemAt(bankIndex);
    }
}


// // cursorDevice = track.createCursorDevice(name);
//         deviceBank = track.createDeviceBank(deviceBankSize);
//         try{
//             UUID deviceUUID = UUID.fromString(deviceIdString);
//             // assertThrows
            
//         catch(Exception e) { matcher = base.host.createVST3DeviceMatcher(deviceIdString); };
//         // if(isVST3){matcher = base.host.createVST3DeviceMatcher(deviceIdString);} else {matcher = base.host.createBitwigDeviceMatcher(deviceIdString);}
//         deviceBank.setDeviceMatcher(this.matcher);
        
//         // device = deviceBank.getItemAt(matcherIndex);

//         ExtensionBase base, String name, String deviceIdString, int bankSize, int matcherIndex, int slot