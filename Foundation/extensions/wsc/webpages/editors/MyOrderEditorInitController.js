scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/text", "scbase/loader!sc/plat/dojo/controller/ScreenController", "scbase/loader!extn/editors/MyOrderEditor"], function(
_dojodeclare, _dojokernel, _dojotext, _scScreenController, _extnMyOrderEditor) {
    return _dojodeclare("extn.editors.MyOrderEditorInitController", [_scScreenController], {
        screenId: 'extn.editors.MyOrderEditor'
    });
});