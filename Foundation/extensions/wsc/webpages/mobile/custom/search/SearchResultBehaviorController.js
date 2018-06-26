scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/text", "scbase/loader!sc/plat/dojo/controller/ServerDataController", "scbase/loader!extn/mobile/custom/search/SearchResult"], function(
_dojodeclare, _dojokernel, _dojotext, _scServerDataController, _extnSearchResult) {
    return _dojodeclare("extn.mobile.custom.search.SearchResultBehaviorController", [_scServerDataController], {
        screenId: 'extn.mobile.custom.search.SearchResult',
        mashupRefs: [{
            mashupId: 'mobileHomeSearch_getShipmentList',
            mashupRefId: 'getShipmentListOnNext'
        }]
    });
});