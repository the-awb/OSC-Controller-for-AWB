package com.armedwithbow;
import com.bitwig.extension.controller.api.AbsoluteHardwareKnob;
import com.bitwig.extension.controller.api.HardwareSlider;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.SendBank;
import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.DeviceBank;

// UI Module class
public class DynamicTrackModule {
   private DeviceControlMapper[] deviceParamMappers;
   private DeviceControlMapper[] deviceModMappers;
   private DeviceControlMapper[] deviceRoutingMappers;
   //  private DeviceParamMapper device_p3;
   //  private DeviceParamMapper device_mod1;
   //  private DeviceParamMapper device_mod2;
   //  private DeviceParamMapper device_mod3;
    
    private AbsoluteHardwareKnob[] busKnobs;
    private AbsoluteHardwareKnob[] fxKnobs;
    private HardwareSlider[] busFaders;
    private HardwareSlider fader;
    
    public final Boolean mapFader;

    public final int index;
    public final String name;
    public final int midiChannel;
    public int[] busses;
    public Track track;
    public SendBank sends;

 
    /**
     * Constructor.
     *
     * @param index the track index for the fx / audio relative to the appropriate scope
     * @param name name of the UI module
     * @param channel midi channel for the module
     * @param busses the indexes of the associated bus channels
     * @param mapBussesSends map bus sends 
     * @param mapFxSends map fx sends 
     * @param mapFader map fader of primary track
     
     */
    DynamicTrackModule(ExtensionBase base, int index, String name, int channel, int[] busses, Boolean mapBussesSends,Boolean mapFxSends, Boolean mapFader, Track track)
    {
       // this.isEffectTrack = isEffectTrack;
       this.index = index;
       this.name = name;
       this.midiChannel = channel;
       this.busses = busses;
       this.mapFader = mapFader;
       this.deviceParamMappers = new DeviceControlMapper[3];
       this.deviceModMappers = new DeviceControlMapper[3];
       this.deviceRoutingMappers = new DeviceControlMapper[1];
       this.track = track;

       if(mapBussesSends||mapFxSends){
         this.setupSends(base, mapBussesSends,mapFxSends);
       }

       if (busses.length>0){
         this.createBusFaderMappings(base);
       }

       if(mapFader){
         createFaderMapping(base);
       }
       
    }

    DeviceBank createDeviceBank(int bankSize){
       return track.createDeviceBank(bankSize);
    }
 

    public void addParamDeviceMapper(ExtensionBase base, String name, Device device, int slot){
      if(this.deviceParamMappers[slot] != null){
         base.host.println("Error. Attempting to add device to slot "+slot+": slot already assigned!");
         return;
      }
      else{ 
         base.host.println("Assinging "+name+" to slot "+slot);
         int cc_base= -1;   
         switch(slot){
            case 0 : {cc_base = base.CONTROLS_1_BASE;};
            break;
            case 1 : {cc_base = base.CONTROLS_2_BASE;};
            break;
            case 2 : {cc_base = base.CONTROLS_3_BASE;};
            break;
            default : {
               base.host.println("Error. Invalid slot assigment for device: " + name);
            }
            };
            if(cc_base > 0){this.deviceParamMappers[slot] = new DeviceControlMapper(base, device, name, this, cc_base);}
            else { return; }
      }
    }

    public void addModDeviceMapper(ExtensionBase base, String name, Device device, int slot){
      if(this.deviceModMappers[slot] != null){
         base.host.println("Error. Attempting to add "+name+" to slot "+slot+": slot already assigned!");
         return;
      }
      else{
         base.host.println("Assinging "+name+" to slot "+slot);
         int cc_base= -1;   
         switch(slot){
            case 0 : {
               cc_base = base.MOD_1_BASE;
            }
            break;
            case 1 : {
               cc_base = base.MOD_2_BASE;
            }
            break;
            case 2 : {
               cc_base = base.MOD_3_BASE;
            }
            break;
            default : {
               base.host.println("Error. Invalid slot assigment for device: " + name);
            }
         };
            
            if(cc_base > 0){this.deviceModMappers[slot] = new DeviceControlMapper(base, device, name, this, cc_base);}
            else { return; }
      }
    }

    public void addRoutingDeviceMapper(ExtensionBase base, Device device){
      if(this.deviceRoutingMappers[0] != null){
         base.host.println("Error. Routing mapping already assigned for " + this.name);
         return;
      }
      else{ 
         base.host.println("Assinging routing mapping for "+this.name);
         this.deviceRoutingMappers[0] = new DeviceControlMapper(base, device, this.name + " routing", this, base.ROUTING_BASE);
      };
    }

    public void createFaderMapping(ExtensionBase base){
      fader = base.extensionHardwareSurface.createHardwareSlider (name.toUpperCase()+"_FADER");
      fader.setAdjustValueMatcher (base.scPortIn.createAbsoluteCCValueMatcher (midiChannel, base.TRACK_FADER_CC));
      fader.setBinding (track.volume ());
    }

    public void createBusFaderMappings(ExtensionBase base){
      busFaders = new HardwareSlider[busses.length];
      for (int i = 0; i < busses.length; i++) {
         busFaders[i] = base.extensionHardwareSurface.createHardwareSlider ("SLIDER_"+name.toUpperCase()+"_"+i);
         busFaders[i].setAdjustValueMatcher (base.oscPortIn.createAbsoluteCCValueMatcher (midiChannel, base.BUS_FADERS_BASE + i));
         busFaders[i].setBinding (base.bus_fader_bank.getItemAt (busses[i]).volume ());
      }
    }
    

 

    public void setupSends(ExtensionBase base, boolean mapBussesSends, boolean mapFxSends){
      // if(mapBusses || mapFx) {
         sends = track.sendBank();
         // base.host.println("track sends: " + String.valueOf(sends.getSizeOfBank()));
      // }

      // map bus sends
      if(mapBussesSends) {

         busKnobs = new AbsoluteHardwareKnob[base.TOTAL_BUS_TRACKS];
         for (int k = 0; k < base.TOTAL_BUS_TRACKS; k++) {
            
               busKnobs[k] = base.extensionHardwareSurface.createAbsoluteHardwareKnob("ABS_KNOB_"+name.toUpperCase()+"_BUS_"+k);
               busKnobs[k].setAdjustValueMatcher (base.oscPortIn.createAbsoluteCCValueMatcher (midiChannel, base.BUS_SENDS_BASE + k));
               busKnobs[k].setBinding (sends.getItemAt (k+ base.BUS_SENDS_START).value());

               if(sends.getSizeOfBank() < k + base.BUS_SENDS_START) {
                  break;
               }
               // bus_knobs[k]
         }
      }

      // map fx sends
      if(mapFxSends) {
         fxKnobs = new AbsoluteHardwareKnob[base.TOTAL_FX_TRACKS];
         for (int k = 0; k < base.TOTAL_FX_TRACKS; k++) {

               fxKnobs[k] = base.extensionHardwareSurface.createAbsoluteHardwareKnob("ABS_KNOB_"+name.toUpperCase()+"_FX_"+k);
               fxKnobs[k].setAdjustValueMatcher (base.oscPortIn.createAbsoluteCCValueMatcher (midiChannel, base.FX_SENDS_BASE + k));
               fxKnobs[k].setBinding (sends.getItemAt (k + base.FX_SENDS_START).value());
               if(sends.getSizeOfBank() < k + base.FX_SENDS_START) {
                  break;
               }
            }
      }

    }
 }
