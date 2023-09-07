package com.armedwithbow;
import com.bitwig.extension.controller.api.AbsoluteHardwareKnob;
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
    
    private AbsoluteHardwareKnob[] bus_knobs;
    private AbsoluteHardwareKnob[] fx_knobs;
    public final Boolean map_fader;
    // public final Boolean isEffectTrack;
    public final int index;
    public final String name;
    public final int midi_channel;
    public int[] busses;
    public final Boolean map_busses;
    public final Boolean map_fx;

    public Track track;
    public SendBank sends;
    public DeviceBank trackDeviceBank;
    
    // public HardwareSlider[] sliders;
    // public IntegerHardwareProperty[] bus_IntHarwareProperty;
    // public IntegerHardwareProperty[] fx_IntIHarwarePropertys;
  
    //class constructor
 
    /**
     * Construcotr.
     *
     * @param index the track index for the fx / audio relative to the appropriate scope
     * @param name name of the UI module
     * @param channel midi channel for the module
     * @param busses the indexes of the associated bus channels
     * @param map_busses map bus sends 
     * @param map_fx map fx sends 
     * @param map_fader map fader of primary track
     
     */
    DynamicTrackModule(int index, String name, int channel, int[] busses, Boolean map_busses,Boolean map_fx, Boolean map_fader, Track track)
    {
       // this.isEffectTrack = isEffectTrack;
       this.index = index;
       this.name = name;
       this.midi_channel = channel;
       this.busses = busses;
       this.map_busses = map_busses;
       this.map_fx = map_fx;
       this.map_fader = map_fader;
       this.deviceParamMappers = new DeviceControlMapper[3];
       this.deviceModMappers = new DeviceControlMapper[3];
       this.deviceRoutingMappers = new DeviceControlMapper[1];
       this.track = track;
       
    }

    DeviceBank getTrackDeviceBank(int bankSize){
      return track.createDeviceBank(bankSize);
    }
 
    public String display()
    {
       String newLine = System.getProperty("line.separator"); 
       String moduleInfo = ("UI Module:" + 
                            newLine + "\tindex: "+ index +
                            newLine + "\tname: "+ name +
                            newLine + "\tchannel: "+ midi_channel +
                            newLine + "\tbusses: "+ busses +
                            newLine + "\tmap sends: "+ map_busses +
                            newLine + "\tmap sends: "+ map_fx
                           );
        return moduleInfo;
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
      if(this.deviceParamMappers[0] != null){
         base.host.println("Error. Routing mapping already assigned for " + this.name);
         return;
      }
      else{ 
         base.host.println("Assinging routing mapping for "+this.name);
         this.deviceParamMappers[0] = new DeviceControlMapper(base, device, this.name + " routing", this, base.ROUTING_BASE);
      };
    }
    

 

    public void setupSends(ExtensionBase base, InterfaceGroup interfaceGroup, int TOTAL_BUS_TRACKS, int BUS_SENDS_START, int TOTAL_FX_TRACKS, int FX_SENDS_START){
      if(map_busses || map_fx) {
         sends = track.sendBank();
         base.host.println("track sends: " + String.valueOf(sends.getSizeOfBank()));
      }

      // map bus sends
      if(map_busses) {

         bus_knobs = new AbsoluteHardwareKnob[TOTAL_BUS_TRACKS];
         for (int k = 0; k < TOTAL_BUS_TRACKS; k++) {
            
               bus_knobs[k] = base.extensionHardwareSurface.createAbsoluteHardwareKnob("ABS_KNOB_"+name.toUpperCase()+"_BUS_"+k);
               bus_knobs[k].setAdjustValueMatcher (base.oscPortIn.createAbsoluteCCValueMatcher (midi_channel, base.BUS_SENDS_BASE + k));
               bus_knobs[k].setBinding (sends.getItemAt (k+ BUS_SENDS_START).value());

               if(sends.getSizeOfBank() < k + BUS_SENDS_START) {
                  break;
               }
               // bus_knobs[k]
         }
      }

      // map fx sends
      if(map_fx) {
         fx_knobs = new AbsoluteHardwareKnob[TOTAL_FX_TRACKS];
         for (int k = 0; k < TOTAL_FX_TRACKS; k++) {

               fx_knobs[k] = base.extensionHardwareSurface.createAbsoluteHardwareKnob("ABS_KNOB_"+name.toUpperCase()+"_FX_"+k);
               fx_knobs[k].setAdjustValueMatcher (base.oscPortIn.createAbsoluteCCValueMatcher (midi_channel, base.FX_SENDS_BASE + k));
               fx_knobs[k].setBinding (sends.getItemAt (k + FX_SENDS_START).value());
               if(sends.getSizeOfBank() < k + FX_SENDS_START) {
                  break;
               }
            }
      }

      //create cursor

      // this.cursorTrack = base.host.createCursorTrack(this.name+"_CURSOR", this.name+"_cursor_track", TOTAL_BUS_TRACKS + TOTAL_FX_TRACKS, 0, false);
    }
 }
