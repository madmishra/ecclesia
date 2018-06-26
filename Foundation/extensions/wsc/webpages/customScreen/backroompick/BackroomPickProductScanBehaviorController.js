scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/text", "scbase/loader!sc/plat/dojo/controller/ServerDataController", "scbase/loader!extn/customScreen/backroompick/BackroomPickProductScan"], function (
    _dojodeclare, _dojokernel, _dojotext, _scServerDataController, _extnBackroomPickProductScan) {
    return _dojodeclare("extn.customScreen.backroompick.BackroomPickProductScanBehaviorController", [_scServerDataController], {
        screenId: 'extn.customScreen.backroompick.BackroomPickProductScan',
        mashupRefs: [{
            mashupId: 'backroomPick_getShipmentLineList',
            mashupRefId: 'getAllShipmentLineList'
        }, {
            mashupId: 'extn_getLinesForGiftWrap',
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
        },
        {
            mashupId: 'extn_changeShipmentStatus',
            mashupRefId: 'extn_printStatus'
        }]
    });
});