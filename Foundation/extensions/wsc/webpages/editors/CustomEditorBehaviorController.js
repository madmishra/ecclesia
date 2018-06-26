scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/text", "scbase/loader!sc/plat/dojo/controller/ServerDataController", "scbase/loader!extn/editors/CustomEditorrEditor"], function(
_dojodeclare, _dojokernel, _dojotext, _scServerDataController, _extnCustomEditor) {
    return _dojodeclare("extn.editors.CustomEditorBehaviorController", [_scServerDataController], {
        screenId: 'extn.editors.CustomEditor',
        mashupRefs: []
    });
});