package com.armedwithbow;
import java.util.HashMap;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Transport;
import com.bitwig.extension.controller.ControllerExtension;

//import com.bitwig.extension.api.Color;
import com.bitwig.extension.controller.api.AbsoluteHardwareKnob;
import com.bitwig.extension.controller.api.HardwareButton;
//import com.bitwig.extension.controller.api.HardwareLightVisualState;
import com.bitwig.extension.controller.api.HardwareOutputElement;
import com.bitwig.extension.controller.api.HardwareSlider;
import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.HardwareTextDisplay;
import com.bitwig.extension.controller.api.HardwareTextDisplayLine;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.MidiOut;
//import com.bitwig.extension.controller.api.OnOffHardwareLight;
//import com.bitwig.extension.controller.api.PianoKeyboard;
import com.bitwig.extension.controller.api.TrackBank;
import com.bitwig.extension.controller.api.IntegerHardwareProperty;
// import com.bitwig.extension.controller.api.ChannelBank;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.SendBank;
import com.bitwig.extension.controller.api.Send;
// import java.util.function.BooleanSupplier;
import com.bitwig.extension.controller.api.BooleanValue;
import com.bitwig.extension.controller.api.StringValue;
import com.bitwig.extension.controller.api.IntegerValue;



// class UIMapper {
   
// }

// class UIModuleGroup {
//    public final String name;
//    final int group_sends;
   
// }


// UI Module class
class UIModule {
  
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
   // public HardwareSlider[] sliders;
   // public IntegerHardwareProperty[] bus_IntHarwareProperty;
   // public IntegerHardwareProperty[] fx_IntIHarwarePropertys;
 
   //class constructor

   
   UIModule(int index, String name, int channel, int[] busses, Boolean map_busses,Boolean map_fx, int param_bank1, int param_bank2, int param_bank3)
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

class ExtensionBase {
   public final ControllerHost host;
   public final HardwareSurface extensionHardwareSurface;
   public final MidiIn port;

   ExtensionBase(ControllerHost host, HardwareSurface extensionHardwareSurface, MidiIn port)
   {
      this.host = host;
      this.extensionHardwareSurface = extensionHardwareSurface;
      this.port = port;
   }
}

class InterfaceGroup {
   public final ExtensionBase base;
   public final String name;
   public final String type;
   public final int start_index;
   public final Boolean isNested;
   public TrackBank groupTrackBank;
   public UIModule[] modules;
   // public final HardwareSurface extentionHardwareSurface;

   InterfaceGroup(ExtensionBase base, String name, String type, int start_index, UIModule[] modules, Boolean isNested)
   {
      this.base = base;
      this.name = name;
      this.start_index = start_index;
      this.type = type;
      this.modules = modules;
      this.isNested = isNested;
      // this.groupTrackBank = groupTrackBank;
      
   }

