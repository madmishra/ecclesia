scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/text", "scbase/loader!sc/plat/dojo/controller/ScreenController", "scbase/loader!extn/mobile/custom/search/SearchResult"], function(
_dojodeclare, _dojokernel, _dojotext, _scScreenController, _extnSearchResult) {
    return _dojodeclare("extn.mobile.custom.search.SearchResultInitController", [_scScreenController], {
        screenId: 'extn.mobile.custom.search.SearchResult',
        mashupRefs: [{
            sourceNamespace: 'getShipmentList_output',
          
            mashupRefId: 'extn_mobile_getShipmentList_ref',
            
            mashupId: 'extn_mobile_getShipmentList'
        }]
    });
});