scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/text", "scbase/loader!sc/plat/dojo/controller/ServerDataController", "scbase/loader!extn/customScreen/sort/SortScanPick"], function(
_dojodeclare, _dojokernel, _dojotext, _scServerDataController, _extnSortScanPick) {
    return _dojodeclare("extn.customScreen.sort.SortScanPickBehaviorController", [_scServerDataController], {
        screenId: 'extn.customScreen.sort.SortScanPick',
        mashupRefs: [{
            mashupId: 'backroomPick_getShipmentLineList',
            mashupRefId: 'getAllShipmentLineList'
        }, {
            mashupId: 'backroomPick_getNotPickedShipmentLineList',
            mashupRefId: 'getNotPickedShipmentLineList'
        }, {
            mashupId: 'backroomPick_getNotPickedShipmentLineList',
            mashupRefId: 'getNotPickedShipmentLineListOnNext'
        }, {
            mashupId: 'backroomPick_updateShipmentQuantity',
            mashupRefId: 'updateShipmentQuantityForPickAll'
        }, {
            mashupId: 'backroomPick_updateShipmentQuantity',
            mashupRefId: 'updateShipmentQuantityForPickAllLine'
        }, {
            mashupId: 'backroomPickUp_registerBarcodeForBackroomPick',
            mashupRefId: 'translateBarCode'
        }, {
            mashupId: 'backroomPickUp_changeShipmentStatusToReadyForCustomerPick',
            mashupRefId: 'saveShipmentStatusForPickUpOrder'
        }, {
            mashupId: 'backroomPickUp_changeShipmentStatusToReadyForPack',
            mashupRefId: 'saveShipmentStatusForShipOrder'
        }, {
            mashupId: 'backroomPickUp_changeShipmentStatusOnNext',
            mashupRefId: 'validateChangeShipmentStatusOnNext'
        }, {
            mashupId: 'backroomPickUp_getNotPickedShipmentLineListCount',
            mashupRefId: 'getNotPickedShipmentLineListCount'
        }]
    });
});