   // public void init(ControllerHost host, HardwareSurface extentionHardwareSurface, MidiIn port, int TOTAL_BUS_TRACKS, int TOTAL_FX_TRACKS, int BUS_SENDS_BASE, int BUS_SENDS_START, int FX_SENDS_BASE, int FX_SENDS_START, int BUS_FADERS_BASE)
   public void init(int TOTAL_BUS_TRACKS, int TOTAL_FX_TRACKS, int BUS_SENDS_BASE, int BUS_SENDS_START, int FX_SENDS_BASE, int FX_SENDS_START)
   {
      for(UIModule mod : modules) {
         if(mod.map_busses || mod.map_fx) {
            mod.track = groupTrackBank.getItemAt(mod.index);
            mod.sends = mod.track.sendBank();
            this.base.host.println("track sends: " + String.valueOf(mod.sends.getSizeOfBank()));
         }

         // map bus sends
         if(mod.map_busses) {
            mod.bus_knobs = new AbsoluteHardwareKnob[TOTAL_BUS_TRACKS];
            // mod.bus_IntHarwareProperty = new IntegerHardwareProperty[TOTAL_BUS_TRACKS];
            for (int k = 0; k < TOTAL_BUS_TRACKS; k++) {
               
                  mod.bus_knobs[k] = this.base.extensionHardwareSurface.createAbsoluteHardwareKnob("ABS_KNOB_"+mod.name.toUpperCase()+"_BUS_"+k);
                  // mod.bus_knobs[k] = this.hardwareSurface.crea ("ABS_KNOB_"+mod.name.toUpperCase()+"_BUS_"+k);
                  mod.bus_knobs[k].setAdjustValueMatcher (this.base.port.createAbsoluteCCValueMatcher (mod.midi_channel, BUS_SENDS_BASE + k));
                  mod.bus_knobs[k].setBinding (mod.sends.getItemAt (k+ BUS_SENDS_START).value());
                  // host.println("subscribed send: " + mod.sends.getItemAt (k+ BUS_SENDS_START).addValueObserver(arg0));
                  // mod.sends.getItemAt (k+ BUS_SENDS_START).addValueObserver( 127, lambda);
                  // mod.sends.getItemAt (k+ BUS_SENDS_START).addValueObserver( 127, midiOutPort.sendMidi(1, 1, 5)); 
                  // setValueSupplier
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

                  mod.fx_knobs[k] = this.base.extensionHardwareSurface.createAbsoluteHardwareKnob("ABS_KNOB_"+mod.name.toUpperCase()+"_FX_"+k);
                  mod.fx_knobs[k].setAdjustValueMatcher (this.base.port.createAbsoluteCCValueMatcher (mod.midi_channel, FX_SENDS_BASE + k));
                  mod.fx_knobs[k].setBinding (mod.sends.getItemAt (k + FX_SENDS_START).value());
                  // setValueSupplier()
                  // mod.sends.getItemAt (k+ BUS_SENDS_START).markInterested();
                  // host.println("subscribed send: " + 
                  if(mod.sends.getSizeOfBank() < k + FX_SENDS_START) {
                     break;
                  }
               }
         }
         
     }
     return;
   }

   // setup sliders for bus tracks associated with the group
   public int setupSliders(HardwareSlider[] sliders, int slider_i, int BUS_FADERS_BASE, TrackBank sliders_bank)
   {
      for(UIModule mod : modules) {
         for (int j = 0; j < mod.busses.length; j++) {
            sliders[slider_i] = this.base.extensionHardwareSurface.createHardwareSlider ("SLIDER_"+mod.name.toUpperCase()+"_"+j);
            sliders[slider_i].setAdjustValueMatcher (this.base.port.createAbsoluteCCValueMatcher (mod.midi_channel, BUS_FADERS_BASE + j));
            sliders[slider_i].setBinding (sliders_bank.getItemAt (mod.busses[j]).volume ());
            // fxBank.itemCount ().markInterested ();
            // Track fx = fxBank.getItemAt (mod.busses[j]);
            // fx.position().markInterested();
            
            
            slider_i++;
         }
      }
      return slider_i;
   }
}
// class SourceUIModule extends UIModule {
//    protected SourceUIModule(int index, String name, int channel, int[] busses, Boolean map_sends, int param_bank1, int param_bank2, int param_bank3)
//    {
//       super(index, name, channel, busses, map_sends, param_bank1, param_bank2, param_bank3);
//    }
// }

// public interface getValueObserverFunction
// {
//     /**
//      * Disable/enable the setting.
//      *
//      * @param enable True to enable
//      */
//     void setEnabled (final boolean enable);


//     /**
//      * Make the widget of the setting visible or hide it.
//      *
//      * @param visible True to show
//      */
//     void setVisible (boolean visible);
// }

// public class getValueObserverFunction {

//    public static void main(String[] args) {

//        Function<String, Integer> func = x -> x.length();

//        Function<Integer, Integer> func2 = x -> x * 2;

//        Integer result = func.andThen(func2).apply("mkyong");   // 12

//        System.out.println(result);

//    }

// }

interface MyFunctionalInterface {
   public void execute();
}


public class OSCControllerforAWBExtension extends ControllerExtension
{
   private final int       channel        = 0;
   private final int       buttonControl  = 112; // TODO -> EXCHANGE WITH YOUR CONTROL
   // private final int       sliderControl_0  = 10;  // TODO -> EXCHANGE WITH YOUR CONTROL
   // private final int       sliderControl_1  = 11;  // TODO -> EXCHANGE WITH YOUR CONTROL
   // private final int       absKnobControl = 15;  // TODO -> EXCHANGE WITH YOUR CONTROL

