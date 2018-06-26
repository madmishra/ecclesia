scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/text", "scbase/loader!sc/plat/dojo/controller/ServerDataController", "scbase/loader!extn/customScreen/search/extnShipmentSearchResult"], function(
_dojodeclare, _dojokernel, _dojotext, _scServerDataController, _extnextnShipmentSearchResult) {
    return _dojodeclare("extn.customScreen.search.extnShipmentSearchResultBehaviorController", [_scServerDataController], {
        screenId: 'extn.customScreen.search.extnShipmentSearchResult',
        mashupRefs: [{
            mashupId: 'extn_shipmentSearch_fetchShipmentList',
            mashupRefId: 'extn_getListBehavior'
        }]
    });
});