package com.armedwithbow;

import com.bitwig.extension.controller.api.ControllerHost;
// import com.bitwig.extension.controller.api.Transport;
import com.bitwig.extension.controller.ControllerExtension;
// import java.util.HashMap;
//import com.bitwig.extension.api.Color;
import com.bitwig.extension.controller.api.HardwareSlider;
import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.MasterTrack;
import com.bitwig.extension.controller.api.DeviceBank;


/*
 *    TO RUN: mvn install
 */



public class OSCControllerforAWBExtension extends ControllerExtension
{
       
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

      final ExtensionBase base = new ExtensionBase( getHost(), TOTAL_BUS_TRACKS, TOTAL_FX_TRACKS, BUS_SENDS_START, FX_SENDS_START, BUS_SENDS_END);

      base.host.println("starting fresh");

      this.hardwareSurface = base.extensionHardwareSurface;

      ///////////////////////////////////
      // 0. Create the constants
      /////////////////////////////////

      
//ADD CONSTANTS TO BASE SO DONT HAVE TO KEEP PASSING
//CREATE INTERFACE GROUPS
//ADD-CREATE MODULES TO THEM
//SETUP SLIDERS (func on base)
//ADD-CREATE PARAM/MOD DEVICES
//MAKE SCENE DICT

      InterfaceGroup audio_in = new InterfaceGroup(base, "audio source lines", "mixed", 13, 0, true);
      InterfaceGroup fx_1 = new InterfaceGroup(base, "fx 1", "mixed", FX_TRACKBANK_START + base.TOTAL_FX_TRACKS, FX_TRACKBANK_START, true);


      audio_in.addModule("vc fx", 2, 1, new int[]{}, false, true, false); //vc + ubermod
      audio_in.addModule("vc in",  3,  0, new int[]{0, 1, 2, 3 }, true, false, false) ; //vc + ubermod
      audio_in.addModule("pro2", 5, 7, new int[]{16}, true, true, false) ; //p2

      
      
      audio_in.addModule("del_b1", 9,  2, new int[]{4}, true, true, false); 
      audio_in.addModule("del_a1",  10, 3, new int[]{5}, true, true, false); 
      audio_in.addModule("del_a2",  11, 4, new int[]{6}, true, true, false); 
      audio_in.addModule("del_b2",  12,  5, new int[]{7}, true, true, false); 


      fx_1.addModule( "splut", 0, 8, new int[]{9}, true, false , false); 
      fx_1.addModule( "sparkle", 1, 10, new int[]{11}, true, false, false); 
      fx_1.addModule( "deci", 2, 11, new int[]{12}, true, false, false); 
      fx_1.addModule("fuzz", 3, 12, new int[]{13}, true, false, false); 
      fx_1.addModule("verb", 4, 13, new int[]{14},true, false,false); 
      fx_1.addModule("mast", 5, 14, new int[]{15, 10, 8},true, false, false); 

      /////////////////////////////////////////////////////////////////////////////////////
      // 1. Create the controls

      
      // 1.1 Create input controls
      HardwareSlider[] sliders;
      sliders = new HardwareSlider[TOTAL_SLIDERS];
      int sliders_i = 0;
      
      audio_in.createSendMappings();
      fx_1.createSendMappings();
      // fx_1.init(base, TOTAL_BUS_TRACKS, TOTAL_FX_TRACKS, BUS_SENDS_BASE, BUS_SENDS_START, FX_SENDS_BASE, FX_SENDS_START, true, true);

      // p2_in.init(TOTAL_BUS_TRACKS, TOTAL_FX_TRACKS, BUS_SENDS_BASE, BUS_SENDS_START, FX_SENDS_BASE, FX_SENDS_START);
      

      
      sliders_i = audio_in.setupSliders(base, sliders, sliders_i);
      sliders_i = fx_1.setupSliders(base, sliders, sliders_i);

      
      FXLayerHandler vcFXBank = new FXLayerHandler(base, audio_in.modulesMap.get("vc fx").getTrackDeviceBank(1));

      InstrumentLayerHandler b1InstBank = new InstrumentLayerHandler(base, audio_in.modulesMap.get("del_b1").getTrackDeviceBank(3));
      InstrumentLayerHandler b2InstBank = new InstrumentLayerHandler(base, audio_in.modulesMap.get("del_b2").getTrackDeviceBank(2));
      InstrumentLayerHandler a1InstBank = new InstrumentLayerHandler(base, audio_in.modulesMap.get("del_a1").getTrackDeviceBank(2));
      InstrumentLayerHandler a2InstBank = new InstrumentLayerHandler(base, audio_in.modulesMap.get("del_a2").getTrackDeviceBank(2));