   private final int TOTAL_MODULES = 12;      
   private final int IN_MODULES = 3;      
   private final int FX_GROUP_1_MODULES = 5;      
   private final int FX_GROUP_2_MODULES = 5;      
   private final int TOTAL_BUS_TRACKS = 18;  // tracks that are just BUS OUT
   private final int TOTAL_FX_TRACKS = 5;    // FX tracks
   private final int TOTAL_SLIDERS = 18;     // sliders defined in o-s-c... should really map to bus tracks somehow
   
   private final int INS_TRACKBANK_START = 0;    
   private final int INS_TRACKBANK_SIZE = 6;    
   private final int DEL_TRACKBANK_START = INS_TRACKBANK_START + INS_TRACKBANK_SIZE - 1;    
   private final int DEL_TRACKBANK_SIZE = 6;    
   private final int BUS_SENDS_START = 0;    
   private final int BUS_SENDS_END = BUS_SENDS_START + TOTAL_BUS_TRACKS - 1;  
   private final int FX_SENDS_START = BUS_SENDS_END + 1;    
   private final int FX_SENDS_END = BUS_SENDS_END + TOTAL_FX_TRACKS;    

   private final int INS_MASTER = INS_TRACKBANK_START;      
   private final int VC_MASTER = INS_MASTER + 1;      
   private final int P2_MASTER = INS_MASTER + 2;      

   private HardwareSurface hardwareSurface;

   protected OSCControllerforAWBExtension(final OSCControllerforAWBExtensionDefinition definition, final ControllerHost host)
   {
      super(definition, host);
   }

