package com.armedwithbow;

import com.bitwig.extension.controller.api.TrackBank;

import com.bitwig.extension.controller.api.AbsoluteHardwareKnob;
import com.bitwig.extension.controller.api.HardwareSlider;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.api.util.midi.ShortMidiMessage;

public class InterfaceGroup {
    // public final ExtensionBase base;
    public final String name;
    public final String type;
    public final int group_size;
    public final int start_index;
    public final Boolean useFlatTrack;
    public TrackBank groupTrackBank;
    public DynamicTrackModule[] modules;
    public CursorTrack cursorTrack;
    // public final HardwareSurface extentionHardwareSurface;
 
    InterfaceGroup(String name, String type, int group_size, int start_index, DynamicTrackModule[] modules, Boolean useFlatTrack)
    {
       // this.base = base;
       this.name = name;
       this.group_size = group_size;
       this.start_index = start_index;
       this.type = type;
       this.modules = modules;
       this.useFlatTrack = useFlatTrack;
       
       // this.groupTrackBank = groupTrackBank;
      
       
    }
 
    // public void init(ControllerHost host, HardwareSurface extentionHardwareSurface, MidiIn port, int TOTAL_BUS_TRACKS, int TOTAL_FX_TRACKS, int BUS_SENDS_BASE, int BUS_SENDS_START, int FX_SENDS_BASE, int FX_SENDS_START, int BUS_FADERS_BASE)
    
    
       public void init(final ExtensionBase base, int TOTAL_BUS_TRACKS, int TOTAL_FX_TRACKS, int BUS_SENDS_BASE, int BUS_SENDS_START, int FX_SENDS_BASE, int FX_SENDS_START, Boolean mapBusSends, Boolean mapFxSends)
    {
       for(DynamicTrackModule mod: this.modules) {
          base.host.println(mod.display());
         }
         
       switch(type){
          case "track" : groupTrackBank = base.host.createMainTrackBank(this.group_size, (mapBusSends ? TOTAL_BUS_TRACKS : 0) + (mapFxSends ? TOTAL_FX_TRACKS : 0), 0);
             break;
          case "mixed" : groupTrackBank = base.host.createTrackBank(this.group_size, (mapBusSends ? TOTAL_BUS_TRACKS : 0) + (mapFxSends ? TOTAL_FX_TRACKS : 0), 0, useFlatTrack);
             break;
          case "fx" : groupTrackBank = base.host.createEffectTrackBank(TOTAL_BUS_TRACKS + TOTAL_FX_TRACKS, 0);
             break;
          }
         
       this.cursorTrack = base.host.createCursorTrack(this.name+"_CURSOR", this.name+"_cursor_track", TOTAL_BUS_TRACKS + TOTAL_FX_TRACKS, 0, false);

       for(DynamicTrackModule mod : this.modules) {
         mod.setupSends(base, this, TOTAL_BUS_TRACKS, BUS_SENDS_START, BUS_SENDS_BASE, TOTAL_FX_TRACKS, FX_SENDS_START, FX_SENDS_BASE);
      }
      
    //   return;
    }


 
    // setup sliders for bus tracks associated with the group
    public int setupSliders(final ExtensionBase base, HardwareSlider[] sliders, int slider_i, int BUS_FADERS_BASE)
    {
       for(DynamicTrackModule mod : modules) {
          if(mod.map_fader){
             sliders[slider_i] = base.extensionHardwareSurface.createHardwareSlider (mod.name.toUpperCase()+"_FADER");
             sliders[slider_i].setAdjustValueMatcher (base.oscPortIn.createAbsoluteCCValueMatcher (mod.midi_channel, 9));
             sliders[slider_i].setBinding (groupTrackBank.getItemAt (mod.index).volume ());
             slider_i++;
          }
          for (int j = 0; j < mod.busses.length; j++) {
             sliders[slider_i] = base.extensionHardwareSurface.createHardwareSlider ("SLIDER_"+mod.name.toUpperCase()+"_"+j);
             sliders[slider_i].setAdjustValueMatcher (base.oscPortIn.createAbsoluteCCValueMatcher (mod.midi_channel, BUS_FADERS_BASE + j));
             sliders[slider_i].setBinding (base.bus_fader_bank.getItemAt (mod.busses[j]).volume ());
             // fxBank.itemCount ().markInterested ();
             // Track fx = fxBank.getItemAt (mod.busses[j]);
             // fx.position().markInterested();
             
             
             slider_i++;
          }
       }
       return slider_i;
    }
// /** {@inheritDoc} */
    // @Override
    public boolean handleMidi (final ShortMidiMessage message)
    {
        if (message.isNoteOn ())
        {
            switch (message.getData1 ())
            {
                case 0x0A:
                    this.groupTrackBank.scrollPageBackwards();
                    return true;
                case 0x0B:
                    this.groupTrackBank.scrollPageForwards();
                    return true;
                default:
                    return false;
            }
        }
        else return false;
    }
    
 }