      FXLayerHandler sparkFXBank = new FXLayerHandler(base, fx_1.modulesMap.get("sparkle").getTrackDeviceBank(1)) ;
      FXLayerHandler splutterFXBank = new FXLayerHandler(base, fx_1.modulesMap.get("splut").getTrackDeviceBank(1)) ;
      MasterTrack masterTrack = base.host.createMasterTrack(0); // setting scenes - here. Change in future ??
      DeviceBank masterRootFXBank = masterTrack.createDeviceBank(3);
      FXLayerHandler masterFXBank1 = new FXLayerHandler(base, masterRootFXBank) ; // top level
      FXLayerHandler masterFXBank2 = new FXLayerHandler(base, masterFXBank1.getLayerDeviceBank(base,0,1, 0, 4)) ; //inner
      
      audio_in.modulesMap.get("del_b1").addRoutingDeviceMapper(base, audio_in.modulesMap.get("del_b1").getTrackDeviceBank(1).getItemAt(0));
      audio_in.modulesMap.get("del_b1").addParamDeviceMapper(base, "b1 compelx mod", audio_in.modulesMap.get("del_b1").getTrackDeviceBank(2).getItemAt(1),0);
      // audio_in.modulesMap.get("del_b1").addParamDeviceMapper(base, "b1 compelx mod",b1InstBank.getInstDevice(0),0);
      audio_in.modulesMap.get("del_b1").addParamDeviceMapper(base, "b1 enveloper",audio_in.modulesMap.get("del_b1").getTrackDeviceBank(3).getItemAt(2),1);


      audio_in.modulesMap.get("del_a1").addRoutingDeviceMapper(base, audio_in.modulesMap.get("del_a1").getTrackDeviceBank(1).getItemAt(0));
      audio_in.modulesMap.get("del_a1").addParamDeviceMapper(base, "a1 compelx mod",a1InstBank.getInstDevice(0),0);

      audio_in.modulesMap.get("del_a2").addRoutingDeviceMapper(base, audio_in.modulesMap.get("del_a2").getTrackDeviceBank(1).getItemAt(0));
      audio_in.modulesMap.get("del_a2").addParamDeviceMapper(base, "a2 compelx mod",a2InstBank.getInstDevice(0),0);

      audio_in.modulesMap.get("del_b2").addRoutingDeviceMapper(base, audio_in.modulesMap.get("del_b2").getTrackDeviceBank(1).getItemAt(0));
      audio_in.modulesMap.get("del_b2").addParamDeviceMapper(base, "b2 compelx mod",b2InstBank.getInstDevice(0),0);

      audio_in.modulesMap.get("vc fx").addParamDeviceMapper(base, "vc params", vcFXBank.getFXDevice(0), 0);
      
      fx_1.modulesMap.get("deci").addParamDeviceMapper(base, "deci", fx_1.modulesMap.get("deci").getTrackDeviceBank(1).getItemAt(0) ,0);

      fx_1.modulesMap.get("fuzz").addParamDeviceMapper(base, "fuzz", fx_1.modulesMap.get("fuzz").getTrackDeviceBank(1).getItemAt(0) ,0);
      
      fx_1.modulesMap.get("splut").addParamDeviceMapper(base, "splutter control", splutterFXBank.getLayerDeviceBank(base, 0, 1, 0, 1).getItemAt(0), 0);
      fx_1.modulesMap.get("splut").addModDeviceMapper(base, "splutter mod", splutterFXBank.getFXDevice(0), 0);

      fx_1.modulesMap.get("sparkle").addParamDeviceMapper(base, "sparkle control", sparkFXBank.getLayerDeviceBank(base, 0,1,0,1).getItemAt(0), 0);
      fx_1.modulesMap.get("sparkle").addModDeviceMapper(base, "sparkle mod", sparkFXBank.getFXDevice(0), 0);
      
      fx_1.modulesMap.get("verb").addParamDeviceMapper(base, "verb params", fx_1.modulesMap.get("verb").getTrackDeviceBank(1).getItemAt(0), 0);
      
      fx_1.modulesMap.get("mast").addParamDeviceMapper(base, "mast params", masterFXBank1.fxBank.getItemAt(0), 0);
      
      fx_1.modulesMap.get("mast").addParamDeviceMapper(base, "um control", masterFXBank2.getLayerDeviceBank(base, 1, 1, 0, 1).getItemAt(0), 1);
      fx_1.modulesMap.get("mast").addModDeviceMapper(base, "um mod", masterFXBank2.fxBank.getItemAt(1), 1);
      
      fx_1.modulesMap.get("mast").addParamDeviceMapper(base, "stut 2", masterFXBank1.fxBank.getItemAt(1), 2);
      
  

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