   @Override
   public void init()
   {
      final ControllerHost host = getHost();    
      
      ///////////////////////////////////
      // 0. Create the constants
      /////////////////////////////////

      


      /* SENDS INDEX - Module > TRACK INDEX // CHAN
      * line 1_vc > 1 // 1
      * line 2 > - // 2
      * line 3_p2 > 2 // 7
      * 0 - line 4_splutter > 3 // 8
      * 1 - b1 > 5 // 3
      * 2 - a1 > 6 // 4
      * 3 - a2 > 7 // 5
      * 4 - b2 > 8 // 6
      * 5 - c1 // 1
      * 6 - c2 // 1
      * 7 - c3 // 1
      * 8 - c4 // 1
      * 9 - b_b1
      * 10 - b_a1
      * 11 - b_a2
      * 12 - b_b2
      * 8 - b_stutter // 1
      * 9 - b_splutter //
      * 15 - b_ubermod
      * 16 - b_sparkle 
      * 17 - b_deci
      * 18 - b_fuzz
      * 19 - b_verb
      * 20 - b_mast
      * 21 - b_p2
      * 22 - b_environment
      * 23 - sparkle // 14
      * 24 - deci // 12
      * 25 - fuzz // 13
      * 26 - reverb // 15  (shared with mast)
      * 27 - mast // 15
      */

      


      UIModule[] modules;
      // UIModule[] in_modules;
      // UIModule[] fx_1_modules;
      // UIModule[] fx_2_modules;
      
      modules = new UIModule[TOTAL_MODULES];
      // in_modules = new UIModule[IN_MODULES];
      // fx_1_modules = new UIModule[FX_GROUP_1_MODULES];
      // fx_2_modules = new UIModule[FX_GROUP_2_MODULES];

      // index is track, busindex is effectTrack
      UIModule vc_mast_mod = new UIModule( 0, "vc_mast", 0, new int[]{}, false, true, 0, 7, 0); //vc + ubermod
      UIModule vc_in_mod = new UIModule( 1, "vc_in", 0, new int[]{0, 1, 2, 3, 8, 10}, true, false, 0, 7, 0); //vc + ubermod
      
      UIModule p2_mast_mod = new UIModule(5, "pro2", 6, new int[]{16}, true, true, 0, 0, 0); //p2
      // modules[1] = new UIModule(false, , "pro2", 6, new int[]{16}, true, 0, 0, 0); //p2
      UIModule splut_mod = new UIModule(7, "splut", 7, new int[]{9}, true, true ,0, 0, 0); // TODO - param banks
      // modules[2] = new UIModule(1, "del_b1", 2, new int[]{4}, true, true, 6, 0, 0); 
      // modules[3] = new UIModule(2, "del_a1", 3, new int[]{6}, true, true, 6, 1, 0); 
      // modules[4] = new UIModule(3, "del_a2", 4, new int[]{5}, true, true, 6, 0, 0); 
      // modules[5] = new UIModule(4, "del_b2", 5, new int[]{7}, true, true, 6, 0, 0); 
      UIModule b1_mod = new UIModule(0, "del_b1", 2, new int[]{4}, true, true, 6, 0, 0); 
      UIModule a1_mod = new UIModule(1, "del_a1", 3, new int[]{6}, true, true, 6, 1, 0); 
      UIModule a2_mod = new UIModule(2, "del_a2", 4, new int[]{5}, true, true, 6, 0, 0); 
      UIModule b2_mod = new UIModule(3, "del_b2", 5, new int[]{7}, true, true, 6, 0, 0); 
      
      // modules[6] = new UIModule(4+TOTAL_BUS_TRACKS, "sparkle", 13, new int[]{11}, false, false, 5, 0, 0); 
      // modules[7] = new UIModule(5+TOTAL_BUS_TRACKS, "deci", 11, new int[]{12}, false, false, 5, 0, 0); 
      // modules[8] = new UIModule(6+TOTAL_BUS_TRACKS, "fuzz", 12, new int[]{13}, false, false, 1, 0, 0); 
      // modules[9] = new UIModule(7+TOTAL_BUS_TRACKS, "verb_mast", 14, new int[]{15, 14},false, false, 2, 2, 0); 
      
      // modules[10] = new UIModule(true, 27, "mast", 14, new int[]{15}, true, 2, 0, 0); 

      UIModule[] vc_modules = {vc_mast_mod, vc_mast_mod};
      UIModule[] p2_modules = {p2_mast_mod};
      UIModule[] fx_1_modules = {splut_mod, b1_mod, a1_mod, a2_mod, b2_mod};

      
      // UIModule[] fx_2_modules = {ins_mast_mod, vc_mast_mod, p2_mast_mod};

      for(UIModule mod: vc_modules) {
       host.println(mod.display());
      }
      // final HashMap<String, Integer> channels = new HashMap<String, Integer>();

      // channels.put("in_1", 1);
      // channels.put("in_2", 2);
      // channels.put("in_3", 7); //p2
      // channels.put("in_4", 8);
      // channels.put("in_5", 9);
      // channels.put("in_6",10);
      // channels.put("in_ensemble", 11);
      // channels.put("bus_b1", 3);
      // channels.put("bus_a1", 4);
      // channels.put("bus_a2", 5);
      // channels.put("bus_b2", 6);
      // channels.put("bus_sparkle", 14);
      // channels.put("bus_fuzz", 12);
      // channels.put("bus_deci", 13);
      // // channels.put("bus_verb", 6);
      // channels.put("bus_mast", 15);

      final int ROUTING_BASE = 10;
      final int CONTROLS_1_BASE = 20;
      final int CONTROLS_2_BASE = 30;
      final int CONTROLS_3_BASE = 40;
      final int FX_SENDS_BASE = 50;
      final int BUS_SENDS_BASE = 70;
      final int BUS_FADERS_BASE = 90;
      
      
      this.hardwareSurface = host.createHardwareSurface ();
      
      final MidiIn port = host.getMidiInPort (0);

      ExtensionBase base = new ExtensionBase( host, this.hardwareSurface, port);
      
      InterfaceGroup vc_in = new InterfaceGroup(base, "in lines", "track", 1, vc_modules, true);
      InterfaceGroup p2_in = new InterfaceGroup(base, "in lines", "track", 5, p2_modules, false);
      InterfaceGroup fx_1 = new InterfaceGroup(base, "fx 1", "fx", 0, fx_1_modules, false);

      vc_in.init(TOTAL_BUS_TRACKS, TOTAL_FX_TRACKS, BUS_SENDS_BASE, BUS_SENDS_START, FX_SENDS_BASE, FX_SENDS_START);
      p2_in.init(TOTAL_BUS_TRACKS, TOTAL_FX_TRACKS, BUS_SENDS_BASE, BUS_SENDS_START, FX_SENDS_BASE, FX_SENDS_START);
      fx_1.init(TOTAL_BUS_TRACKS, TOTAL_FX_TRACKS, BUS_SENDS_BASE, BUS_SENDS_START, FX_SENDS_BASE, FX_SENDS_START);

      final TrackBank bus_fader_bank = host.createEffectTrackBank (BUS_SENDS_END, 0);
      // InterfaceGroup fx_2 = new InterfaceGroup(base, "fx 2", "fx", fx_2_modules, false);
      /////////////////////////////////////////////////////////////////////////////////////
      // 1. Create the controls
      
      // 1.1 Create input controls
      HardwareSlider[] sliders;
      sliders = new HardwareSlider[TOTAL_SLIDERS];

      
      // HardwareButton
      final HardwareButton playButton = this.hardwareSurface.createHardwareButton ("PLAY_BUTTON");
      // HardwareSlider
      // final HardwareSlider slider_vc = this.hardwareSurface.createHardwareSlider ("SLIDER_VC");
      // final HardwareSlider slider_p2 = this.hardwareSurface.createHardwareSlider ("SLIDER_P2");
      // final HardwareSlider slider_splut = this.hardwareSurface.createHardwareSlider ("SLIDER_SPLUT");
      //final HardwareSlider slider_delIn_1 = this.hardwareSurface.createHardwareSlider ("SLIDER_DIN_2");
      //final HardwareSlider slider_delIn_2 = this.hardwareSurface.createHardwareSlider ("SLIDER_DIN_1");
      
      // final HardwareSlider slider_b1 = this.hardwareSurface.createHardwareSlider ("SLIDER_B2");
      // final HardwareSlider slider_b2 = this.hardwareSurface.createHardwareSlider ("SLIDER_B2");
      // final HardwareSlider slider_a1 = this.hardwareSurface.createHardwareSlider ("SLIDER_A1");
      // final HardwareSlider slider_a2 = this.hardwareSurface.createHardwareSlider ("SLIDER_A2");
      
      // final HardwareSlider slider_mast = this.hardwareSurface.createHardwareSlider ("SLIDER_B_MAST");
      // final HardwareSlider slider_c1 = this.hardwareSurface.createHardwareSlider ("SLIDER_B_C1"); // vc
      // final HardwareSlider slider_c2 = this.hardwareSurface.createHardwareSlider ("SLIDER_B_C2");
      // final HardwareSlider slider_c3 = this.hardwareSurface.createHardwareSlider ("SLIDER_B_C3");
      // final HardwareSlider slider_c4 = this.hardwareSurface.createHardwareSlider ("SLIDER_B_C4");
      // final HardwareSlider slider_b_delb1 = this.hardwareSurface.createHardwareSlider ("SLIDER_B_DEL_B1"); // delBus
      // final HardwareSlider slider_b_delb2 = this.hardwareSurface.createHardwareSlider ("SLIDER_B_DEL_B2");
      // final HardwareSlider slider_b_dela1 = this.hardwareSurface.createHardwareSlider ("SLIDER_B_DEL_A1");
      // final HardwareSlider slider_b_dela2 = this.hardwareSurface.createHardwareSlider ("SLIDER_B_DEL_A2");
      // final HardwareSlider slider_b_um = this.hardwareSurface.createHardwareSlider ("SLIDER_B_UM");
      // final HardwareSlider slider_b_stut_1 = this.hardwareSurface.createHardwareSlider ("SLIDER_B_STUT_1");
      // final HardwareSlider slider_b_stut_2 = this.hardwareSurface.createHardwareSlider ("SLIDER_B_STUT_2");
      // final HardwareSlider slider_b_sparkle = this.hardwareSurface.createHardwareSlider ("SLIDER_B_SPARK");
      // final HardwareSlider slider_b_splut = this.hardwareSurface.createHardwareSlider ("SLIDER_B_SPLUT");
      // final HardwareSlider slider_b_fuzz = this.hardwareSurface.createHardwareSlider ("SLIDER_B_FUZZ");
      // final HardwareSlider slider_verb = this.hardwareSurface.createHardwareSlider ("SLIDER_B_VERB");
      // final HardwareSlider slider_b_p2 = this.hardwareSurface.createHardwareSlider ("SLIDER_B_P2");
      


      // AbsoluteHardwareKnob
      final AbsoluteHardwareKnob absKnob = this.hardwareSurface.createAbsoluteHardwareKnob ("ABSOLUTE_KNOB");

       /////////////////////////////////////////////////////////////////////////////////////
      // 2. Bind the controls to MIDI commands

      
      playButton.pressedAction ().setActionMatcher (port.createCCActionMatcher (this.channel, this.buttonControl, 127));
      playButton.releasedAction ().setActionMatcher (port.createCCActionMatcher (this.channel, this.buttonControl, 0));

      
      // absKnob.setAdjustValueMatcher (port.createAbsoluteCCValueMatcher (this.channel, this.absKnobControl));

      /////////////////////////////////////////////////////////////////////////////////////
      // 3. Bind the controls to actions / targets

      final Transport transport = host.createTransport ();
      playButton.pressedAction ().setBinding (transport.playAction ());

      final TrackBank ins_trackBank = host.createTrackBank (INS_TRACKBANK_SIZE + DEL_TRACKBANK_SIZE, TOTAL_BUS_TRACKS + TOTAL_FX_TRACKS, 0);
      // final TrackBank del_trackBank = host.createTrackBank (DEL_TRACKBANK_SIZE, TOTAL_BUS_TRACKS + TOTAL_FX_TRACKS, 0);
      

      // public class 

      final MidiOut midiOutPort = host.getMidiOutPort (0);
      
   //    MyFunctionalInterface lambda = (int i) -> {
   //       System.out.println("Executing...");
   //   }
      
      int i = 0;
      for(UIModule mod : in_modules) {
         if(mod.map_busses || mod.map_fx) {
            mod.track = ins_trackBank.getItemAt(mod.index);
            mod.sends = mod.track.sendBank();
            host.println("track sends: " + String.valueOf(mod.sends.getSizeOfBank()));
         }
         if(mod.map_busses) {
            mod.bus_knobs = new AbsoluteHardwareKnob[TOTAL_BUS_TRACKS];
            // mod.bus_IntHarwareProperty = new IntegerHardwareProperty[TOTAL_BUS_TRACKS];
            for (int k = 0; k < TOTAL_BUS_TRACKS; k++) {
               
                  mod.bus_knobs[k] = this.hardwareSurface.createAbsoluteHardwareKnob("ABS_KNOB_"+mod.name.toUpperCase()+"_BUS_"+k);
                  // mod.bus_knobs[k] = this.hardwareSurface.crea ("ABS_KNOB_"+mod.name.toUpperCase()+"_BUS_"+k);
                  mod.bus_knobs[k].setAdjustValueMatcher (port.createAbsoluteCCValueMatcher (mod.midi_channel, BUS_SENDS_BASE + k));
                  mod.bus_knobs[k].setBinding (mod.sends.getItemAt (k+ BUS_SENDS_START).value());
                  // host.println("subscribed send: " + mod.sends.getItemAt (k+ BUS_SENDS_START).addValueObserver(arg0));
                  // mod.sends.getItemAt (k+ BUS_SENDS_START).addValueObserver( 127, lambda);
                  // mod.sends.getItemAt (k+ BUS_SENDS_START).addValueObserver( 127, midiOutPort.sendMidi(1, 1, 5)); 
                  // setValueSupplier
                  if(mod.sends.getSizeOfBank() < k + BUS_SENDS_START) {
                     break;
                  }
                  // mod.bus_knobs[k]
            }
         }
         if(mod.map_fx) {
            mod.fx_knobs = new AbsoluteHardwareKnob[TOTAL_FX_TRACKS];
            for (int k = 0; k < TOTAL_FX_TRACKS; k++) {

                  mod.fx_knobs[k] = this.hardwareSurface.createAbsoluteHardwareKnob("ABS_KNOB_"+mod.name.toUpperCase()+"_FX_"+k);
                  mod.fx_knobs[k].setAdjustValueMatcher (port.createAbsoluteCCValueMatcher (mod.midi_channel, FX_SENDS_BASE + k));
                  mod.fx_knobs[k].setBinding (mod.sends.getItemAt (k + FX_SENDS_START).value());
                  // setValueSupplier()
                  // mod.sends.getItemAt (k+ BUS_SENDS_START).markInterested();
                  // host.println("subscribed send: " + 
                  if(mod.sends.getSizeOfBank() < k + FX_SENDS_START) {
                     break;
                  }
               }
         }
         
         // DO MOD BUSSES
         for (int j = 0; j < mod.busses.length; j++) {
            sliders[i] = this.hardwareSurface.createHardwareSlider ("SLIDER_"+mod.name.toUpperCase()+"_"+j);
            sliders[i].setAdjustValueMatcher (port.createAbsoluteCCValueMatcher (mod.midi_channel, BUS_FADERS_BASE + j));
            sliders[i].setBinding (fxBank.getItemAt (mod.busses[j]).volume ());
            // fxBank.itemCount ().markInterested ();
            // Track fx = fxBank.getItemAt (mod.busses[j]);
            // fx.position().markInterested();
            
            
            i++;
         }
     }

     
     for (int t = 0; t < fxBank.getSizeOfBank(); t++) {
      Track fx = fxBank.getItemAt (t);
      // fx.position().markInterested();
      host.println("fx position: " + String.valueOf(fx.position ().getAsInt()));
     }

      ins_trackBank.scrollPosition ().markInterested ();
      ins_trackBank.canScrollBackwards ().markInterested ();
      ins_trackBank.canScrollForwards ().markInterested ();
      ins_trackBank.itemCount ().markInterested ();
      ins_trackBank.cursorIndex() .markInterested ();
      ins_trackBank.canScrollForwards	()	.markInterested ();
      
      fxBank.scrollPosition ().markInterested ();
      fxBank.canScrollBackwards ().markInterested ();
      fxBank.canScrollForwards ().markInterested ();
      fxBank.itemCount ().markInterested ();
      fxBank.cursorIndex() .markInterested ();
      fxBank.canScrollForwards	()	.markInterested ();

      
      // host.println(String.valueOf(ins_trackBank.getSizeOfBank()));

      // host.println("cursor index: " + String.valueOf(fxBank.cursorIndex().getAsInt()));
      // host.println("bank itemCount: " + String.valueOf(fxBank.itemCount().getAsInt()));
      // host.println(fxBank.canScrollForwards().getAsBoolean() ? "can scroll" : "can't scroll");


      // host.println(String.valueOf(ins_trackBank.getSizeOfBank()));

      // host.println("cursor index: " + String.valueOf(ins_trackBank.cursorIndex().getAsInt()));
      // host.println("bank itemCount: " + String.valueOf(ins_trackBank.itemCount().getAsInt()));
      // host.println(ins_trackBank.canScrollForwards().getAsBoolean() ? "can scroll" : "can't scroll");
      // for (int t = 0; t < ins_trackBank.getSizeOfBank(); t++) {
      //    // Track track = ins_trackBank.getItemAt(t);
      //    Track track = ins_trackBank.getItemAt(t);
      //    host.println("track sends: " + String.valueOf(track.sendBank().getSizeOfBank()));
      //    // track.isGroup().getAsBoolean()
         
         
      //    track.isGroup().markInterested();
      //    track.position().markInterested();
      //    track.trackType().markInterested();
         
      //    host.println("track type: " + track.trackType().get());
      //    host.println("position: " + String.valueOf(track.position ().getAsInt()));
         
      //    // String result = track.isGroup().getAsBoolean() ? "group" : "not group";
      //    host.println(track.isGroup().getAsBoolean() ? "group" : "not group");
      //    // host.println(result);
      // }
      // slider_1.setBinding (trackBank.getItemAt (0).volume ());
      // slider_2.setBinding (trackBank.getItemAt (1).volume ());
      // absKnob.setBinding (trackBank.getItemAt (1).volume ());
      // relKnob.setBinding (trackBank.getItemAt (2).volume ());

      

   /////////////////////////////////////////////////////////////////////////////////////
      // 5. Layout the simulator UI

      this.hardwareSurface.setPhysicalSize (200, 200);

      playButton.setBounds (137.0, 133.5, 30.75, 11.5);
      //this.hardwareSurface.hardwareElementWithId ("SLIDER_1").setBounds (106.0, 120.0, 10.0, 34.25);
      //this.hardwareSurface.hardwareElementWithId ("SLIDER_2").setBounds (136.0, 120.0, 10.0, 34.25);
      // this.hardwareSurface.hardwareElementWithId ("ABSOLUTE_KNOB").setBounds (127.25, 12.25, 10.0, 10.0);


      // TODO: Perform your driver initialization here.
      // For now just show a popup notification for verification that it is running.
      host.showPopupNotification("OSC Controller for AWB Initialized");
   }

   @Override
   public void exit()
   {
      // TODO: Perform any cleanup once the driver exits
      // For now just show a popup notification for verification that it is no longer running.
      getHost().showPopupNotification("OSC Controller for AWB Exited");
   }

   @Override
   public void flush()
   {
      if (this.hardwareSurface != null)
            this.hardwareSurface.updateHardware ();
   }


}
