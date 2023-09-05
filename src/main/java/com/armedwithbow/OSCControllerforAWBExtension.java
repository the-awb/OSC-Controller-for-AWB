package com.armedwithbow;
import java.util.HashMap;
import com.bitwig.extension.api.util.midi.ShortMidiMessage;
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

/*
 *    TO RUN: mvn install
 */

// class UIMapper {
   
// }

// class UIModuleGroup {
//    public final String name;
//    final int group_sends;
   
// }






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

// interface MyFunctionalInterface {
//    public void execute();
// }


public class OSCControllerforAWBExtension extends ControllerExtension
{
   private final int TOTAL_MODULES = 13;      
   private final int IN_MODULES = 3;      
   private final int FX_GROUP_1_MODULES = 10;      
   private final int FX_GROUP_2_MODULES = 5;      
   private final int TOTAL_BUS_TRACKS = 18;  // tracks that are just BUS OUT
   private final int TOP_LEVEL_GROUPS = 8;   // Top level Groups of tracks
   private final int TOTAL_FX_TRACKS = 6;    // FX tracks
   private final int TOTAL_SLIDERS = 23;     // sliders defined in o-s-c... should really map to bus tracks somehow
   
   private final int FX_TRACKBANK_START = 21;    
   private final int INS_TRACKBANK_SIZE = 6;    
 
   private final int FX_SENDS_START = 0;    
   private final int FX_SENDS_END = FX_SENDS_START + TOTAL_FX_TRACKS - 1;    
   private final int BUS_SENDS_START = FX_SENDS_END + 1;    
   private final int BUS_SENDS_END = BUS_SENDS_START + TOTAL_BUS_TRACKS;  

   private final String FX_LAYER_ID = "a0913b7f-096b-4ac9-bddd-33c775314b42";
   // private final int INS_MASTER = INS_TRACKBANK_START;      
   // private final int VC_MASTER = INS_MASTER + 1;      
   // private final int P2_MASTER = INS_MASTER + 2;      

   private HardwareSurface hardwareSurface;

   protected OSCControllerforAWBExtension(final OSCControllerforAWBExtensionDefinition definition, final ControllerHost host)
   {
      super(definition, host);
   }

