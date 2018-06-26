scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/text", "scbase/loader!sc/plat/dojo/controller/ServerDataController", "scbase/loader!extn/editors/MyOrderEditor"], function(
_dojodeclare, _dojokernel, _dojotext, _scServerDataController, _extnMyOrderEditor) {
    return _dojodeclare("extn.editors.MyOrderEditorBehaviorController", [_scServerDataController], {
        screenId: 'extn.editors.MyOrderEditor',
        mashupRefs: []
    });
});