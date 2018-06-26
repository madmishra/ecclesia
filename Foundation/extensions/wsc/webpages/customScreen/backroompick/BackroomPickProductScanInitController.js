scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/text", "scbase/loader!sc/plat/dojo/controller/ScreenController", "scbase/loader!extn/customScreen/backroompick/BackroomPickProductScan"], function(
_dojodeclare, _dojokernel, _dojotext, _scScreenController, _extnBackroomPickProductScan) {
    return _dojodeclare("extn.customScreen.backroompick.BackroomPickProductScanInitController", [_scScreenController], {
        screenId: 'extn.customScreen.backroompick.BackroomPickProductScan',
        mashupRefs: [{
            sourceNamespace: 'backroomPickShipmentDetails_output',
            callSequence: '',
            mashupRefId: 'getShipmentDetailsByShipmentKey',
            sequence: '',
            sourceBindingOptions: '',
            mashupId: 'backroomPick_getShipmentDetailsByShipmentKey'
        }, {
            sourceNamespace: 'getShortageLines',
            callSequence: '',
            mashupRefId: 'getNotPickedShipmentLineListInit',
            sequence: '',
            sourceBindingOptions: '',
            mashupId: 'extn_getLinesForGiftWrap'
        }]
    });
});