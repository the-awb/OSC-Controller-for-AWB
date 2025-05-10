This script creates a custom Hardware interface between Armed With Bow's supercollider / Open Sound Control devices

It's not a generalised script, but made specifically for a set up.

The hardware interface is used because it is the lowest level interface and get's the same prioritisation as the audio engine.

Due to the number of parameters being mapped, and the topology of the network, the design is idiosyncratic.

"Bus" refers to audio busses that are used to generate envelope follower data in supercollider for the dynamic interactive system.
These busses are originally sending audio from Bitwig to a motu 1248 and on to Supercollider.
This was because generating midi from envelope followers in Bitwig (in the normal interface) was chocking the and becoming on-responsive.
May-25 system update to try sending OSC messages, handling the message sending with a script to avoid chocking.

ExtensionBase 
// wrapper for host.
// stores constants to define number of BUS sends, FX sends, domains for midi messaging, midi ports, and oscConnection
--| InterfaceGroup
    // Stores a TrackGroup, and provides definitions for where the Group starts and extends
    --| DynamicTrackModule
        // Takes a Track from an InterfaceGroup and creates definitions for how it should behave
        //>> Whether FX / BUS sends are used
        //>> Associates BUS Tracks and enables Bus Fader control
        //>> Whether to try and find a ToolDevice to use for an envFollowerOscObserver, /
            sending OSC messages to replace audio out


Helpers
TrackSceneMapper
// Recieves a DynamicTrackModule and maps n scenes for remote control

InstrumentLayerHandler
// Recieves a DeviceBank and filters for InstrumentLayers.
// Provides convenient access for LayerBanks and individual devices from those layers

FXLayerHandler
// Recieves a DeviceBank and filters for FXLayers.
// Provides convenient access for LayerBanks and individual devices from those layers


DeviceControlMapper
// Takes a Device and maps it to the midi values for a given DynamicTrackModule and MIDIBASE

ToolDeviceHandler
// Recieves a DeviceBank and applies a filter for ToolDevice.
Makes specific toolDevices available by index

EnvFollowerObserver
// Recieves a Device and takes the first remoteControl as envelopeFollower data stream to ouput as OSC messages

SpecificDeviceBankHandler
// Wrapper for DeviceMatcher handling the 3 types of Device to match by id:
//>> 1 Bitwig,  2 vst2,  3 vst3
// Recieves a DeviceBank and makes the matched device available, as well as the matcher.


