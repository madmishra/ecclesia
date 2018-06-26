scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/text", "scbase/loader!sc/plat/dojo/controller/ServerDataController", "scbase/loader!extn/customScreen/search/extnShipmentDetails"], function(
_dojodeclare, _dojokernel, _dojotext, _scServerDataController, _extnextnShipmentDetails) {
    return _dojodeclare("extn.customScreen.search.extnShipmentDetailsBehaviorController", [_scServerDataController], {
        screenId: 'extn.customScreen.search.extnShipmentDetails',
        mashupRefs: [{
            mashupId: 'containerPack_unpackShipment',
            mashupRefId: 'containerPack_unpackShipment'
        }, {
            mashupId: 'containerPack_changeShipment',
            mashupRefId: 'containerPack_changeShipment'
        }, {
            mashupId: 'containerPack_changeShipmentStatus',
            mashupRefId: 'containerPack_changeShipmentStatus'
        }, {
            mashupId: 'backroomPick_updateShipmentQuantity',
            mashupRefId: 'changeShipmentToUpdateQty'
        }, {
            mashupId: 'backroomPick_changeShipmentStatus',
            mashupRefId: 'backroomPick_changeShipmentStatus'
        }, {
            mashupId: 'search_searchPickTicket',
            mashupRefId: 'searchPickTicket'
        }, {
            mashupId: 'common_getShipmentDetails',
            mashupRefId: 'getShipmentDetailsForBackroomPick'
        }, {
            mashupId: 'common_getShipmentDetails',
            mashupRefId: 'getShipmentDetailsForRecordCustomerPick'
        }, {
            mashupId: 'common_getShipmentDetails',
            mashupRefId: 'getShipmentDetailsForPack'
        }]
    });
});