package com.armedwithbow;
import com.bitwig.extension.controller.api.AbsoluteHardwareKnob;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.SendBank;

// UI Module class
public class UIModule {
  
    // public final Boolean isEffectTrack;
    public final int index;
    public final String name;
    public final int midi_channel;
    public int[] busses;
    public final Boolean map_busses;
    public final Boolean map_fx;
    public final int param_bank1;
    public final int param_bank2;
    public final int param_bank3;
    public  Track track;
    public  SendBank sends;
    public AbsoluteHardwareKnob[] bus_knobs;
    public AbsoluteHardwareKnob[] fx_knobs;
    public final Boolean map_fader;
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
     * @param param_bank1 first bank of module params
     * @param param_bank2 second bank of module params
     * @param param_bank3 third bank of module params
     * @param map_fader map fader of primary track
     
     */
    UIModule(int index, String name, int channel, int[] busses, Boolean map_busses,Boolean map_fx, int param_bank1, int param_bank2, int param_bank3, Boolean map_fader)
    {
       // this.isEffectTrack = isEffectTrack;
       this.index = index;
       this.name = name;
       this.midi_channel = channel;
        this.busses = busses;
        this.map_busses = map_busses;
        this.map_fx = map_fx;
        this.param_bank1 = param_bank1;
        this.param_bank2 = param_bank2;
        this.param_bank3 = param_bank3;
        this.map_fader = map_fader;
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
                            newLine + "\tmap sends: "+ map_fx +
                            newLine + "\tparam_bank1: "+ param_bank1 +
                            newLine + "\tparam_bank2: "+ param_bank2 +
                            newLine + "\tparam_bank3: "+ param_bank3
                           );
        return moduleInfo;
    }
 }