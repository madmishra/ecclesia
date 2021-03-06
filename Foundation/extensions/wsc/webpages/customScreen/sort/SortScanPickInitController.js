scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/text", "scbase/loader!sc/plat/dojo/controller/ScreenController", "scbase/loader!extn/customScreen/sort/SortScanPick"], function(
_dojodeclare, _dojokernel, _dojotext, _scScreenController, _extnSortScanPick) {
    return _dojodeclare("extn.customScreen.sort.SortScanPickInitController", [_scScreenController], {
        screenId: 'extn.customScreen.sort.SortScanPick',
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
            mashupId: 'backroomPick_getNotPickedShipmentLineList'
        }
    ]
    });
});