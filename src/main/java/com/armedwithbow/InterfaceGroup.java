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
    public UIModule[] modules;
    public CursorTrack cursorTrack;
    // public final HardwareSurface extentionHardwareSurface;
 
    InterfaceGroup(String name, String type, int group_size, int start_index, UIModule[] modules, Boolean useFlatTrack)
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
       for(UIModule mod: this.modules) {
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
       for(UIModule mod : this.modules) {
          if(mod.map_busses || mod.map_fx) {
             mod.track = groupTrackBank.getItemAt(mod.index);
             mod.sends = mod.track.sendBank();
             base.host.println("track sends: " + String.valueOf(mod.sends.getSizeOfBank()));
          }
 
          // map bus sends
          if(mod.map_busses) {
 
             mod.bus_knobs = new AbsoluteHardwareKnob[TOTAL_BUS_TRACKS];
             for (int k = 0; k < TOTAL_BUS_TRACKS; k++) {
                
                   mod.bus_knobs[k] = base.extensionHardwareSurface.createAbsoluteHardwareKnob("ABS_KNOB_"+mod.name.toUpperCase()+"_BUS_"+k);
                   mod.bus_knobs[k].setAdjustValueMatcher (base.portIn.createAbsoluteCCValueMatcher (mod.midi_channel, BUS_SENDS_BASE + k));
                   mod.bus_knobs[k].setBinding (mod.sends.getItemAt (k+ BUS_SENDS_START).value());
 
                   if(mod.sends.getSizeOfBank() < k + BUS_SENDS_START) {
                      break;
                   }
                   // mod.bus_knobs[k]
             }
          }
 
          // map fx sends
          if(mod.map_fx) {
             mod.fx_knobs = new AbsoluteHardwareKnob[TOTAL_FX_TRACKS];
             for (int k = 0; k < TOTAL_FX_TRACKS; k++) {
 
                   mod.fx_knobs[k] = base.extensionHardwareSurface.createAbsoluteHardwareKnob("ABS_KNOB_"+mod.name.toUpperCase()+"_FX_"+k);
                   mod.fx_knobs[k].setAdjustValueMatcher (base.portIn.createAbsoluteCCValueMatcher (mod.midi_channel, FX_SENDS_BASE + k));
                   mod.fx_knobs[k].setBinding (mod.sends.getItemAt (k + FX_SENDS_START).value());
                   if(mod.sends.getSizeOfBank() < k + FX_SENDS_START) {
                      break;
                   }
                }
          }

          //create cursor

          this.cursorTrack = base.host.createCursorTrack(this.name+"_CURSOR", this.name+"_cursor_track", TOTAL_BUS_TRACKS + TOTAL_FX_TRACKS, 0, false);
          
      }
      
    //   return;
    }


 
    // setup sliders for bus tracks associated with the group
    public int setupSliders(final ExtensionBase base, HardwareSlider[] sliders, int slider_i, int BUS_FADERS_BASE)
    {
       for(UIModule mod : modules) {
          if(mod.map_fader){
             sliders[slider_i] = base.extensionHardwareSurface.createHardwareSlider (mod.name.toUpperCase()+"_FADER");
             sliders[slider_i].setAdjustValueMatcher (base.portIn.createAbsoluteCCValueMatcher (mod.midi_channel, 9));
             sliders[slider_i].setBinding (groupTrackBank.getItemAt (mod.index).volume ());
             slider_i++;
          }
          for (int j = 0; j < mod.busses.length; j++) {
             sliders[slider_i] = base.extensionHardwareSurface.createHardwareSlider ("SLIDER_"+mod.name.toUpperCase()+"_"+j);
             sliders[slider_i].setAdjustValueMatcher (base.portIn.createAbsoluteCCValueMatcher (mod.midi_channel, BUS_FADERS_BASE + j));
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