   @Override
   public void init()
   {

      final ExtensionBase base = new ExtensionBase( getHost(), BUS_SENDS_END);

      base.host.println("starting fresh");

      this.hardwareSurface = base.extensionHardwareSurface;
      // final ControllerHost host = getHost();    
      
      // this.hardwareSurface = host.createHardwareSurface();
      // final MidiIn port = host.getMidiInPort (0);
      // final TrackBank bus_fader_bank = host.createEffectTrackBank (BUS_SENDS_END, 0);
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


      // index is track, busindex is effectTrack
      final DynamicTrackModule vc_fx_mod = new DynamicTrackModule( 2, "vc fx", 1, new int[]{}, false, true, false); //vc + ubermod
      final DynamicTrackModule vc_in_mod = new DynamicTrackModule( 3, "vc in", 0, new int[]{0, 1, 2, 3, 8, 10}, true, false, false); //vc + ubermod
      final DynamicTrackModule p2_mast_mod = new DynamicTrackModule(5, "pro2", 7, new int[]{16}, true, true, false); //p2
      
      

      // final UIModule del_bus_mod = new UIModule( 7, "del bus", 6, new int[]{}, true, true, 0, 7, 0, true); //vc + ubermod
      final DynamicTrackModule b1_mod = new DynamicTrackModule(9, "del_b1", 2, new int[]{4}, true, true, false); 
      final DynamicTrackModule a1_mod = new DynamicTrackModule(10, "del_a1", 3, new int[]{5}, true, true, false); 
      final DynamicTrackModule a2_mod = new DynamicTrackModule(11, "del_a2", 4, new int[]{6}, true, true, false); 
      final DynamicTrackModule b2_mod = new DynamicTrackModule(12, "del_b2", 5, new int[]{7}, true, true, false); 
      
      // final UIModule splut_mod = new UIModule(TOTAL_BUS_TRACKS, "splut", 8, new int[]{9}, true, true ,0, 0, 0, false); // TODO - param banks
      // final UIModule sparkle_mod = new UIModule(TOTAL_BUS_TRACKS + 1, "sparkle", 10, new int[]{11}, true, true, 5, 0, 0, false); 
      // final UIModule deci_mod = new UIModule(TOTAL_BUS_TRACKS + 2, "deci", 12, new int[]{12}, false, false, 5, 0, 0, false); 
      // final UIModule fuzz_mod = new UIModule(TOTAL_BUS_TRACKS + 3, "fuzz", 11, new int[]{13}, false, false, 1, 0, 0, false); 
      // final UIModule verb_mod = new UIModule(TOTAL_BUS_TRACKS + 4, "verb_mast", 13, new int[]{15, 14},false, false, 2, 2, 0, false); 
      

      // use this if doing fx sends

      final DynamicTrackModule splut_mod = new DynamicTrackModule(FX_TRACKBANK_START, "splut", 8, new int[]{9}, true, false , false); // TODO - param banks
      final DynamicTrackModule sparkle_mod = new DynamicTrackModule(FX_TRACKBANK_START + 1, "sparkle", 10, new int[]{11}, true, false, false); 
      final DynamicTrackModule deci_mod = new DynamicTrackModule(FX_TRACKBANK_START + 2, "deci", 12, new int[]{12}, true, false, false); 
      final DynamicTrackModule fuzz_mod = new DynamicTrackModule(FX_TRACKBANK_START + 3, "fuzz", 11, new int[]{13}, true, false, false); 
      final DynamicTrackModule verb_mod = new DynamicTrackModule(FX_TRACKBANK_START + 4, "verb", 13, new int[]{14},true, false,false); 
      final DynamicTrackModule mast_mod = new DynamicTrackModule(FX_TRACKBANK_START + 5, "mast", 14, new int[]{15},true, false, false); 
      
      // UIModule mast_mod = new UIModule(true, 27, "mast", 14, new int[]{15}, true, 2, 0, 0, false); 

      final DynamicTrackModule[] audio_modules = new DynamicTrackModule[]{vc_fx_mod, vc_in_mod, p2_mast_mod, b1_mod, a1_mod, a2_mod, b2_mod};
      final DynamicTrackModule[] fx_1_modules = new DynamicTrackModule[]{splut_mod, sparkle_mod, deci_mod, fuzz_mod, verb_mod, mast_mod};
      
      // final UIModule[] delay_modules = new UIModule[]{del_bus_mod, b1_mod, a1_mod, a2_mod, b2_mod};
      // UIModule[] fx_2_modules = {sparkle_mod, deci_mod, a1_mod, a2_mod, b2_mod};

      


      final int TRACK_FADER_CC = 9;
      final int ROUTING_BASE = 10;
      final int CONTROLS_1_BASE = 20;
      final int CONTROLS_2_BASE = 30;
      final int CONTROLS_3_BASE = 40;
      final int FX_SENDS_BASE = 50;
      final int BUS_SENDS_BASE = 70;
      final int BUS_FADERS_BASE = 90;
       
      

      
      
      InterfaceGroup audio_in = new InterfaceGroup( "audio source lines", "mixed", 13, 0, audio_modules, true);
      // InterfaceGroup fx_1 = new InterfaceGroup( "fx 1", "mixed", TOTAL_BUS_TRACKS + TOTAL_FX_TRACKS, TOTAL_BUS_TRACKS, fx_1_modules, false);
      InterfaceGroup fx_1 = new InterfaceGroup( "fx 1", "mixed", FX_TRACKBANK_START + TOTAL_FX_TRACKS, 0, fx_1_modules, true);
      // InterfaceGroup p2_in = new InterfaceGroup(base, "in lines", "track", 5, p2_modules, false);

      
      
      // InterfaceGroup fx_2 = new InterfaceGroup(base, "fx 2", "fx", fx_2_modules, false);
      /////////////////////////////////////////////////////////////////////////////////////
      // 1. Create the controls

      
      // 1.1 Create input controls
      HardwareSlider[] sliders;
      sliders = new HardwareSlider[TOTAL_SLIDERS];
      int sliders_i = 0;
      
      audio_in.init(base, TOTAL_BUS_TRACKS, TOTAL_FX_TRACKS, BUS_SENDS_BASE, BUS_SENDS_START, FX_SENDS_BASE, FX_SENDS_START, true, true);
      fx_1.init(base, TOTAL_BUS_TRACKS, TOTAL_FX_TRACKS, BUS_SENDS_BASE, BUS_SENDS_START, FX_SENDS_BASE, FX_SENDS_START, true, true);
      // fx_1.init(base, TOTAL_BUS_TRACKS, TOTAL_FX_TRACKS, BUS_SENDS_BASE, BUS_SENDS_START, FX_SENDS_BASE, FX_SENDS_START, true, true);

      // p2_in.init(TOTAL_BUS_TRACKS, TOTAL_FX_TRACKS, BUS_SENDS_BASE, BUS_SENDS_START, FX_SENDS_BASE, FX_SENDS_START);
      

      
      sliders_i = audio_in.setupSliders(base, sliders, sliders_i, BUS_FADERS_BASE);
      sliders_i = fx_1.setupSliders(base, sliders, sliders_i, BUS_FADERS_BASE);

      deci_mod.addParamDeviceMapper(base, "deci", FX_LAYER_ID ,0,0);
      // sliders_i = delays.setupSliders(base, sliders, sliders_i, BUS_FADERS_BASE);
      
      // audio_in.groupTrackBank.scrollBy(audio_in.start_index);  //scrollBy(this.start_index);
      // delays.groupTrackBank.scrollBy(delays.start_index);  //scrollBy(this.start_index);
      // HardwareButton
  
      // TODO: Perform your driver initialization here.
      // For now just show a popup notification for verification that it is running.
      base.host.showPopupNotification("OSC Controller for AWB Initialized");
      

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
      // if (this.hardwareSurface != null)
      //       this.hardwareSurface.updateHardware ();
      if (this.hardwareSurface != null)
            this.hardwareSurface.updateHardware ();
   }


}
