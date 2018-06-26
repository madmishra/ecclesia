scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/text", "scbase/loader!sc/plat/dojo/controller/ScreenController", "scbase/loader!extn/customScreen/search/extnShipmentSearch"], function(
_dojodeclare, _dojokernel, _dojotext, _scScreenController, _extnextnShipmentSearch) {
    return _dojodeclare("extn.customScreen.search.extnShipmentSearchInitController", [_scScreenController], {
        screenId: 'extn.customScreen.search.extnShipmentSearch',
        childControllers: [{
            screenId: 'extn.customScreen.search.extnShipmentSearchResult',
            controllerId: 'extn.customScreen.search.extnShipmentSearchResultInitController'
        }],
        mashupRefs: [{
            cached: 'PAGE',
            sourceNamespace: 'getOrderByList_output',
            mashupRefId: 'getOrderByList',
            sequence: '',
            mashupId: 'search_getAdvancedShipmentSearchOrderByList',
            callSequence: '',
            sourceBindingOptions: ''
        }, {
            cached: 'PAGE',
            sourceNamespace: 'getScacList_output',
            mashupRefId: 'getScacList',
            sequence: '',
            mashupId: 'shipmentSearch_fetchScacList',
            callSequence: '',
            sourceBindingOptions: ''
        }]
    });
});