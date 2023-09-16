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
      modulesMap.put(key, new DynamicTrackModule(base, this.startIndex+trackOffset, key, midiChannel, bussesToMap, mapBusSends, mapFXSends, mapFader, groupTrackBank.getItemAt(startIndex+trackOffset)) );

   }
    
    


    // setup sliders for bus tracks associated with the group
    public void createBusFaderMappings()
    {
       for (DynamicTrackModule mod : modulesMap.values()){


          mod.createBusFaderMappings(base);
       }
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