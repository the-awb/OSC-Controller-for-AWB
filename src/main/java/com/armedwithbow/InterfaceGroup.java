package com.armedwithbow;

import com.bitwig.extension.controller.api.TrackBank;
import com.bitwig.extension.controller.api.BooleanValue;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.HardwareSlider;
import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import java.lang.System;
import java.util.HashMap;
import java.util.Map;

public class InterfaceGroup {
    public final String name;
    public final String type;
    public final int groupSize;
    public final int startIndex;
    public final Boolean useFlatTrack;
    public TrackBank groupTrackBank;
    public Map<String, DynamicTrackModule> modulesMap;
    private ExtensionBase base;
 
   // InterfaceGroup(ExtensionBase base, String name, String type, int group_size, int start_index, DynamicTrackModule[] modules, Boolean useFlatTrack)
   InterfaceGroup(ExtensionBase base, String name, String type, int groupSize, int startIndex, Boolean useFlatTrack)
   {
      this.name = name;
      this.groupSize = groupSize;
      this.startIndex = startIndex;
      this.type = type;
      this.modulesMap = new HashMap<String, DynamicTrackModule>();
      this.useFlatTrack = useFlatTrack;
      this.base = base;

      // always mapping sends on groupTrack
      // this.groupTrackBank = base.host.createMainTrackBank(groupSize, base.TOTAL_BUS_TRACKS + base.TOTAL_FX_TRACKS, 0);
      
      switch(type){
         case "track" : groupTrackBank = base.host.createMainTrackBank(this.groupSize, base.TOTAL_BUS_TRACKS + base.TOTAL_FX_TRACKS, 0);
            break;
         case "mixed" : groupTrackBank = base.host.createTrackBank(this.groupSize, base.TOTAL_BUS_TRACKS + base.TOTAL_FX_TRACKS, 0, useFlatTrack);
            break;
         case "fx" : groupTrackBank = base.host.createEffectTrackBank(base.TOTAL_BUS_TRACKS + base.TOTAL_FX_TRACKS, 0);
            break;
         }
       
   }


      public void addModule(String key, int trackOffset, int midiChannel, int[] bussesToMap, boolean mapBusSends, boolean mapFXSends, boolean mapFader){
      modulesMap.put(key, new DynamicTrackModule(this.startIndex+trackOffset, key, midiChannel, bussesToMap, mapBusSends, mapFXSends, mapFader, groupTrackBank.getItemAt(startIndex+trackOffset)) );

   }
    
    
   public void createSendMappings()
    {
      for (Map.Entry<String, DynamicTrackModule> modEntry : modulesMap.entrySet()){
      //  for(DynamicTrackModule mod: this.modulesMap) {
          base.host.println(modEntry.getValue().display());
         }
         
       
       for(DynamicTrackModule mod : modulesMap.values()) {
         mod.setupSends(base, this, base.TOTAL_BUS_TRACKS, base.BUS_SENDS_START, base.TOTAL_FX_TRACKS, base.FX_SENDS_START);
      }
      
    }


    // setup sliders for bus tracks associated with the group
    public int setupSliders(final ExtensionBase base, HardwareSlider[] sliders, int slider_i)
    {
       for (DynamicTrackModule mod : modulesMap.values()){
          if(mod.map_fader){
             sliders[slider_i] = base.extensionHardwareSurface.createHardwareSlider (mod.name.toUpperCase()+"_FADER");
             sliders[slider_i].setAdjustValueMatcher (base.oscPortIn.createAbsoluteCCValueMatcher (mod.midi_channel, 9));
             sliders[slider_i].setBinding (groupTrackBank.getItemAt (mod.index).volume ());
             slider_i++;
          }
          for (int j = 0; j < mod.busses.length; j++) {
             sliders[slider_i] = base.extensionHardwareSurface.createHardwareSlider ("SLIDER_"+mod.name.toUpperCase()+"_"+j);
             sliders[slider_i].setAdjustValueMatcher (base.oscPortIn.createAbsoluteCCValueMatcher (mod.midi_channel, base.BUS_FADERS_BASE + j));
             sliders[slider_i].setBinding (base.bus_fader_bank.getItemAt (mod.busses[j]).volume ());
             
             
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