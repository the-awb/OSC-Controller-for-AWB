package com.armedwithbow;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.MidiOut;
import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.TrackBank;
import com.bitwig.extension.callback.ShortMidiDataReceivedCallback;

public class ExtensionBase {
    public final ControllerHost host;
    public final HardwareSurface extensionHardwareSurface;
    public final MidiIn portIn;
    // public final MidiOut outPort;
    public final TrackBank bus_fader_bank;
 
    ExtensionBase(ControllerHost host, int BUS_SENDS_END)
    {
       this.host = host;
       this.extensionHardwareSurface = host.createHardwareSurface();
       this.portIn = host.getMidiInPort (0);
    //    this.portIn.setMidiCallback (inputCallback);
       this.bus_fader_bank = host.createEffectTrackBank (BUS_SENDS_END, 0);
       
    }
 }

 