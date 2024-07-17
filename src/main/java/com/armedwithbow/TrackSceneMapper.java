package com.armedwithbow;

import com.bitwig.extension.controller.api.CursorDevice;
import com.bitwig.extension.controller.api.CursorRemoteControlsPage;
import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.DeviceBank;
import com.bitwig.extension.controller.api.DeviceMatcher;
import com.bitwig.extension.controller.api.HardwareButton;
import com.bitwig.extension.controller.api.Scene;
import com.bitwig.extension.controller.api.SceneBank;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;

import java.util.UUID;

import com.bitwig.extension.controller.api.AbsoluteHardwareControl;
import com.bitwig.extension.controller.api.AbsoluteHardwareKnob;
import com.bitwig.extension.controller.api.ClipLauncherSlot;
import com.bitwig.extension.controller.api.ClipLauncherSlotBank;
import com.bitwig.extension.controller.api.ClipLauncherSlotOrScene;
import com.bitwig.extension.controller.api.ClipLauncherSlotOrSceneBank;

public class TrackSceneMapper {

    // private final CursorDevice cursorDevice;
    // private final DeviceBank deviceBank;
    // private DeviceMatcher matcher;
    // private final Device device;
    // private final ClipLauncherSlotOrSceneBank slotBank;
    // private final ClipLauncherSlotBank slotBank;
    private int SCENE_SLOTS;
    // private ClipLauncherSlot[] sceneSlots;
    // private Scene[] sceneSlots;
    private HardwareButton[] sceneButtons;
    private TrackBank trackBank;
    private SceneBank sceneBank;
    // private ClipLauncherSlotOrScene

    // private ClipLauncherSlotOrScene currentScene;

    // DeviceControlMapper(ExtensionBase base, Track track, String name, String
    // deviceIdString, int matcherIndex, DynamicTrackModule mod, int MIDI_BASE_CC,
    // int deviceBankSize){
    TrackSceneMapper(ExtensionBase base, DynamicTrackModule mod, int bankSize) {
        // this.device = device;
        // base.host.println("creating mapper");

        // slotBank = mod.track.clipLauncherSlotBank();
        // cursorDevice.selectDevice(device);
        this.SCENE_SLOTS = bankSize;
        // this.SCENE_SLOTS = 10;

        sceneButtons = new HardwareButton[SCENE_SLOTS];
        trackBank = mod.track.createTrackBank(1, 0, SCENE_SLOTS, false);
        sceneBank = trackBank.sceneBank();

        for (int i = 0; i < SCENE_SLOTS; i++) {
            sceneButtons[i] = base.extensionHardwareSurface.createHardwareButton("BUTTON_SCN" + i);
            sceneButtons[i].pressedAction()
                    .setActionMatcher(base.scPortIn.createNoteOnActionMatcher(mod.midiChannel, i));

            sceneButtons[i].pressedAction().addBinding(sceneBank.getItemAt(i).launchAction());

        }
        ;

    }
}