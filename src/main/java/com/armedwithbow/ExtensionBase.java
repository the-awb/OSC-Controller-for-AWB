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
    public final MidiIn oscPortIn;
    public final MidiIn scPortIn;
   
    public final int TRACK_FADER_CC;
    public final int ROUTING_BASE;
    public final int CONTROLS_1_BASE;
    public final int CONTROLS_2_BASE;
    public final int CONTROLS_3_BASE;
    public final int MOD_1_BASE;
    public final int MOD_2_BASE;
    public final int MOD_3_BASE;
    public final int DIRECT_PARAM_BASE;
    public final int FX_SENDS_BASE;
    public final int BUS_SENDS_BASE;
    public final int BUS_FADERS_BASE;
    // public final MidiOut outPort;
    public final TrackBank bus_fader_bank;
 
    ExtensionBase(ControllerHost host, int BUS_SENDS_END)
    {
       this.host = host;
       this.extensionHardwareSurface = host.createHardwareSurface();
       this.oscPortIn = host.getMidiInPort (0);
       this.scPortIn = host.getMidiInPort (1);
    //    this.portIn.setMidiCallback (inputCallback);
       this.bus_fader_bank = host.createEffectTrackBank (BUS_SENDS_END, 0);

       TRACK_FADER_CC = 9;
         ROUTING_BASE = 0;
         CONTROLS_1_BASE = 10;
         CONTROLS_2_BASE = 20;
         CONTROLS_3_BASE = 30;
         MOD_1_BASE = 40;
         MOD_2_BASE = 50;
         MOD_3_BASE = 60;
         DIRECT_PARAM_BASE = 70;
         FX_SENDS_BASE = 80;
         BUS_SENDS_BASE = 100;
         BUS_FADERS_BASE = 120;
       
    }
 }

 //   "routing": 0,
//   "controls_1": 10,
//   "controls_2": 20,
//   "controls_3": 30,
//   "mod_1": 40,
//   "mod_2": 50,
//   "mod_3": 60,
//   "device_direct": 70,
//   "fx_1": 80,
//   "fx_2": 90,
//   "bus_1": 100,
//   "bus_2": 110,
//   "level": 120