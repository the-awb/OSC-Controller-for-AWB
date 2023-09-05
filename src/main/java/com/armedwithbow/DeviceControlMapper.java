package com.armedwithbow;
import com.bitwig.extension.controller.api.CursorDevice;
import com.bitwig.extension.controller.api.CursorRemoteControlsPage;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.DeviceBank;
import com.bitwig.extension.controller.api.DeviceMatcher;
import com.bitwig.extension.controller.api.Track;

import java.util.UUID;

import com.bitwig.extension.controller.api.AbsoluteHardwareKnob;
import com.bitwig.extension.controller.api.ControllerHost;

public class DeviceControlMapper {
    private final CursorDevice cursorDevice;
    private final DeviceBank deviceBank;
    private DeviceMatcher matcher;
    private final Device device;
    private final CursorRemoteControlsPage remoteControlsPage;
    static final int BANKSIZE = 8;
    public final AbsoluteHardwareKnob[] paramKnobs;

    DeviceControlMapper(ExtensionBase base, Track track, String name, String deviceIdString, int matcherIndex, DynamicTrackModule mod, int MIDI_BASE_CC){
        cursorDevice = track.createCursorDevice(name);
        deviceBank = track.createDeviceBank(1);
        try{
            UUID deviceUUID = UUID.fromString(deviceIdString);
            // assertThrows
            matcher = base.host.createBitwigDeviceMatcher(deviceUUID);}
        catch(Exception e) { matcher = base.host.createVST3DeviceMatcher(deviceIdString); };
        // if(isVST3){matcher = base.host.createVST3DeviceMatcher(deviceIdString);} else {matcher = base.host.createBitwigDeviceMatcher(deviceIdString);}
        deviceBank.setDeviceMatcher(this.matcher);
        device = deviceBank.getItemAt(matcherIndex);
        remoteControlsPage = device.createCursorRemoteControlsPage(BANKSIZE);
        cursorDevice.selectDevice(device);

        paramKnobs = new AbsoluteHardwareKnob[BANKSIZE];

        for (int i=0; i<BANKSIZE;i++){
            paramKnobs[i] = base.extensionHardwareSurface.createAbsoluteHardwareKnob("ABS_KNOB_"+name.toUpperCase()+"_DEVICE_P"+i);
            paramKnobs[i].setAdjustValueMatcher (base.scPortIn.createAbsoluteCCValueMatcher (mod.midi_channel, MIDI_BASE_CC + i));
            paramKnobs[i].setBinding (remoteControlsPage.getParameter(i));
            remoteControlsPage.getParameter(i).displayedValue().addValueObserver(val -> {base.host.println(val);});
         };

    }
}
